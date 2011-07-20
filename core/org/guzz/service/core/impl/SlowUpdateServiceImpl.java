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
import java.sql.SQLException;

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
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SlowUpdateServiceImpl extends AbstractService implements GuzzContextAware, SlowUpdateService {
	private static transient final Log log = LogFactory.getLog(SlowUpdateServiceImpl.class) ;
	
	public static final String QUEUE_MAX_SIZE = "queueSize" ;
	
	private TransactionManager tm ;
	
	private ObjectMappingManager omm ;
		
	protected UpdateToDBThread updateThread ;
	
	private int batchSize = 2048 ;
	private int queueSize = 20480 ;
	
	public void updateCount(String businessName, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc){
		tableCondition = tableCondition == null ? Guzz.getTableCondition() : tableCondition ;
		
		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) omm.getObjectMapping(businessName, tableCondition) ;
		
		if(mapping == null){
			throw new ORMException("unknown business:[" + businessName + "]") ;
		}
		
		String columnToUpdate = mapping.getColNameByPropNameForSQL(propToUpdate) ;
		
		if(columnToUpdate == null){
			throw new ORMException("unknown property:[" + propToUpdate + "], business name:[" + businessName + "]") ;
		}
		
		updateCount(mapping.getDbGroup().getPhysicsGroupName(tableCondition), mapping.getTable().getTableName(tableCondition), columnToUpdate, mapping.getTable().getPKColumn().getColNameForSQL(), pkValue, countToInc) ;
	}
	
	public void updateCount(Class domainClass, Object tableCondtion, String propToUpdate, Serializable pkValue, int countToInc){
		updateCount(domainClass.getName(), tableCondtion, propToUpdate, pkValue, countToInc) ;
	}
	
	public void updateCount(String dbGroup, String tableName, String columnToUpdate, String pkColName, Serializable pkValue, int countToInc) {
		if(!isAvailable()){
			throw new GuzzException("slowUpdateService is not available. use the config server's [slowUpdate] to active this service.") ;
		}
		
		IncUpdateBusiness ut = new IncUpdateBusiness(dbGroup) ;
		
		ut.setTableName(tableName) ;
		ut.setPkColunName(pkColName) ;
		
		ut.setColumnToUpdate(columnToUpdate) ;
		ut.setPkValue(pkValue.toString()) ;
		ut.setCountToInc(countToInc) ;
		
		this.updateThread.addToQueue(ut) ;
		
		if(this.updateThread.isSleeping()){
			synchronized (updateThread) {
				try{
					this.updateThread.notify() ;
				}catch(Exception e){}
			}
		}
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			//TODO: 将此类设计成delegate模式，如果没有配置按照直接更新数据库处理。
			
			//没有配置此项，不启动。
			return false;
		}
		
		ServiceConfig sc = scs[0] ;
		
		String m_queueSize = (String) sc.getProps().get(QUEUE_MAX_SIZE) ;
		String m_batchSize = (String) sc.getProps().get("batchSize") ;
		
		this.queueSize = StringUtil.toInt(m_queueSize, this.queueSize) ;
		this.batchSize = StringUtil.toInt(m_batchSize, this.batchSize) ;
		
		return true ;
	}
	
	public void startup() {
		//启动更新线程
		if(updateThread == null){
			updateThread = new UpdateToDBThread(this.queueSize) ;
			updateThread.start() ;
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
		
		public UpdateToDBThread(int queueSize){
			super("slowUpdateThread", queueSize) ;
		}
		
		protected boolean doWithTheQueue() throws SQLException{			
			if(queues == null){
				return false ;
			}
			
			int length = queues.length ;			
			boolean processSomething = false ;
								
			WriteTranSession tran = null ;
			IncUpdateBusiness ut = null ;
			
			try{
				ObjectBatcher batcher = null ;
				int addedCount = 0 ;
				
				for(int i = 0 ; i < length; i++){
					ut = (IncUpdateBusiness) queues[i] ;
					if(ut == null) continue ;
					queues[i] = null ; //释放队列空间
										
					processSomething = true ;
					
					if(tran == null){
						tran = tm.openRWTran(false) ;
					}
					if(batcher == null){
						batcher = tran.createObjectBatcher() ;
					}
					
					batcher.insert(ut) ;
					addedCount++ ;
					
					if(addedCount >= batchSize){
						batcher.executeBatch() ;
						tran.commit() ;
						
						addedCount = 0 ;
					}
				}
				
				if(addedCount > 0){
					batcher.executeBatch() ;
					tran.commit() ;
				}
			}catch(Exception e){
				log.error(ut, e) ;
				
				if(tran != null){
					tran.rollback() ;
				}
			}finally{
				if(tran != null){
					tran.close() ;
				}
			}
			
			return processSomething ;
		}
		
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.tm = guzzContext.getTransactionManager() ;
		this.omm = guzzContext.getObjectMappingManager() ;
	}
}










