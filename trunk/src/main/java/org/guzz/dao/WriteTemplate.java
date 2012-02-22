/*
 * Copyright 2008-2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guzz.dao;

import java.io.Serializable;
import java.util.Map;

import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.WriteTranSession;
import org.guzz.web.context.spring.GuzzTransactionManager;

/**
 * Helper class that simplifies Guzz data access code. 
 * 
 * Consider this as org.springframework.orm.hibernate3.HibernateTemplate.
 *
 * <p>The central method is <code>execute</code>, supporting Guzz access code
 * implementing the {@link GuzzWriteCallback} interface for writing 
 * and {@link GuzzReadCallback} interface for reading from master databases
 * within the same transaction of the {@link WriteTranSession} under Spring application context. 
 * It provides Guzz WriteTranSession handling such that neither 
 * the GuzzWriteCallback/GuzzReadCallback implementation nor the calling
 * code needs to explicitly care about retrieving/closing Guzz Sessions,
 * or handling Session lifecycle exceptions. For typical writing single step actions,
 * there are various convenience methods (insert, update, delete, getForUpdate).
 *
 * <p>Can be used within a service implementation via direct instantiation
 * with a GuzzContext reference, or get prepared in an application context
 * and given to services as bean reference. Note: The GuzzContext should
 * always be configured as bean in the application context, in the first case
 * given to the service directly, in the second case to the prepared template.
 *
 * <p>Lazy loading will always open a new connection to a read database 
 * even in a Spring application context.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see GuzzReadCallback
 * @see GuzzWriteCallback
 * @see GuzzTransactionManager
 */
public interface WriteTemplate {

	public Serializable insert(Object domainObject);
	
	public boolean update(Object domainObject);
	
	public boolean delete(Object domainObject);
	
	public Serializable insert(Object domainObject, Object tableCondition);
	
	public boolean update(Object domainObject, Object tableCondition);
	
	public boolean delete(Object domainObject, Object tableCondition);
	
	public Object getForUpdate(Class domainClass, Serializable pk);
	
	/**
	 * Execute a database read/query operation on the write/master database.
	 * 
	 * <p>If called within a thread-bound Guzz transaction (initiated
	 * by GuzzTransactionManager), the code will join the transaction to get executed.
	 * Or a new <code>ReadonlyTranSession</code> with master database writable connections 
	 * is opened to serve the action. 
	 * 
	 * <p>Allows for returning a result object created within the callback,
	 * i.e. a domain object or a collection of domain objects.
	 * A thrown custom RuntimeException is treated as an application exception:
	 * It gets propagated to the caller of the template.
	 */
	public <T> T executeReadInMasterDB(GuzzReadCallback<T> action) ;

	/**
	 * Execute a database write operation.
	 * 
	 * <p>If called within a thread-bound Guzz transaction (initiated
	 * by GuzzTransactionManager), the code will join the transaction to get executed.
	 * Or a new {@link TransactionManager#openRWTran(false)} is opened to serve the action. 
	 * 
	 * The caller doesn't have to close, commit or rollback the {@link WriteTranSession} passed in,
	 * <code>WriteTemplate</code> will perform the cleanup if necessary.
	 * 
	 * <p>Allows for returning a result object created within the callback,
	 * i.e. A Integer indicating the affected rows.
	 * A thrown custom RuntimeException is treated as an application exception:
	 * It gets propagated to the caller of the template.
	 */
	public <T> T executeWrite(GuzzWriteCallback<T> action) ;
	
	public int executeUpdate(BindedCompiledSQL bsql) ;
	
	public int executeUpdate(String id, Map params) ;
	
	/**
	 * Export read API from the underly <code>WriteTranSession</code> of this template.
	 * 
	 * <p>If called within a thread-bound Guzz transaction (initiated
	 * by GuzzTransactionManager), the returned <code>ReadonlyTranSession</code> will be inside the thread-bound transaction.
	 * Or a new <code>ReadonlyTranSession</code> with master database writable connections is opened to return.
	 * 
	 * <p><b>The caller is responsible for closing the returned <code>ReadonlyTranSession</code>.</b>
	 */
	public ReadonlyTranSession exportReadAPI() ;
	
	/**
	 * Return the the underly <code>WriteTranSession</code> of this template.
	 * 
	 * <p>If called within a thread-bound Guzz transaction (initiated
	 * by GuzzTransactionManager), the returned <code>WriteTranSession</code> will be inside the thread-bound transaction.
	 * Or a new <b>auto-commit</b> <code>WriteTranSession</code> is opened to return.
	 * 
	 * <p><b>The caller is responsible for closing the returned <code>WriteTranSession</code>.</b>
	 */
	public WriteTranSession getWriteTranSession() ;

}
