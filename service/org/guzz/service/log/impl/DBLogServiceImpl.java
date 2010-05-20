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
package org.guzz.service.log.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.log.LogService;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;
import org.guzz.util.thread.DemonQueuedThread;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * 将日志对象写入日志数据库。{@link DBLogServiceImpl}只需要连接日志数据库主库，不需要从数据库，也不需要业务数据库任何资料。
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DBLogServiceImpl extends AbstractService implements LogService, GuzzContextAware{
	private static transient final Log log = LogFactory.getLog(DBLogServiceImpl.class) ;
		
	protected GuzzContext guzzContext ;
	
	protected TransactionManager tm ;
	
	protected DBLogThread updateThread ;
	
	private int commitSize = 2048 ;
	private int queueSize = 20480 ;

	public boolean configure(ServiceConfig[] scs) {			
		if(scs == null || scs.length == 0){
			//没有配置此项，不启动。
			log.warn("slowUpdateServer is not started. no configuration found.") ;
			return false;
		}
		
		ServiceConfig sc = scs[0] ;
		
		String m_commitSize = (String) sc.getProps().get("commitSize") ;
		String m_queueSize = (String) sc.getProps().get("queueSize") ;
		
		this.commitSize = StringUtil.toInt(m_commitSize, this.commitSize) ;
		this.queueSize = StringUtil.toInt(m_queueSize, this.queueSize) ;
		
		return true ;
	}

	public void startup(){				
		//启动更新线程
		if(updateThread == null){
			updateThread = new DBLogThread(queueSize) ;
			updateThread.start() ;
		}			
	}

	public void log(Object logObject) {
		updateThread.addToQueue(new LogObject(logObject, Guzz.getTableCondition())) ;
	}
	
	public void log(Object logObject, Object tableCondition) {
		updateThread.addToQueue(new LogObject(logObject, tableCondition)) ;
	}

	public void shutdown() {
		if(updateThread != null){
			updateThread.shutdown() ;
			updateThread = null ;
		}
	}
	
	static class LogObject{
		
		public LogObject(Object logObject, Object tableCondition){
			this.logObject = logObject ;
			this.tableCondition = tableCondition ;
		}
		
		public Object logObject ;
		
		public Object tableCondition ;
	}
	
	class DBLogThread extends DemonQueuedThread {
				
		public DBLogThread(int maxQueueSize){
			super("dbLogClientThread", maxQueueSize) ;
		}
		
		protected boolean doWithTheQueue() throws Exception{
			WriteTranSession tran = null ;
			boolean doSomething = false ;
			
			try{
				int addedCount = 0 ;
				
				for(int i = 0 ; i < this.queues.length ; i++){
					LogObject log = (LogObject) this.queues[i] ;
					if(log == null) continue ;
					this.queues[i] = null ;
					
					if(tran == null){
						tran = tm.openRWTran(false) ;
					}
					
					doSomething = true ;
					
					Guzz.setTableCondition(log.tableCondition) ;
					tran.insert(log.logObject) ;
					addedCount++ ;
					
					if(addedCount >= commitSize){
						tran.commit() ;
						
						addedCount = 0 ;
					}
				}
				
				if(addedCount > 0){
					tran.commit() ;
				}
			}catch(Exception e){
				if(tran != null){
					tran.rollback() ;
				}
				throw e ;
			}finally{
				if(tran != null){
					tran.close() ;
				}
			}
			
			return doSomething ;
		}
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.tm = guzzContext.getTransactionManager() ;
	}

	public boolean isAvailable() {
		return updateThread != null;
	}
	
}
