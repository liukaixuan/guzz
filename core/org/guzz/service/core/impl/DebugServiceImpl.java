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
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.service.ServiceConfig;
import org.guzz.service.core.DebugService;
import org.guzz.util.ArrayUtil;

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

	public boolean isDebugMode() {
		return isDebugMode;
	}
	
	public boolean isLogParams(){
		return printSQLParams ;
	}
	
	protected boolean isDemonThread(){
		Thread t = Thread.currentThread() ;
		String name =t.getName() ;
		if(name == null) return false ;
		
		return name.startsWith(DebugService.DEMON_NAME_PREFIX) ;
	}

	public void logSQL(String sql, Object[] params) {
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQLParams){
			logInfo("sql:[" + sql + "], params is:[" + ArrayUtil.arrayToString(params) + "]") ;
		}else if(printSQL){
			logInfo("sql:[" + sql + "]") ;
		}
	}
	
	public void logSQL(BindedCompiledSQL bsql) {
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		CompiledSQL sql = bsql.getCompiledSQL() ;
		
		if(printSQLParams){
			logInfo("sql:[" + sql.getSql() + "], params is:[" + bsql.getBindedParams() + "]") ;
		}else if(printSQL){
			logInfo("sql:[" + sql.getSql() + "]") ;
		}
	}
	
	public void logSQL(BindedCompiledSQL bsql, String sqlStatment){
		if(this.ignoreDemonThreadSQL && isDemonThread()){
			return ;
		}
		
		if(printSQLParams){
			logInfo("sql:[" + sqlStatment + "], params is:[" + bsql.getBindedParams() + "]") ;
		}else if(printSQL){
			logInfo("sql:[" + sqlStatment + "]") ;
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
		String result = "guzz debug info:" ;
		
		result += "debugMode:" + this.isDebugMode ;
		result += ",logOnError:" + this.logOnError ;
		result += ",haltOnError:" + this.haltOnError ;
		result += ",printSQL:" + this.printSQL ;
		result += ",printSQLParams:" + this.printSQLParams ;	
		
		logInfo(result) ;
	}
	
	protected void resetToDefaultConfig(){
		isDebugMode = false ;
		haltOnError = false ;
		logOnError = true ;
		printSQL = false ;
		printSQLParams = false ;
	}
	
	protected void logInfo(String msg){
		if(log.isInfoEnabled()){
			log.info(msg) ;
		}else if(log.isErrorEnabled()){
			log.error(msg) ;
		}else if(log.isFatalEnabled()){
			log.fatal(msg) ;
		}else{
			System.out.println(msg);
		}
	}
	
	protected void logInfo(String msg, Throwable e){
		if(log.isInfoEnabled()){
			log.info(msg, e) ;
		}else if(log.isErrorEnabled()){
			log.error(msg, e) ;
		}else if(log.isFatalEnabled()){
			log.fatal(msg, e) ;
		}else{
			System.err.println(msg);
			e.printStackTrace(System.err) ;
		}
	}

	public void startup() {
	}

}
