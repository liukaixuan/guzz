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
package org.guzz.dao;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.guzz.GuzzContext;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.service.core.DynamicSQLService;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;


/**
 * 
 * 供第三方方便调用的BaseDao.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzBaseDao {
	
	private TransactionManager transactionManager ;
	
	private GuzzContext guzzContext ;
	
	public Serializable insert(Object domainObject){
		WriteTranSession session = transactionManager.openRWTran(true) ;
		
		try{
			return session.insert(domainObject) ;
		}finally{
			session.close() ;
		}
	}
	
	public void update(Object domainObject){
		WriteTranSession session = transactionManager.openRWTran(true) ;
		
		try{
			session.update(domainObject) ;
		}finally{
			session.close() ;
		}
	}
	
	public void delete(Object domainObject){
		WriteTranSession session = transactionManager.openRWTran(true) ;
		
		try{
			session.delete(domainObject) ;
		}finally{
			session.close() ;
		}
	}
	
	public Serializable insert(Object domainObject, Object tableCondition){
		WriteTranSession session = transactionManager.openRWTran(true) ;
		
		try{
			return session.insert(domainObject, tableCondition) ;
		}finally{
			session.close() ;
		}
	}
	
	public void update(Object domainObject, Object tableCondition){
		WriteTranSession session = transactionManager.openRWTran(true) ;
		
		try{
			session.update(domainObject, tableCondition) ;
		}finally{
			session.close() ;
		}
	}
	
	public void delete(Object domainObject, Object tableCondition){
		WriteTranSession session = transactionManager.openRWTran(true) ;
		
		try{
			session.delete(domainObject, tableCondition) ;
		}finally{
			session.close() ;
		}
	}
	
	public Object getForUpdate(Class domainClass, Serializable pk){
		WriteTranSession session = transactionManager.openRWTran(true) ;
		
		try{
			return session.findObjectByPK(domainClass, pk) ;
		}finally{
			session.close() ;
		}
	}
	
	public Object getForRead(Class domainClass, Serializable pk){
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.findObjectByPK(domainClass, pk) ;
		}finally{
			session.close() ;
		}
	}
	
	public Object findObject(String id, Map params){
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.findObject(id, params) ;
		}finally{
			session.close() ;
		}
	}
	
	public Object findObject(BindedCompiledSQL bsql) {
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.findObject(bsql) ;
		}finally{
			session.close() ;
		}
	}	
	
	public Object findObject(SearchExpression se) {
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.findObject(se) ;
		}finally{
			session.close() ;
		}	
	}

	
	/**
	 * Return the value of first column in the first row.
	 * 
	 * @param id Sql id defined in guzz.xml or {@link DynamicSQLService}
	 * @param params Named parameters
	 * @param returnType The data type of the result to return. eg: int, long, float...
	 * @see ReadonlyTranSession#findCell00(String, Map, String)
	 */
	public Object findCell00(String id, Map params, String returnType){
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.findCell00(id, params, returnType) ;
		}finally{
			session.close() ;
		}	
	}
	
	/**
	 * Return the value of first column in the first row.
	 * 
	 * @param bsql BindedCompiledSQL to execute.
	 * @param returnType The data type of the result to return. eg: int, long, float...
	 * @see ReadonlyTranSession#findCell00(BindedCompiledSQL, String)
	 */
	public Object findCell00(BindedCompiledSQL bsql, String returnType) {
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.findCell00(bsql, returnType) ;
		}finally{
			session.close() ;
		}		
	}
	
	/**执行se中的count操作，返回long类型的数据。从slave数据库读取。*/
	public long count(SearchExpression se){
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.count(se) ;
		}finally{
			session.close() ;
		}		
	}
	
	/**执行se中的list操作。从slave数据库读取。*/
	public List list(SearchExpression se){
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.list(se) ;
		}finally{
			session.close() ;
		}		
	}
	
	/**
	 * 从slave数据库读取列表。
	 * 
	 * @param bsql BindedCompiledSQL
	 * @param startPos 从 1 开始
	 * @param maxSize
	 */
	public List list(BindedCompiledSQL bsql, int startPos, int maxSize){
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.list(bsql, 1, 2) ;
		}finally{
			session.close() ;
		}
	}
	
	/**执行se中的count操作，返回long类型的数据。从slave数据库读取。*/
	public PageFlip page(SearchExpression se){
		ReadonlyTranSession session = transactionManager.openDelayReadTran() ;
		
		try{
			return session.page(se) ;
		}finally{
			session.close() ;
		}		
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setGuzzContext(GuzzContext guzzContext){
		this.guzzContext = guzzContext ;
		setTransactionManager(guzzContext.getTransactionManager()) ;
	}
	
	public GuzzContext getGuzzContext(){
		return this.guzzContext ;
	}
	
}
