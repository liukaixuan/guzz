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

import org.guzz.Service;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.service.ProxyService;
import org.guzz.service.core.DebugService;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DebugServiceProxy extends ProxyService implements DebugService {
	
	public DebugServiceProxy(DebugService debugServiceImpl){
		this.debugServiceImpl = debugServiceImpl ;
	}
	
	private DebugService debugServiceImpl ;

	public boolean isDebugMode() {
		return debugServiceImpl.isDebugMode() ;
	}

	public boolean isLogSQL() {
		return debugServiceImpl.isLogSQL() ;
	}

	public boolean isMeasureTime() {
		return debugServiceImpl.isMeasureTime() ;
	}

	public boolean isLogParams() {
		return debugServiceImpl.isLogParams() ;
	}

	public void logSQL(String sql, long nanoTime) {
		debugServiceImpl.logSQL(sql, nanoTime) ;
	}

	public void logSQL(String sql, Object[] params, long nanoTime) {
		debugServiceImpl.logSQL(sql, params, nanoTime) ;
	}

	public void logSQL(String sql, int[] params, long nanoTime) {
		debugServiceImpl.logSQL(sql, params, nanoTime) ;
	}

	public void logSQL(BindedCompiledSQL bsql, long nanoTime) {
		debugServiceImpl.logSQL(bsql, nanoTime) ;
	}

	public void logSQL(BindedCompiledSQL bsql, String sqlStatment, long nanoTime) {
		debugServiceImpl.logSQL(bsql, sqlStatment, nanoTime) ;
	}
	
	public void logBatch(String sql, int repeatTimes, long nanoTime) {
		debugServiceImpl.logBatch(sql, repeatTimes, nanoTime) ;
	}

	public void onErrorProcess(String msg, Exception e) {
		debugServiceImpl.onErrorProcess(msg, e) ;
	}
	
	public void setServiceImpl(Service service){
		this.debugServiceImpl = (DebugService) service ;
	}

	public Service getServiceImpl() {
		return (Service) debugServiceImpl ;
	}

}
