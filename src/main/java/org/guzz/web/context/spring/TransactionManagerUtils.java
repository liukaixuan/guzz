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

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.aopalliance.intercept.Interceptor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.GuzzException;
import org.guzz.exception.JDBCException;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.jdbc.support.SQLStateSQLExceptionTranslator;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

/**
 * Helper class featuring methods for Guzz WriteTranSession handling,
 * allowing for reuse of Guzz WriteTranSession instances within transactions.
 * Also provides support for exception translation.
 *
 * <p>Supports synchronization with both Spring-managed JTA transactions
 * (see {@link org.springframework.transaction.jta.JtaTransactionManager})
 * and non-Spring JTA transactions (i.e. plain JTA or EJB CMT),
 * transparently providing transaction-scoped Guzz WriteTranSessions.
 * Note that for non-Spring JTA transactions, a JTA TransactionManagerLookup
 * has to be specified in the Guzz configuration.
 *
 * <p>Used internally by {@link SpringWriteTemplate},
 * and {@link GuzzTransactionManager}. Can also be used directly in
 * application code.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see #getSession
 * @see #releaseSession
 * @see GuzzTransactionManager
 * @see org.springframework.transaction.jta.JtaTransactionManager
 * @see org.springframework.transaction.support.TransactionSynchronizationManager
 */
public abstract class TransactionManagerUtils {

	/**
	 * Order value for TransactionSynchronization objects that clean up Guzz WriteTranSessions.
	 * Returns <code>DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER + 100</code>
	 * to execute Session cleanup after JDBC Connection cleanup, if any.
	 * @see org.springframework.jdbc.datasource.DataSourceUtils#CONNECTION_SYNCHRONIZATION_ORDER
	 */
	public static final int SESSION_SYNCHRONIZATION_ORDER =
			DataSourceUtils.CONNECTION_SYNCHRONIZATION_ORDER + 100;

	static final Log logger = LogFactory.getLog(TransactionManagerUtils.class);


	/**
	 * Create an appropriate SQLExceptionTranslator for the given TransactionManager.
	 * If a DataSource is found, a SQLErrorCodeSQLExceptionTranslator for the DataSource
	 * is created; else, a SQLStateSQLExceptionTranslator as fallback.
	 * @param transactionManager the TransactionManager to create the translator for
	 * @return the SQLExceptionTranslator
	 * @see #getDataSource
	 * @see org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator
	 * @see org.springframework.jdbc.support.SQLStateSQLExceptionTranslator
	 */
	public static SQLExceptionTranslator newJdbcExceptionTranslator() {
		return new SQLStateSQLExceptionTranslator();
	}
	

	/**
	 * Get a Guzz WriteTranSession for the given TransactionManager. Is aware of and will
	 * return any existing corresponding Session bound to the current thread, for
	 * example when using {@link GuzzTransactionManager}. Will create a new
	 * Session otherwise, if "allowCreate" is <code>true</code>.
	 * <p>This is the <code>getSession</code> method used by typical data access code,
	 * in combination with <code>releaseSession</code> called when done with
	 * the Session. Note that SpringWriteTemplate allows to write data access code
	 * without caring about such resource handling.
	 * @param transactionManager Guzz TransactionManager to create the session with
	 * @param allowCreate whether a non-transactional Session should be created
	 * when no transactional Session can be found for the current thread
	 * @return the Guzz WriteTranSession
	 * @throws DataAccessResourceFailureException if the Session couldn't be created
	 * @throws IllegalStateException if no thread-bound Session found and
	 * "allowCreate" is <code>false</code>
	 * @see #getSession(TransactionManager, Interceptor, SQLExceptionTranslator)
	 * @see #releaseSession
	 * @see SpringWriteTemplate
	 */
	public static WriteTranSession getSession(TransactionManager transactionManager) throws DataAccessResourceFailureException, IllegalStateException {
		try {
			return doGetSession(transactionManager);
		}catch (GuzzException ex) {
			throw new DataAccessResourceFailureException("Could not open Guzz WriteTranSession", ex);
		}
	}
	
