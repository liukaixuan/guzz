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

import java.sql.Connection;

import org.guzz.orm.sql.SQLQueryCallBack;
import org.guzz.orm.type.SQLDataType;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface JDBCTemplate {
	            	
	public int executeUpdate(String sql) ;
	
	/**
     * 执行一个SQL语句。
     * @param sql 如：update a set a = a + 1 ;
     * @param params 参数
     * @return 执行后的结果，返回值参看PreparedStatement.executeUpdate()
     */
	public int executeUpdate(String sql, Object[] params) ;
	
	/**
     * 执行一个SQL语句。
     * @param sql 如：update a set a = a + 1 ;
     * @param params 参数
     * @return 执行后的结果，返回值参看PreparedStatement.executeUpdate()
     */
	public int executeUpdate(String sql, int[] params) ;
	
	
	public int executeUpdate(String sql, SQLDataType[] dataTypes, Object[] params) ;
	
	/**
	 * 返回callback返回的值。
	 */
	public Object executeQuery(String sql, SQLQueryCallBack callback) ;
	
	
	public Object executeQuery(String sql, Object[] params, SQLQueryCallBack callback) ;
	
	public Object executeQueryWithoutPrepare(String sql, SQLQueryCallBack callback) ;

	public Connection getConnection() ;
	
}
