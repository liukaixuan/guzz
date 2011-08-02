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

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Service;
import org.guzz.exception.GuzzException;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.service.AbstractService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.DebugService;
import org.guzz.util.ArrayUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DebugServiceImpl extends AbstractService implements DebugService, Service {
	private transient static final Log log = LogFactory.getLog(DebugServiceImpl.class) ;
		
	private boolean isDebugMode = false ;
	
	private boolean haltOnError = false ;

	private boolean logOnError = true ;
	
	private boolean printSQL = false ;
	
	private boolean printSQLParams = false ;
	
	/**是否忽略后台线程执行的SQL语句*/
	private boolean ignoreDemonThreadSQL = false ;
	
	private boolean measureTime = false ;
	
	private long onlySlowSQLInNano = 0L ;

	public boolean isDebugMode() {
		return isDebugMode;
	}

	public boolean isLogSQL() {
		return printSQL ;
	}

	public boolean isMeasureTime() {
		return measureTime ;
	}
	
	public boolean isLogParams(){
		return printSQLParams ;
	}
	
	protected boolean isDemonThread(){
		Thread t = Thread.currentThread() ;
		
		return t.isDaemon() ;
		
//		String name =t.getName() ;
//		if(name == null) return false ;
//		
//		return name.startsWith(DebugService.DEMON_NAME_PREFIX) ;
	}
	
	public void logSQL(String sql, long nanoTime){
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQL && nanoTime >= this.onlySlowSQLInNano){
			StringBuffer sb = new StringBuffer() ;
			sb.append("sql:[")
			  .append(sql)
			  .append("], timeCost:[")
			  .append(nanoTime)
			  .append("ns]") ;
			
			logInfo(sb.toString()) ;
		}
	}

	public void logBatch(String sql, int repeatTimes, long nanoTime) {
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQL && nanoTime >= this.onlySlowSQLInNano){
			StringBuffer sb = new StringBuffer() ;
			sb.append("batch sql:[")
			  .append(sql)
			  .append("], repeatTimes:[")
			  .append(repeatTimes)
			  .append("], timeCost:[")
			  .append(nanoTime)
			  .append("ns]") ;
			
			logInfo(sb.toString()) ;
		}
	}

	public void logSQL(String sql, Object[] params, long nanoTime) {
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQL && nanoTime >= this.onlySlowSQLInNano){
			StringBuffer sb = new StringBuffer() ;
			sb.append("sql:[")
			  .append(sql) ;
			
			if(printSQLParams){
			  sb.append("], params is:[")
			  .append(ArrayUtil.arrayToString(params)) ;
			}
			
			sb.append("], timeCost:[")
			  .append(nanoTime)
			  .append("ns]") ;
			
			logInfo(sb.toString()) ;
		}
	}
	
	public void logSQL(String sql, int[] params, long nanoTime) {
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQL && nanoTime >= this.onlySlowSQLInNano){
			StringBuffer sb = new StringBuffer() ;
			sb.append("sql:[")
			  .append(sql) ;
			
			if(printSQLParams){
			  sb.append("], params is:[")
			  .append(ArrayUtil.arrayToString(params)) ;
			}
			
			sb.append("], timeCost:[")
			  .append(nanoTime)
			  .append("ns]") ;
			
			logInfo(sb.toString()) ;
		}
	}
	
	public void logSQL(BindedCompiledSQL bsql, long nanoTime) {
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQL && nanoTime >= this.onlySlowSQLInNano){
			StringBuffer sb = new StringBuffer() ;
			sb.append("sql:[")
			  .append(bsql.getSQLToRun()) ;
			
			if(printSQLParams){
			  sb.append("], params is:[")
			  .append(bsql.getBindedParams()) ;
			}
			
			sb.append("], timeCost:[")
			  .append(nanoTime)
			  .append("ns]") ;
			
			logInfo(sb.toString()) ;
		}
	}
	
	public void logSQL(BindedCompiledSQL bsql, String sqlStatment, long nanoTime){
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQL && nanoTime >= this.onlySlowSQLInNano){
			StringBuffer sb = new StringBuffer() ;
			sb.append("sql:[")
			  .append(sqlStatment) ;
			
			if(printSQLParams){
			  sb.append("], params is:[")
			  .append(bsql.getBindedParams()) ;
			}
			
			sb.append("], timeCost:[")
			  .append(nanoTime)
			  .append("ns]") ;
			
			logInfo(sb.toString()) ;
		}
	}

	public void onErrorProcess(String msg, Exception e) {
		if(this.logOnError){
			this.logInfo(msg, e) ;
		}
		
		if(this.haltOnError){
			throw new GuzzException(msg, e) ;
		}
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			if(log.isInfoEnabled()){
				log.info("[guzzDebug] not found. alter system to production mode.") ;
			}
			
			resetToDefaultConfig() ;
			printGuzzDebugInfo() ;
			
			return true ;
		}
		
		Properties prop = scs[0].getProps() ;
		
		this.isDebugMode = "debug".equalsIgnoreCase(prop.getProperty("runMode")) ;
		this.haltOnError = "halt".equalsIgnoreCase(prop.getProperty("onError")) ;
		this.logOnError = "log".equalsIgnoreCase(prop.getProperty("onError")) ;
		this.printSQL = "true".equalsIgnoreCase(prop.getProperty("printSQL")) ;
		this.printSQLParams = "true".equalsIgnoreCase(prop.getProperty("printSQLParams")) ;
		this.ignoreDemonThreadSQL = "true".equalsIgnoreCase(prop.getProperty("ignoreDemonThreadSQL")) ;
		this.measureTime = "true".equalsIgnoreCase(prop.getProperty("measureTime")) ;
		
		String ms = prop.getProperty("onlySlowSQLInMillSeconds") ;
		
		if(StringUtil.notEmpty(ms)){
			this.onlySlowSQLInNano = Long.parseLong(ms) * 1000000 ;
		}
		
		printGuzzDebugInfo() ;
		
		return true ;
	}

	public boolean isAvailable() {
		return true;
	}

	public void shutdown() {
		resetToDefaultConfig() ;
	}
	
	protected void printGuzzDebugInfo(){
		if(log.isInfoEnabled()){
			String result = "guzz debug info:" ;
			
			result += "debugMode:" + this.isDebugMode ;
			result += ",logOnError:" + this.logOnError ;
			result += ",haltOnError:" + this.haltOnError ;
			result += ",printSQL:" + this.printSQL ;
			result += ",printSQLParams:" + this.printSQLParams ;	
			result += ",measureTime:" + this.measureTime ;
			result += ",onlySlowSQLInNano:" + this.onlySlowSQLInNano ;
			
			log.info(result) ;
		}
	}
	
	protected void resetToDefaultConfig(){
		isDebugMode = false ;
		haltOnError = false ;
		logOnError = true ;
		printSQL = false ;
		printSQLParams = false ;
		measureTime = false ;
		onlySlowSQLInNano = 0L ;
	}
	
	protected void logInfo(String msg){
		if(log.isInfoEnabled()){
			log.info(msg) ;
		}else{
			//如果log4j info级别无法输出，直接按照System.out输出，不在自动调高级别按warning，error等打印。避免引起维护人员对程序出错的误判。
			System.out.println(msg);
		}
	}
	
	protected void logInfo(String msg, Throwable e){
		if(log.isInfoEnabled()){
			log.info(msg, e) ;
		}else{
			//如果log4j info级别无法输出，直接按照System.out输出，不在自动调高级别按warning，error等打印。避免引起维护人员对程序出错的误判。
			
			System.err.println(msg);
			e.printStackTrace(System.err) ;
		}
	}

	public void startup() {
	}

}
