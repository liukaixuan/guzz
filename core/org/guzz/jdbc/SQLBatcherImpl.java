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
package org.guzz.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.service.core.DebugService;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SQLBatcherImpl extends AbstractBatcher implements SQLBatcher {

	private final DebugService debugService ;
	private final PreparedStatement ps ;
	private final Dialect dialect ;
	private final CompiledSQL cs ;
	private final String rawSQL ;
	private int objectsCountInBatch ;
	
	public SQLBatcherImpl(DebugService debugService, PreparedStatement ps, String rawSQL, Dialect dialect, CompiledSQL cs){
		super(dialect.getDefaultBatchSize()) ;
		
		this.debugService = debugService ;
		this.ps = ps ;
		this.rawSQL = rawSQL ;
		this.dialect = dialect ;
		this.cs = cs ;
	}
	
	public void addNewBatchParams(Map params) {
		checkAndAutoExecuteBatch(this.objectsCountInBatch) ;
		
		BindedCompiledSQL bsql = cs.bind(params) ;
		try {
			bsql.prepareNamedParams(dialect, ps) ;
			ps.addBatch() ;
			
		} catch (SQLException e) {
			throw new DaoException("error add batch params:[" + params + "]. CompiledSQL is:" + cs, e) ;
		}
		
		objectsCountInBatch++ ;
	}

	public void addNewBatchParams(String paramName, int paramValue) {
		checkAndAutoExecuteBatch(this.objectsCountInBatch) ;
		
		BindedCompiledSQL bsql = cs.bind(paramName, paramValue) ;
		try {
			bsql.prepareNamedParams(dialect, ps) ;
			ps.addBatch() ;
			
		} catch (SQLException e) {
			throw new DaoException("error add batch params:[" + paramName + "=" + paramValue + "]. CompiledSQL is:" + cs, e) ;
		}
		
		objectsCountInBatch++ ;
	}

	public void addNewBatchParams(String paramName, Object paramValue) {
		checkAndAutoExecuteBatch(this.objectsCountInBatch) ;
		
		BindedCompiledSQL bsql = cs.bind(paramName, paramValue) ;
		try {
			bsql.prepareNamedParams(dialect, ps) ;
			ps.addBatch() ;
			
		} catch (SQLException e) {
			throw new DaoException("error add batch params:[" + paramName + "=" + paramValue + "]. CompiledSQL is:" + cs, e) ;
		}
		
		objectsCountInBatch++ ;
	}

	public void clearBatch() {
		try {
			ps.clearBatch() ;
		} catch (SQLException e) {
			throw new DaoException("error execute clearBatch. CompiledSQL is:" + cs, e) ;
		}
		
		this.objectsCountInBatch = 0 ;
	}

	public int[] executeBatch() {
		if(this.objectsCountInBatch == 0) return new int[0] ;
		
		boolean measureTime = this.debugService.isMeasureTime() ;
		long startTime = 0L ;
		if(measureTime){
			startTime = System.nanoTime() ;
		}
		
		try {
			int[] affectedRows = ps.executeBatch() ;
			
			if(this.debugService.isLogSQL()){
				long timeCost = 0 ;
				if(measureTime){
					timeCost = System.nanoTime() - startTime ;
				}
				
				this.debugService.logBatch(this.rawSQL, this.objectsCountInBatch, timeCost) ;
			}
			
			return affectedRows ;
		} catch (SQLException e) {
			throw new DaoException("error execute batch update. CompiledSQL is:" + cs, e) ;
		}
	}
	
}