	/**
	 * Get a Guzz WriteTranSession for the given TransactionManager. Is aware of and will
	 * return any existing corresponding Session bound to the current thread, for
	 * example when using {@link GuzzTransactionManager}. Will create a new
	 * Session otherwise, if "allowCreate" is <code>true</code>.
	 * <p>Same as {@link #getSession}, but throwing the original GuzzException.
	 * @param transactionManager Guzz TransactionManager to create the session with
	 * @param entityInterceptor Guzz entity interceptor, or <code>null</code> if none
	 * @param jdbcExceptionTranslator SQLExcepionTranslator to use for flushing the
	 * Session on transaction synchronization (may be <code>null</code>)
	 * @param allowCreate whether a non-transactional Session should be created
	 * when no transactional Session can be found for the current thread
	 * @return the Guzz WriteTranSession
	 * @throws GuzzException if the Session couldn't be created
	 * @throws IllegalStateException if no thread-bound Session found and
	 * "allowCreate" is <code>false</code>
	 */
	private static WriteTranSession doGetSession(TransactionManager transactionManager) throws GuzzException, IllegalStateException {
		Assert.notNull(transactionManager, "No TransactionManager specified");
		WriteTranSession session = null ;
		
		WriteTranSessionHolder writeTranSessionHolder = (WriteTranSessionHolder) TransactionSynchronizationManager.getResource(transactionManager);
		if (writeTranSessionHolder != null) {
			// pre-bound Guzz WriteTranSession
			session = writeTranSessionHolder.getWriteTranSession() ;
		}else{
			//个人分析：下面的代码在非spring声明式事务下执行。如果是声明事务应该执行不到。如果理解的不对，可能就出现bug了。
			logger.debug("Opening Guzz WriteTranSession");
			session = transactionManager.openRWTran(false) ;
	
			// Use same Session for further Guzz actions within the transaction.
			// Thread object will get removed by synchronization at transaction completion.
			if (TransactionSynchronizationManager.isSynchronizationActive()) {
				// We're within a Spring-managed transaction, possibly from JtaTransactionManager.
				logger.debug("Registering Spring transaction synchronization for new Guzz WriteTranSession");
				writeTranSessionHolder = new WriteTranSessionHolder(session);
				
				TransactionSynchronizationManager.registerSynchronization(
						new SpringSessionSynchronization(writeTranSessionHolder, transactionManager, true));
	
				TransactionSynchronizationManager.bindResource(transactionManager, writeTranSessionHolder);
				writeTranSessionHolder.setSynchronizedWithTransaction(true);
			}
			
			// Check whether we are allowed to return the Session.
			if (!isSessionTransactional(session, transactionManager)) {
				closeSession(session);
				throw new IllegalStateException("No Guzz WriteTranSession bound to thread here");
			}
		}
		
		if(writeTranSessionHolder.hasTimeout()){
			session.setQueryTimeoutInSeconds(writeTranSessionHolder.getTimeToLiveInSeconds()) ;
		}

		return session;
	}

