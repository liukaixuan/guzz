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

import java.sql.SQLException;

import org.guzz.exception.GuzzException;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.web.context.spring.GuzzTransactionManager;

/**
 * Callback interface for Guzz {@link ReadonlyTranSession} code. 
 * To be used with {@link WriteTemplate}'s
 * executeRead methods, often as anonymous classes within a method implementation.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see WriteTemplate
 * @see GuzzTransactionManager
 */
public interface GuzzReadCallback<T> {

	/**
	 * Gets called by <code>WriteTemplate.executeRead</code> with an active
	 * Guzz <code>ReadonlyTranSession</code>. Does not need to care about activating
	 * or closing the <code>ReadonlyTranSession</code>, or handling transactions.
	 *
	 * <p>If called without a thread-bound Guzz transaction (initiated
	 * by GuzzTransactionManager), a new <code>ReadonlyTranSession</code> 
	 * with master database writable connections is opened. 
	 *
	 * <p>Allows for returning a result object created within the callback,
	 * i.e. a domain object or a collection of domain objects.
	 * A thrown custom RuntimeException is treated as an application exception:
	 * It gets propagated to the caller of the template.
	 *
	 * @param session active ReadonlyTranSession
	 * @return a result object, or <code>null</code> if none
	 * @throws GuzzException if thrown by the Guzz API
	 * @throws SQLException if thrown by JDBC API
	 */
	T doRead(ReadonlyTranSession session) throws GuzzException, SQLException;

}
