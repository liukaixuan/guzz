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

package org.guzz.web.context.spring;

import java.sql.SQLException;

import org.guzz.GuzzContext;
import org.guzz.dao.GuzzReadCallback;
import org.guzz.dao.GuzzWriteCallback;
import org.guzz.dao.WriteTemplate;
import org.guzz.exception.DaoException;
import org.guzz.exception.GuzzException;
import org.guzz.exception.JDBCException;
import org.guzz.transaction.AbstractWriteTemplate;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.transaction.WriteTranSessionImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.util.Assert;

/**
 * 
 * <code>WriteTemplate</code> works with Spring managed transaction.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see GuzzTransactionManager
 */
public class SpringWriteTemplate extends AbstractWriteTemplate implements WriteTemplate, InitializingBean {

	private TransactionManager transactionManager ;

	private SQLExceptionTranslator jdbcExceptionTranslator;
	
	private SQLExceptionTranslator defaultJdbcExceptionTranslator;

	/**
	 * Create a new SpringWriteTemplate instance.
	 */
	public SpringWriteTemplate() {
	}

	/**
	 * Create a new SpringWriteTemplate instance.
	 * @param transactionManager TransactionManager to create Sessions
	 * @throws Exception 
	 */
	public SpringWriteTemplate(TransactionManager transactionManager) throws Exception {
		setTransactionManager(transactionManager);
		afterPropertiesSet();
	}

	public TransactionManager getTransactionManager(){
		return this.transactionManager ;
	}
	

	/**
	 * Execute the action specified by the given action object within a Session.
	 * @param action callback object that specifies the Hibernate action
	 * @param enforceNewSession whether to enforce a new Session for this template
	 * even if there is a pre-bound transactional Session
	 * @param enforceNativeSession whether to enforce exposure of the native
	 * Hibernate Session to callback code
	 * @return a result object returned by the action, or <code>null</code>
	 * @throws org.springframework.dao.DataAccessException in case of Hibernate errors
	 */
	protected <T> T doExecute(GuzzWriteCallback<T> action, boolean enforceNativeSession) throws DataAccessException {
		Assert.notNull(action, "Callback object must not be null");

		WriteTranSession session = this.currentSession(enforceNativeSession) ;

		try {
			T result = action.doWrite(session);
			return result;
		}
		catch (DaoException ex) {
			throw convertHibernateAccessException(ex);
		}
		catch (SQLException ex) {
			throw convertJdbcAccessException(ex);
		}
		catch (RuntimeException ex) {
			// Callback code threw application exception...
			throw ex;
		}
	}

	/**
	 * Return a Session for use by this template.
	 * <p>Returns a new Session in case of "alwaysUseNewSession" (using the same
	 * JDBC Connection as a transactional Session, if applicable), a pre-bound
	 * Session in case of "allowCreate" turned off, and a pre-bound or new Session
	 * otherwise (new only if no transactional or otherwise pre-bound Session exists).
	 * @return the Session to use (never <code>null</code>)
	 * @see TransactionManagerUtils#getSession
	 * @see TransactionManagerUtils#getNewSession
	 * @see #setAlwaysUseNewSession
	 * @see #setAllowCreate
	 */
	protected WriteTranSession getSession() {
		if (TransactionManagerUtils.hasTransactionalSession(getTransactionManager())) {
			return TransactionManagerUtils.getSession(getTransactionManager());
		}else {
			throw new DataAccessResourceFailureException("Could not obtain current thread-bounded Guzz WriteTranSession.");
		}
	}

	public <T> T executeReadInMasterDB(GuzzReadCallback<T> action) {
		try {
			return action.doRead(exportReadAPI()) ;
		} catch (GuzzException e) {
			throw convertHibernateAccessException(e) ;
		} catch (SQLException e) {
			throw convertJdbcAccessException(e) ;
		}
	}	

	public <T> T executeWrite(GuzzWriteCallback<T> action) {
		try {
			return action.doWrite(this.currentSession(false)) ;
		} catch (GuzzException e) {
			throw convertHibernateAccessException(e) ;
		} catch (SQLException e) {
			throw convertJdbcAccessException(e) ;
		}
	}