	/**
	 * Stringify the given Session for debug logging.
	 * Returns output equivalent to <code>Object.toString()</code>:
	 * the fully qualified class name + "@" + the identity hash code.
	 * <p>The sole reason why this is necessary is because Guzz3's
	 * <code>Session.toString()</code> implementation is broken (and won't be fixed):
	 * it logs the toString representation of all persistent objects in the Session,
	 * which might lead to ConcurrentModificationExceptions if the persistent objects
	 * in turn refer to the Session (for example, for lazy loading).
	 * @param session the Guzz WriteTranSession to stringify
	 * @return the String representation of the given Session
	 */
	public static String toString(WriteTranSession session) {
		return session.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(session));
	}

	/**
	 * Return whether there is a transactional Guzz WriteTranSession for the current thread,
	 * that is, a Session bound to the current thread by Spring's transaction facilities.
	 * @param transactionManager Guzz TransactionManager to check (may be <code>null</code>)
	 * @return whether there is a transactional Session for current thread
	 */
	public static boolean hasTransactionalSession(TransactionManager transactionManager) {
		if (transactionManager == null) {
			return false;
		}
		
		WriteTranSessionHolder writeTranSessionHolder =
				(WriteTranSessionHolder) TransactionSynchronizationManager.getResource(transactionManager);
		
		return (writeTranSessionHolder != null && !writeTranSessionHolder.isEmpty());
	}

	/**
	 * Return whether the given Guzz WriteTranSession is transactional, that is,
	 * bound to the current thread by Spring's transaction facilities.
	 * @param session the Guzz WriteTranSession to check
	 * @param transactionManager Guzz TransactionManager that the Session was created with
	 * (may be <code>null</code>)
	 * @return whether the Session is transactional
	 */
	public static boolean isSessionTransactional(WriteTranSession session, TransactionManager transactionManager) {
		if (transactionManager == null) {
			return false;
		}
		
		WriteTranSessionHolder writeTranSessionHolder =
				(WriteTranSessionHolder) TransactionSynchronizationManager.getResource(transactionManager) ;
		
		return (writeTranSessionHolder != null && writeTranSessionHolder.getWriteTranSession() == session);
	}

	/**
	 * Apply the current transaction timeout, if any, to the given
	 * Guzz Query object.
	 * @param query the Guzz Query object
	 * @param transactionManager Guzz TransactionManager that the Query was created for
	 * (may be <code>null</code>)
	 * @see org.hibernate.Query#setTimeout
	 */
	public static void applyTransactionTimeout(PreparedStatement pstm, TransactionManager transactionManager) {
		Assert.notNull(pstm, "No PreparedStatement object specified");
		if (transactionManager != null) {
			WriteTranSessionHolder writeTranSessionHolder =
					(WriteTranSessionHolder) TransactionSynchronizationManager.getResource(transactionManager);
			
			if (writeTranSessionHolder != null && writeTranSessionHolder.hasTimeout()) {
				try {
					pstm.setQueryTimeout(writeTranSessionHolder.getTimeToLiveInSeconds());
				} catch (SQLException e) {
					throw new DataAccessResourceFailureException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Convert the given GuzzException to an appropriate exception
	 * from the <code>org.springframework.dao</code> hierarchy.
	 * @param ex GuzzException that occured
	 * @return the corresponding DataAccessException instance
	 * @see GuzzAccessor#convertGuzzAccessException
	 * @see GuzzTransactionManager#convertGuzzAccessException
	 */
	public static DataAccessException convertGuzzAccessException(GuzzException ex) {
		if(ex instanceof JDBCException){
			JDBCException e = (JDBCException) ex ;
			
			return new UncategorizedSQLException(e.getMessage(), e.getSQL(), e.getSQLException()) ;
		}
		
		return new DataAccessResourceFailureException(ex.getMessage(), ex);		
	}
	
	/**
	 * Close the given Session, created via the given factory,
	 * if it is not managed externally (i.e. not bound to the thread).
	 * @param session the Guzz WriteTranSession to close (may be <code>null</code>)
	 * @param transactionManager Guzz TransactionManager that the Session was created with
	 * (may be <code>null</code>)
	 */
	public static void releaseSession(WriteTranSession session, TransactionManager transactionManager) {
		if (session == null) {
			return;
		}
		
		// Only close non-transactional Sessions.
		if (!isSessionTransactional(session, transactionManager)) {
			closeSession(session);
		}
	}

	/**
	 * Perform actual closing of the Guzz WriteTranSession,
	 * catching and logging any cleanup exceptions thrown.
	 * @param session the Guzz WriteTranSession to close (may be <code>null</code>)
	 * @see org.guzz.transaction.WriteTranSession#close()
	 */
	public static void closeSession(WriteTranSession session) {
		if (session != null) {
			logger.debug("Closing Guzz WriteTranSession");
			session.close();
		}
	}

}
