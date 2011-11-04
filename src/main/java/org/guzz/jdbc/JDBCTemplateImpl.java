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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.exception.JDBCException;
import org.guzz.orm.sql.SQLQueryCallBack;
import org.guzz.orm.type.SQLDataType;
import org.guzz.service.core.DebugService;
import org.guzz.transaction.AbstractTranSessionImpl;
import org.guzz.util.CloseUtil;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class JDBCTemplateImpl implements JDBCTemplate{
	
	protected final Dialect dialect ;
	
	protected final Connection conn ;
	
	protected final boolean isReadonly ;
	
	protected final DebugService debugService ;
	
	protected final AbstractTranSessionImpl tranSessionImpl ;
	
	public JDBCTemplateImpl(AbstractTranSessionImpl tranSessionImpl, Dialect dialect, DebugService debugService, Connection conn, boolean isReadonly){
		this.tranSessionImpl = tranSessionImpl ;
		this.dialect = dialect ;
		this.debugService = debugService ;
		this.conn = conn ;
		this.isReadonly = isReadonly ;
	}
	
	public Object executeQuery(String sql, SQLQueryCallBack callback) {		
		return executeQuery(sql, null, callback) ;
	}

	public Object executeQuery(String sql, Object[] params, SQLQueryCallBack callback) {
		boolean measureTime = this.debugService.isMeasureTime() ;
		long startTime = 0L ;
		if(measureTime){
			startTime = System.nanoTime() ;
		}
		
		PreparedStatement pstm = null ;
		ResultSet rs = null ;
		try {
			pstm = conn.prepareStatement(sql) ;
			tranSessionImpl.applyQueryTimeout(pstm) ;
			
			if(params != null && params.length > 0){
				for(int i = 0 ; i < params.length ; i++){
					Object value = params[i] ;
					
					pstm.setObject(i + 1, value) ;
					
					//comment out. If the param's datatype orcorresponding property is not specified, use the jdbc setObject(...) anywhere.
//					SQLDataType type = this.dialect.getDataType(value.getClass().getName()) ;
//					type.setSQLValue(pstm, i + 1, value) ;
				}
			}
			
			rs = pstm.executeQuery() ;
			
			if(this.debugService.isLogSQL()){
				long timeCost = 0 ;
				if(measureTime){
					timeCost = System.nanoTime() - startTime ;
				}
				
				this.debugService.logSQL(sql, params, timeCost) ;
			}
			
			return callback.iteratorResultSet(rs) ;
		}catch(SQLException e){
			throw new JDBCException("Error Code:" + e.getErrorCode() + ", sql:" + sql, e, e.getSQLState()) ;
		}catch (Exception e) {
			throw new DaoException(sql, e) ;
		}finally{
			CloseUtil.close(rs) ;
			CloseUtil.close(pstm) ;
		}
	}
	
	public Object executeQueryWithoutPrepare(String sql, SQLQueryCallBack callback){
		boolean measureTime = this.debugService.isMeasureTime() ;
		long startTime = 0L ;
		if(measureTime){
			startTime = System.nanoTime() ;
		}
		
		Statement st = null ;
		ResultSet rs = null ;
		
		try {
			st = conn.createStatement() ;
			tranSessionImpl.applyQueryTimeout(st) ;
			
			rs = st.executeQuery(sql) ;
			
			if(this.debugService.isLogSQL()){
				long timeCost = 0 ;
				if(measureTime){
					timeCost = System.nanoTime() - startTime ;
				}
				
				this.debugService.logSQL(sql, timeCost) ;
			}
			
			return callback.iteratorResultSet(rs) ;
		}catch(SQLException e){
			throw new JDBCException("Error Code:" + e.getErrorCode() + ", sql:" + sql, e, e.getSQLState()) ;
		}catch (Exception e) {
			throw new DaoException(sql, e) ;
		}finally{
			CloseUtil.close(rs) ;
			CloseUtil.close(st) ;
		}
	}

	public int executeUpdate(String sql, Object[] params) {
		if(isReadonly){
			throw new DaoException("connection is readonly. sql is:" + sql) ;
		}

		boolean measureTime = this.debugService.isMeasureTime() ;
		long startTime = 0L ;
		if(measureTime){
			startTime = System.nanoTime() ;
		}
		
		PreparedStatement pstm = null ;
		
		try {			
			pstm = conn.prepareStatement(sql) ;
			tranSessionImpl.applyQueryTimeout(pstm) ;
			
			if(params != null && params.length > 0){
				for(int i = 0 ; i < params.length ; i++){
					Object value = params[i] ;
					
					pstm.setObject(i + 1, value) ;
					
					//comment out. If the param's datatype orcorresponding property is not specified, use the jdbc setObject(...) anywhere.
//					SQLDataType type = this.dialect.getDataType(value.getClass().getName()) ;
//					type.setSQLValue(pstm, i + 1, value) ;
				}
			}

			int affectedRows = pstm.executeUpdate() ;
			
			if(this.debugService.isLogSQL()){
				long timeCost = 0 ;
				if(measureTime){
					timeCost = System.nanoTime() - startTime ;
				}
				
				this.debugService.logSQL(sql, params, timeCost) ;
			}
			
			return affectedRows ;
		}catch(SQLException e){
			throw new JDBCException("Error Code:" + e.getErrorCode() + ", sql:" + sql, e, e.getSQLState()) ;
		}catch (Exception e) {
			throw new DaoException(sql, e) ;
		}finally{
			CloseUtil.close(pstm) ;
		}
	}
	
	public int executeUpdate(String sql, int[] params) {
		if(isReadonly){
			throw new DaoException("connection is readonly. sql is:" + sql) ;
		}

		boolean measureTime = this.debugService.isMeasureTime() ;
		long startTime = 0L ;
		if(measureTime){
			startTime = System.nanoTime() ;
		}
		
		PreparedStatement pstm = null ;
		
		try {
			pstm = conn.prepareStatement(sql) ;
			tranSessionImpl.applyQueryTimeout(pstm) ;
			
			if(params != null && params.length > 0){
				for(int i = 0 ; i < params.length ; i++){					
					pstm.setInt(i + 1, params[i]) ;
				}
			}
			
			int affectedRows = pstm.executeUpdate() ;
			
			if(this.debugService.isLogSQL()){
				long timeCost = 0 ;
				if(measureTime){
					timeCost = System.nanoTime() - startTime ;
				}
				
				this.debugService.logSQL(sql, params, timeCost) ;
			}
			
			return affectedRows ;
		}catch(SQLException e){
			throw new JDBCException("Error Code:" + e.getErrorCode() + ", sql:" + sql, e, e.getSQLState()) ;
		}catch (Exception e) {
			throw new DaoException(sql, e) ;
		}finally{
			CloseUtil.close(pstm) ;
		}
	}
	
	public int executeUpdate(String sql, SQLDataType[] dataTypes, Object[] params){
		if(isReadonly){
			throw new DaoException("connection is readonly. sql is:" + sql) ;
		}

		boolean measureTime = this.debugService.isMeasureTime() ;
		long startTime = 0L ;
		if(measureTime){
			startTime = System.nanoTime() ;
		}
		
		PreparedStatement pstm = null ;
		
		try {
			pstm = conn.prepareStatement(sql) ;
			tranSessionImpl.applyQueryTimeout(pstm) ;
			
			if(dataTypes != null && dataTypes.length > 0){
				for(int i = 0 ; i < dataTypes.length ; i++){
					SQLDataType type = dataTypes[i] ;
					Object value = params[i] ;
					
					type.setSQLValue(pstm, i + 1, value) ;
				}
			}
			
			int affectedRows = pstm.executeUpdate() ;
			
			if(this.debugService.isLogSQL()){
				long timeCost = 0 ;
				if(measureTime){
					timeCost = System.nanoTime() - startTime ;
				}
				
				this.debugService.logSQL(sql, params, timeCost) ;
			}
			
			return affectedRows ;
		}catch(SQLException e){
			throw new JDBCException("Error Code:" + e.getErrorCode() + ", sql:" + sql, e, e.getSQLState()) ;
		}catch (Exception e) {
			throw new DaoException(sql, e) ;
		}finally{
			CloseUtil.close(pstm) ;
		}
	}
	
	public int executeUpdateWithoutPrepare(String sql){
		if(isReadonly){
			throw new DaoException("connection is readonly. sql is:" + sql) ;
		}

		boolean measureTime = this.debugService.isMeasureTime() ;
		long startTime = 0L ;
		if(measureTime){
			startTime = System.nanoTime() ;
		}
		
		Statement st = null ;
		
		try {
			st = conn.createStatement() ;
			tranSessionImpl.applyQueryTimeout(st) ;
			
			int affectedRows = st.executeUpdate(sql) ;
			
			if(this.debugService.isLogSQL()){
				long timeCost = 0 ;
				if(measureTime){
					timeCost = System.nanoTime() - startTime ;
				}
				
				this.debugService.logSQL(sql, timeCost) ;
			}
			
			return affectedRows ;
		}catch(SQLException e){
			throw new JDBCException("Error Code:" + e.getErrorCode() + ", sql:" + sql, e, e.getSQLState()) ;
		}catch (Exception e) {
			throw new DaoException(sql, e) ;
		}finally{
			CloseUtil.close(st) ;
		}
	}
	
	public int executeUpdate(String sql) {
		return executeUpdate(sql, new int[0]) ;
	}

	public Connection getConnection() {
		return (Connection) Proxy.newProxyInstance(
				conn.getClass().getClassLoader(), new Class[]{Connection.class},
				new CloseSuppressingInvocationHandler(conn));
	}

	/**
	 * Native JDBC Connection. Don't close it!
	 */
	public Connection getNativeConnection() {
		return conn ;
	}
	
	/**
	 * Invocation handler that suppresses close calls on JDBC Connection.
	 */
	private class CloseSuppressingInvocationHandler implements InvocationHandler {

		private final Connection target;
		
		public CloseSuppressingInvocationHandler(Connection target) {
			this.target = target;
		}

		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			// Invocation on Session interface coming in...

			if (method.getName().equals("equals")) {
				// Only consider equal when proxies are identical.
				return (proxy == args[0]);
			}else if (method.getName().equals("hashCode")) {
				// Use hashCode of Session proxy.
				return System.identityHashCode(proxy);
			}else if (method.getName().equals("close")) {
				// Handle close method: suppress, not valid.
				return null;
			}else if (method.getName().equals("setTransactionIsolation") && args.length == 1) {
				tranSessionImpl.getConnectionsGroup().setTransactionIsolation(target, (Integer) args[0]) ;
				return null;
			}

			// Invoke method on target Session.
			try {
				Object retVal = method.invoke(this.target, args);
				
				//set timeout
				if(retVal instanceof PreparedStatement){
					tranSessionImpl.applyQueryTimeout((PreparedStatement) retVal) ;
				}else if(retVal instanceof Statement){
					tranSessionImpl.applyQueryTimeout((Statement) retVal) ;
				}else if(retVal instanceof CallableStatement){
					tranSessionImpl.applyQueryTimeout((CallableStatement) retVal) ;
				}
				
				return retVal;
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}


}