	public ReadonlyTranSession exportReadAPI() {
		return ((WriteTranSessionImpl) this.currentSession(true)).exportReadAPI() ;
	}
	
	public WriteTranSession getWriteTranSession() {
		return this.currentSession(false) ;
	}


	/**
	 * Set the JDBC exception translator for this instance.
	 * <p>Applied to any SQLException root cause of a Hibernate JDBCException,
	 * overriding Hibernate's default SQLException translation (which is
	 * based on Hibernate's Dialect for a specific target database).
	 * @param jdbcExceptionTranslator the exception translator
	 * @see java.sql.SQLException
	 * @see org.hibernate.JDBCException
	 * @see org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
	 * @see org.springframework.jdbc.support.SQLStateSQLExceptionTranslator
	 */
	public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
		this.jdbcExceptionTranslator = jdbcExceptionTranslator;
	}

	/**
	 * Return the JDBC exception translator for this instance, if any.
	 */
	public SQLExceptionTranslator getJdbcExceptionTranslator() {
		return this.jdbcExceptionTranslator;
	}

	/**
	 * Convert the given HibernateException to an appropriate exception
	 * from the <code>org.springframework.dao</code> hierarchy.
	 * <p>Will automatically apply a specified SQLExceptionTranslator to a
	 * Hibernate JDBCException, else rely on Hibernate's default translation.
	 * @param ex HibernateException that occured
	 * @return a corresponding DataAccessException
	 * @see TransactionManagerUtils#convertHibernateAccessException
	 * @see #setJdbcExceptionTranslator
	 */
	public DataAccessException convertHibernateAccessException(GuzzException ex) {
		if (getJdbcExceptionTranslator() != null && ex instanceof JDBCException) {
			return convertJdbcAccessException((JDBCException) ex, getJdbcExceptionTranslator());
		}
		
		return TransactionManagerUtils.convertGuzzAccessException(ex);
	}

	/**
	 * Convert the given Hibernate JDBCException to an appropriate exception
	 * from the <code>org.springframework.dao</code> hierarchy, using the
	 * given SQLExceptionTranslator.
	 * @param ex Hibernate JDBCException that occured
	 * @param translator the SQLExceptionTranslator to use
	 * @return a corresponding DataAccessException
	 */
	protected DataAccessException convertJdbcAccessException(JDBCException ex, SQLExceptionTranslator translator) {
		return translator.translate("Hibernate operation: " + ex.getMessage(), ex.getSQL(), ex.getSQLException());
	}

	/**
	 * Convert the given SQLException to an appropriate exception from the
	 * <code>org.springframework.dao</code> hierarchy. Can be overridden in subclasses.
	 * <p>Note that a direct SQLException can just occur when callback code
	 * performs direct JDBC access via <code>Session.connection()</code>.
	 * @param ex the SQLException
	 * @return the corresponding DataAccessException instance
	 * @see #setJdbcExceptionTranslator
	 * @see org.hibernate.Session#connection()
	 */
	protected DataAccessException convertJdbcAccessException(SQLException ex) {
		SQLExceptionTranslator translator = getJdbcExceptionTranslator();
		if (translator == null) {
			translator = getDefaultJdbcExceptionTranslator();
		}
		return translator.translate("Hibernate-related JDBC operation", null, ex);
	}

	/**
	 * Obtain a default SQLExceptionTranslator, lazily creating it if necessary.
	 * <p>Creates a default
	 * {@link org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator}
	 * for the SessionFactory's underlying DataSource.
	 */
	protected synchronized SQLExceptionTranslator getDefaultJdbcExceptionTranslator() {
		if (this.defaultJdbcExceptionTranslator == null) {
			this.defaultJdbcExceptionTranslator = TransactionManagerUtils.newJdbcExceptionTranslator();
		}
		return this.defaultJdbcExceptionTranslator;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.transactionManager, "transactionManager must not be null");
	}

	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.setTransactionManager(guzzContext.getTransactionManager()) ;
	}

}
