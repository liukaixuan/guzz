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

package org.guzz.transaction;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.SQLException;
import java.util.Map;

import org.guzz.dao.GuzzWriteCallback;
import org.guzz.dao.WriteTemplate;
import org.guzz.exception.GuzzException;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.web.context.spring.SpringWriteTemplate;

/**
 * Base class for implementors of {@link TranSessionLocator}.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see SoloWriteTemplate
 * @see SpringWriteTemplate
 */
public abstract class AbstractWriteTemplate implements WriteTemplate {

	/**
	 * Execute the action specified by the given action object within the current binded WriteTranSession.
	 * @param action callback object that specifies the Guzz action
	 * @param enforceNativeSession whether to enforce exposure of the native
	 * Guzz Session to callback code
	 * @return a result object returned by the action, or <code>null</code>
	 * @throws RuntimeException in case of Guzz errors
	 */
	protected abstract <T> T doExecute(GuzzWriteCallback<T> action, boolean enforceNativeSession) throws RuntimeException ;
	
	/**
	 * return the current binded WriteTranSession.
	 * 
	 * @param exportNativeSession export native WriteTranSession
	 */
	protected WriteTranSession currentSession(boolean exportNativeSession) {
		WriteTranSession session = getSession() ;
		
		return exportNativeSession ? session : createSessionProxy(session) ;
	}

	/**
	 * Return the current Session for use by this template.
	 * @return the Session to use (never <code>null</code>)
	 */
	protected abstract WriteTranSession getSession() ;

	/**
	 * Create a close-suppressing proxy for the given Guzz Session.
	 * @param session the Guzz Session to create a proxy for
	 * @return the Session proxy
	 */
	protected WriteTranSession createSessionProxy(WriteTranSession session) {
		Class[] sessionIfcs = new Class[]{WriteTranSession.class} ;
		
		return (WriteTranSession) Proxy.newProxyInstance(
				session.getClass().getClassLoader(), sessionIfcs,
				new CloseSuppressingInvocationHandler(session));
	}

	public Serializable insert(final Object domainObject) {
		return this.doExecute(new GuzzWriteCallback<Serializable>() {

			public Serializable doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.insert(domainObject) ;
			}
			
		}, true) ;
	}

	public Serializable insert(final Object domainObject, final Object tableCondition) {
		return this.doExecute(new GuzzWriteCallback<Serializable>() {

			public Serializable doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.insert(domainObject, tableCondition) ;
			}
			
		}, true) ;
	}
	
	public boolean update(final Object domainObject) {
		return this.doExecute(new GuzzWriteCallback<Boolean>() {

			public Boolean doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.update(domainObject) ;
			}
			
		}, true).booleanValue() ;
	}

	public boolean update(final Object domainObject, final Object tableCondition) {
		return this.doExecute(new GuzzWriteCallback<Boolean>() {

			public Boolean doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.update(domainObject, tableCondition) ;
			}
			
		}, true).booleanValue() ;
	}

	public boolean delete(final Object domainObject) {
		return this.doExecute(new GuzzWriteCallback<Boolean>() {

			public Boolean doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.delete(domainObject) ;
			}
			
		}, true).booleanValue() ;
	}

	public boolean delete(final Object domainObject, final Object tableCondition) {
		return this.doExecute(new GuzzWriteCallback<Boolean>() {

			public Boolean doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.delete(domainObject, tableCondition) ;
			}
			
		}, true).booleanValue() ;
	}

	public Object getForUpdate(final Class domainClass, final Serializable pk) {
		return this.doExecute(new GuzzWriteCallback<Object>() {

			public Object doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.findObjectByPK(domainClass, pk) ;
			}
			
		}, true) ;
	}
	
	public int executeUpdate(final BindedCompiledSQL bsql){
		return this.doExecute(new GuzzWriteCallback<Integer>() {

			public Integer doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.executeUpdate(bsql) ;
			}
			
		}, true).intValue() ;
	}
	
	public int executeUpdate(final String id, final Map params){
		return this.doExecute(new GuzzWriteCallback<Integer>() {

			public Integer doWrite(WriteTranSession session) throws GuzzException, SQLException {
				return session.executeUpdate(id, params) ;
			}
			
		}, true).intValue() ;
	}


	/**
	 * Invocation handler that suppresses close calls on Guzz Sessions.
	 * @see WriteTranSession#close
	 */
	private class CloseSuppressingInvocationHandler implements InvocationHandler {

		private final WriteTranSession target;

		public CloseSuppressingInvocationHandler(WriteTranSession target) {
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
			}

			// Invoke method on target Session.
			try {
				Object retVal = method.invoke(this.target, args);
				
				return retVal;
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}

}
