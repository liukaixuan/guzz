/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.guzz.service.core.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.exception.GuzzException;
import org.guzz.exception.ORMException;
import org.guzz.jdbc.ObjectBatcher;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.SlowUpdateService;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;
import org.guzz.util.thread.DemonQueuedThread;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * 在写入前进行操作合并的update服务。
 * <p/>
 * 进行数据合并时，将创建Map保存临时对象，更新数据一般不会出现错误和丢失（无锁操作，不能确保不出任何差错）；但数据全部存放在内存，如果项目较多可能出现内存不足。
 * <p/>
 * 建议用在更新项目较少，但更新频率非常高的场景下。
 * <p/>
 * 实现方式：
 * <lo>
 * <li>获取到更新操作后，根据key从Map中读取以往的操作</li>
 * <li>如果操作存在，增加本次操作的count值；如果不存在，当前对象写入Map中。</li>
 * <li>后台进程获取更新Map，并创建新的Map供新的update操作使用，保证当前处理的Map不再更新。</li>
 * <li>后台进程遍历Map，并将操作写入临时数据库。</li>
 * <li>后台进行休眠@param updateInterval 毫秒，重复执行。</li>
 * </lo>
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SuperSlowUpdateServiceImpl extends AbstractService implements GuzzContextAware, SlowUpdateService {
	private static transient final Log log = LogFactory.getLog(SuperSlowUpdateServiceImpl.class) ;
		
	private Map updateOperations = new HashMap(2048) ;
		
	private TransactionManager tm ;
	
	private ObjectMappingManager omm ;
		
	protected UpdateToDBThread updateThread ;
	
	private int batchSize = 2048 ;
	
	/**后台更新频率，默认500毫秒。*/
	private int updateInterval = 500 ;
	
	private Object insertLock = new Object() ;
	
	public void updateCount(String businessName, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc){
		tableCondition = tableCondition == null ? Guzz.getTableCondition() : tableCondition ;
		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) omm.getObjectMapping(businessName, tableCondition) ;
		
		if(mapping == null){
			throw new ORMException("unknown business:[" + businessName + "]") ;
		}
		
		String columnToUpdate = mapping.getColNameByPropName(propToUpdate) ;
		
		if(columnToUpdate == null){
			throw new ORMException("unknown property:[" + propToUpdate + "], business name:[" + businessName + "]") ;
		}		
		
		updateCount(mapping.getDbGroup().getGroupName(), mapping.getTable().getTableName(tableCondition), columnToUpdate, mapping.getTable().getPKColName(), pkValue, countToInc) ;
	}
	
	public void updateCount(Class domainClass, Object tableCondtion, String propToUpdate, Serializable pkValue, int countToInc){
		updateCount(domainClass.getName(), tableCondtion, propToUpdate, pkValue, countToInc) ;
	}
	
	public void updateCount(String dbGroup, String tableName, String columnToUpdate, String pkColName, Serializable pkValue, int countToInc) {
		if(!isAvailable()){
			throw new GuzzException("superSlowUpdateService is not available. use the config server's [slowUpdate] to active this service.") ;
		}
		
		//key
		StringBuffer sb = new StringBuffer(32) ;		
		sb.append(pkValue)
		  .append(columnToUpdate) 
		  .append(tableName)
		  .append(dbGroup) ;
		
		String key = sb.toString() ;
		
		IncUpdateBusiness ut = (IncUpdateBusiness) this.updateOperations.get(key) ;
		if(ut != null){
			ut.incCount(countToInc) ;
		}else{
			synchronized(insertLock){
				//read again
				ut = (IncUpdateBusiness) this.updateOperations.get(key) ;
				
				if(ut != null){//already created while waiting for the lock
					//do nothing
				}else{
					ut = new IncUpdateBusiness(dbGroup) ;
					
					//put it into the map as soon as possible. 
					this.updateOperations.put(key, ut) ;
				}
			}
			
			//release lock as soon as possible.
			ut.setTableName(tableName) ;
			ut.setPkColunName(pkColName) ;
			
			ut.setColumnToUpdate(columnToUpdate) ;
			ut.setPkValue(pkValue.toString()) ;
			
			//use this thread safe method. other thread maybe has already changed the count value.
			ut.incCount(countToInc) ;
		}
		
		//just let him sleep, the delay is fine. we would like more batch performance.
//		if(this.updateThread.isSleeping()){
//			synchronized (updateThread) {
//				try{
//					this.updateThread.notify() ;
//				}catch(Exception e){}
//			}
//		}
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			//TODO: 将此类设计成delegate模式，如果没有配置按照直接更新数据库处理。
			
			//没有配置此项，不启动。
			return false;
		}
		
		ServiceConfig sc = scs[0] ;
		
		String m_batchSize = (String) sc.getProps().get("batchSize") ;
		String m_updateInterval = (String) sc.getProps().get("updateInterval") ;
		
		this.batchSize = StringUtil.toInt(m_batchSize, this.batchSize) ;
		this.updateInterval = StringUtil.toInt(m_updateInterval, this.updateInterval) ;
		
		return true ;
	}
	
	public void startup() {		
		//启动更新线程
		if(updateThread == null){
			updateThread = new UpdateToDBThread() ;
			updateThread.start() ;
			
			log.info("super slow update service started.") ;
		}
	}
	
	public void shutdown() {		
		if(updateThread != null){
			updateThread.shutdown() ;
			updateThread = null ;
		}
	}

	public boolean isAvailable() {
		return this.tm != null && updateThread != null;
	}
	
	class UpdateToDBThread extends DemonQueuedThread{
		
		public UpdateToDBThread(){
			super("superSlowUpdateThread", 0) ;
		}
		
		protected boolean doWithTheQueue() throws Exception{
			Map oldOperations = updateOperations ;
			
			if(oldOperations == null || oldOperations.isEmpty()){
				return false ;
			}
			
			//将操作Map挪出来，同时生成1个新的Map供update操作计算增量。
			//在生成新的Map的时候，将旧Map中count != 0的记录（认为更新频繁记录）提前存放进去，避免在update操作发现没有此记录，从而多线程并发生成，相互覆盖，造成数据不准确。
			HashMap newMap = new HashMap(2048) ;
		
			/*
			 * 20091231 BUG注意：最初设计没有使用同步，寄希望于 “就算抛出异常，抛出异常后本方法等待interval毫秒后还会重新执行，不影响使用。因此没有必要浪费同步资源”。
			 * 但在实际使用中发现：高并发下，的确可能出现ConcurrentModifyException，而一旦出现ConcurrentModifyException，就会重复的抛出此异常，并导致CPU资源全部耗尽；
			 * CPU上升到150%以上，系统无法继续工作，造成宕机。具体原因不详，推测是JDK HashMap的bug。
			 */
			synchronized(insertLock){
				Iterator it = oldOperations.entrySet().iterator() ;
				
				while(it.hasNext()){
					Map.Entry e = (Entry) it.next() ; //
					String key = (String) e.getKey() ;				
					IncUpdateBusiness value = (IncUpdateBusiness) e.getValue() ;
					
					if(value.getCountToInc() != 0){
						IncUpdateBusiness newValue = new IncUpdateBusiness() ;
						newValue.setColumnToUpdate(value.getColumnToUpdate()) ;
						newValue.setCountToInc(0) ;
						newValue.setDbGroup(value.getDbGroup()) ;
						newValue.setId(value.getId()) ;
						newValue.setPkColunName(value.getPkColunName()) ;
						newValue.setPkValue(value.getPkValue()) ;
						newValue.setTableName(value.getTableName()) ;
						
						newMap.put(key, newValue) ;
					}
				}
			}
			
			//使用新的Map
			WriteTranSession tran = tm.openRWTran(false) ; //如果数据库连接打开失败，旧的数据保持不变，避免数据丢失。
			updateOperations = newMap ;
			
			
			//旧的Map中的值，可能某些线程已经获取到，但由于CPU调度还没有执行incCount操作。执行其他代码等待一下所有旧记录不在变化。
			Iterator i = oldOperations.values().iterator() ;			
			ObjectBatcher batcher = null ;
			int addedCount = 0 ;
			IncUpdateBusiness ut = null ;
			
			try{
				while(i.hasNext()){
					ut = (IncUpdateBusiness) i.next() ;
					if(ut == null) continue ;
					
					//执行一次同步等待，使得没有来得及完成CPU指令周期的更新操作得以执行。
					ut.incCount(0) ;
					
					if(ut.getCountToInc() == 0) continue ;
					
					if(batcher == null){
						batcher = tran.createObjectBatcher() ;
					}
					
					batcher.insert(ut) ;
					addedCount++ ;
					
					if(addedCount >= batchSize){
						batcher.executeUpdate() ;
						tran.commit() ;
						
						addedCount = 0 ;
					}
				}
				
				if(addedCount > 0){
					batcher.executeUpdate() ;
					tran.commit() ;
				}
			}catch(Exception e){
				log.error(ut, e) ;
				//成功1个是1个，不进行回滚。
			}finally{
				if(tran != null){
					tran.close() ;
				}
			}
			
			oldOperations.clear() ;
			oldOperations = null ;
			
			//force sleep to reduce confict.
			return false ;
		}

		protected int getMillSecondsToSleep() {
			return updateInterval ;
		}
		
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.tm = guzzContext.getTransactionManager() ;
		this.omm = guzzContext.getObjectMappingManager() ;
	}
}










