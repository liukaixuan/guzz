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
package org.guzz.service.core;

import org.guzz.orm.sql.BindedCompiledSQL;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface DebugService {
	
	public static final String DEMON_NAME_PREFIX = "gz_demon_t_" ;
	
	public boolean isDebugMode() ;
	
	public boolean isLogSQL() ;
	
	public boolean isLogParams() ;
	
	/**
	 * measure the time of how long a sql spent to execute.
	 */
	public boolean isMeasureTime() ;
	
	public void onErrorProcess(String msg, Exception e) ;

	public void logSQL(String sql, long nanoTime) ;
	
	public void logSQL(String sql, Object[] params, long nanoTime) ;
	
	public void logSQL(String sql, int[] params, long nanoTime) ;
	
	/**
	 * log the sql to run.
	 */
	public void logSQL(BindedCompiledSQL bsql, long nanoTime) ;
	
	/**
	 * log the sql to run.
	 * 
	 * @param bsql
	 * @param sqlStatment raw sql statement
	 */
	public void logSQL(BindedCompiledSQL bsql, String sqlStatment, long nanoTime) ;
	
	/**
	 * log the batch operations.
	 * 
	 * @param sql
	 * @param repeatTimes batch size
	 * @param nanoTime how long cost?
	 */
	public void logBatch(String sql, int repeatTimes, long nanoTime) ;
	
}
