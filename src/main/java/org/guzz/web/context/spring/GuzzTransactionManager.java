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

import org.guzz.GuzzContext;
import org.guzz.exception.DaoException;
import org.guzz.exception.JDBCException;
import org.guzz.transaction.IsolationsSavePointer;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * {@link org.springframework.transaction.PlatformTransactionManager}
 * implementation for a single Guzz {@link org.guzz.TransactionManager}.
 * Binds a Guzz <code>WriteTranSession</code> from the specified factory to the thread, 
 * potentially allowing for one thread-bound Session per factory. 
 * {@link TransactionManagerUtils}
 * and {@link SpringWriteTemplate} are aware of thread-bound Sessions and participate
 * in such transactions automatically. Using either of those is required for Guzz
 * access code that needs to support this transaction handling mechanism.
 *
 * <p>Supports custom isolation levels, and timeouts that get applied as
 * Guzz transaction timeouts.
 * 
 * <p>JTA and nested transactions are not supported!
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see TransactionManagerUtils#getSession
 * @see TransactionManagerUtils#applyTransactionTimeout
 * @see TransactionManagerUtils#releaseSession
 * @see SpringWriteTemplate
 */
@SuppressWarnings("serial")
public class GuzzTransactionManager extends AbstractPlatformTransactionManager
		implements ResourceTransactionManager, InitializingBean {

	private TransactionManager transactionManager;

	private SQLExceptionTranslator jdbcExceptionTranslator;


	/**
	 * Create a new GuzzTransactionManager instance.
	 * A TransactionManager has to be set to be able to use it.
	 * @see #setTransactionManager
	 */
	public GuzzTransactionManager() {
	}

	/**
	 * Create a new GuzzTransactionManager instance.
	 * @param transactionManager transactionManager to manage transactions for
	 */
	public GuzzTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
		afterPropertiesSet();
	}

	/**
	 * Create a new GuzzTransactionManager instance.
	 * @param sessionFactory SessionFactory to manage transactions for
	 */
	public GuzzTransactionManager(GuzzContext guzzContext) {
		this(guzzContext.getTransactionManager()) ;
	}


	/**
	 * Set the TransactionManager that this instance should manage transactions for.
	 */
	public void setTransactionManager(TransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	

	/**
	 * Set the TransactionManager that this instance should manage transactions for.
	 */
	public void setGuzzContext(GuzzContext guzzContext) {
		this.setTransactionManager(guzzContext.getTransactionManager()) ;
	}

	/**
	 * Return the TransactionManager that this instance should manage transactions for.
	 */
	public TransactionManager getTransactionManager() {
		return this.transactionManager;
	}

	/**
	 * Set the JDBC exception translator for this transaction manager.
	 * <p>Applied to any SQLException root cause of a Guzz JDBCException that
	 * is thrown on flush, overriding Guzz default SQLException translation
	 * @param jdbcExceptionTranslator the exception translator
	 * @see java.sql.SQLException
	 */
	public void setJdbcExceptionTranslator(SQLExceptionTranslator jdbcExceptionTranslator) {
		this.jdbcExceptionTranslator = jdbcExceptionTranslator;
	}

	/**
	 * Return the JDBC exception translator for this transaction manager, if any.
	 */
	public SQLExceptionTranslator getJdbcExceptionTranslator() {
		return this.jdbcExceptionTranslator;
	}
	
	protected DataAccessException convertGuzzAccessException(DaoException ex) {
		if(ex instanceof JDBCException && getJdbcExceptionTranslator() != null){
			JDBCException e = (JDBCException) ex ;
			
			return getJdbcExceptionTranslator().translate(e.getMessage(), e.getSQL(), e.getSQLException()) ;
		}
		
		return TransactionManagerUtils.convertGuzzAccessException(ex);
	}

	public void afterPropertiesSet() {
		if (getTransactionManager() == null) {
			throw new IllegalArgumentException("Property 'transactionManager' is required");
		}
	}

	public Object getResourceFactory() {
		return getTransactionManager();
	}

	@Override
	protected Object doGetTransaction() {
		GuzzTransactionObject txObject = new GuzzTransactionObject();

		WriteTranSessionHolder writeTranSessionHolder =
				(WriteTranSessionHolder) TransactionSynchronizationManager.getResource(getTransactionManager());
		
		if (writeTranSessionHolder != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Found thread-bound WriteTranSession [" +
						TransactionManagerUtils.toString(writeTranSessionHolder.getWriteTranSession()) + "] for Guzz transaction");
			}
			txObject.setWriteTranSessionHolder(writeTranSessionHolder);
		}

		return txObject;
	}

	@Override
	protected boolean isExistingTransaction(Object transaction) {
		GuzzTransactionObject txObject = (GuzzTransactionObject) transaction;
		
		return txObject.hasSpringManagedTransaction() ;
	}

	@Override
	protected void doBegin(Object transaction, TransactionDefinition definition) {
		GuzzTransactionObject txObject = (GuzzTransactionObject) transaction;

		//TODO: checkout the outside DataSourceTransactionManager 
//		if (txObject.hasConnectionHolder() && !txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
//			throw new IllegalTransactionStateException(
//					"Pre-bound JDBC Connection found! GuzzTransactionManager does not support " +
//					"running within DataSourceTransactionManager if told to manage the DataSource itself. ") ;
//		}

		WriteTranSession writeTranSession = null ;

		try {
			if (txObject.getSessionHolder() == null || txObject.getSessionHolder().isSynchronizedWithTransaction()) {
				writeTranSession = getTransactionManager().openRWTran(false) ;
				
				if (logger.isDebugEnabled()) {
					logger.debug("Opened new Session [" + TransactionManagerUtils.toString(writeTranSession) + "] for Guzz transaction");
				}
				txObject.setWriteTranSession(writeTranSession);
			}

			writeTranSession = txObject.getSessionHolder().getWriteTranSession() ;

			if (definition.getIsolationLevel() != TransactionDefinition.ISOLATION_DEFAULT) {
				// We should set a specific isolation level but are not allowed to...
				IsolationsSavePointer oldSavePointer = writeTranSession.setTransactionIsolation(definition.getIsolationLevel()) ;
			
				txObject.setIsolationsSavePointer(oldSavePointer) ;
			}
			
			// Register transaction timeout.
			int timeout = determineTimeout(definition);
			if (timeout != TransactionDefinition.TIMEOUT_DEFAULT) {
				txObject.getSessionHolder().setTimeoutInSeconds(timeout);
			}
						
			// Bind the session holder to the thread.
			if (txObject.isNewWriteTranSessionHolder()) {
				TransactionSynchronizationManager.bindResource(getTransactionManager(), txObject.getSessionHolder());
			}
			txObject.getSessionHolder().setSynchronizedWithTransaction(true);
			
		}catch (Exception ex) {
			if (txObject.isNewWriteTranSession()) {
				try {
					if (writeTranSession != null) {
						//TransactionIsolation不需要重置，因为数据库连接还没有打开，没有连接因此而改变属性。
						writeTranSession.rollback();
					}
				}
				catch (Throwable ex2) {
					logger.debug("Could not rollback WriteTranSession after failed transaction begin", ex);
				}
				finally {
					TransactionManagerUtils.closeSession(writeTranSession);
				}
			}
			throw new CannotCreateTransactionException("Could not open Guzz WriteTranSession for transaction", ex);
		}
	}

	@Override
	protected Object doSuspend(Object transaction) {
		GuzzTransactionObject txObject = (GuzzTransactionObject) transaction;
		txObject.setWriteTranSessionHolder(null);
		WriteTranSessionHolder writeTranSessionHolder = (WriteTranSessionHolder) TransactionSynchronizationManager.unbindResource(getTransactionManager());
				
		return new SuspendedResourcesHolder(writeTranSessionHolder);
	}

	@Override
	protected void doResume(Object transaction, Object suspendedResources) {
		SuspendedResourcesHolder resourcesHolder = (SuspendedResourcesHolder) suspendedResources;
		if (TransactionSynchronizationManager.hasResource(getTransactionManager())) {
			// From non-transactional code running in active transaction synchronization
			// -> can be safely removed, will be closed on transaction completion.
			TransactionSynchronizationManager.unbindResource(getTransactionManager());
		}
		
		TransactionSynchronizationManager.bindResource(getTransactionManager(), resourcesHolder.getSessionHolder());
	}


	@Override
	protected void doCommit(DefaultTransactionStatus status) {
		GuzzTransactionObject txObject = (GuzzTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Committing Guzz transaction on WriteTranSession [" +
					TransactionManagerUtils.toString(txObject.getSessionHolder().getWriteTranSession()) + "]");
		}
		try {
			txObject.getSessionHolder().getWriteTranSession().commit();
		}catch(DaoException ex){
			throw convertGuzzAccessException(ex);
		}catch (Throwable ex) {
			throw new TransactionSystemException("Could not commit Hibernate transaction", ex);
		}
	}

	@Override
	protected void doRollback(DefaultTransactionStatus status) {
		GuzzTransactionObject txObject = (GuzzTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Rolling back Guzz transaction on WriteTranSession [" +
					TransactionManagerUtils.toString(txObject.getSessionHolder().getWriteTranSession()) + "]");
		}
		
		try {
			txObject.getSessionHolder().getWriteTranSession().rollback();
		}catch (Throwable ex) {
			throw new TransactionSystemException("Could not roll back Hibernate transaction", ex);
		}
	}

	@Override
	protected void doSetRollbackOnly(DefaultTransactionStatus status) {
		GuzzTransactionObject txObject = (GuzzTransactionObject) status.getTransaction();
		if (status.isDebug()) {
			logger.debug("Setting Guzz transaction on WriteTranSession [" +
					TransactionManagerUtils.toString(txObject.getSessionHolder().getWriteTranSession()) + "] rollback-only");
		}
		txObject.setRollbackOnly();
	}

	@Override
	protected void doCleanupAfterCompletion(Object transaction) {
		GuzzTransactionObject txObject = (GuzzTransactionObject) transaction;

		// Remove the session holder from the thread.
		if (txObject.isNewWriteTranSessionHolder()) {
			TransactionSynchronizationManager.unbindResource(getTransactionManager());
		}

		WriteTranSession session = txObject.getSessionHolder().getWriteTranSession() ;
		
		if(txObject.getIsolationsSavePointer() != null){
			session.resetTransactionIsolationTo(txObject.getIsolationsSavePointer()) ;
		}

		if (txObject.isNewWriteTranSession()) {
			if (logger.isDebugEnabled()) {
				logger.debug("Closing Guzz WriteTranSession [" + TransactionManagerUtils.toString(session) +
						"] after transaction");
			}
			
			TransactionManagerUtils.closeSession(session);
		} else {
			if (logger.isDebugEnabled()) {
				logger.debug("Not closing pre-bound Guzz WriteTranSession [" +
						TransactionManagerUtils.toString(session) + "] after transaction");
			}
		}
		
		txObject.getSessionHolder().clear();
	}

	/**
	 * Guzz transaction object, representing a WriteTranSessionHolder.
	 * Used as transaction object by GuzzTransactionManager.
	 */
	private class GuzzTransactionObject implements SmartTransactionObject {

		private WriteTranSessionHolder writeTranSessionHolder;
		
		private IsolationsSavePointer isolationsSavePointer ;

		private boolean newWriteTranSessionHolder;

		private boolean newWriteTranSession;

		public void setWriteTranSession(WriteTranSession session) {
			this.writeTranSessionHolder = new WriteTranSessionHolder(session);
			this.newWriteTranSessionHolder = true;
			this.newWriteTranSession = true;
		}

		public void setWriteTranSessionHolder(WriteTranSessionHolder writeTranSessionHolder) {
			this.writeTranSessionHolder = writeTranSessionHolder;
			this.newWriteTranSessionHolder = false;
			this.newWriteTranSession = false;
		}

		public WriteTranSessionHolder getSessionHolder() {
			return this.writeTranSessionHolder;
		}

		public boolean isNewWriteTranSessionHolder() {
			return this.newWriteTranSessionHolder;
		}

		public boolean isNewWriteTranSession() {
			return this.newWriteTranSession;
		}

		public boolean hasSpringManagedTransaction() {
			return (this.writeTranSessionHolder != null && this.writeTranSessionHolder.getWriteTranSession() != null);
		}

		public void setRollbackOnly() {
			this.writeTranSessionHolder.setRollbackOnly();
		}

		public boolean isRollbackOnly() {
			return this.writeTranSessionHolder.isRollbackOnly() ;
		}

		public IsolationsSavePointer getIsolationsSavePointer() {
			return this.isolationsSavePointer;
		}

		public void setIsolationsSavePointer(IsolationsSavePointer isolationsSavePointer) {
			this.isolationsSavePointer = isolationsSavePointer;
		}

		public void flush() {
			//no-op
		}
		
	}


	/**
	 * Holder for suspended resources.
	 * Used internally by <code>doSuspend</code> and <code>doResume</code>.
	 */
	private static class SuspendedResourcesHolder {

		private final WriteTranSessionHolder writeTranSessionHolder;

		private SuspendedResourcesHolder(WriteTranSessionHolder writeTranSessionHolder) {
			this.writeTranSessionHolder = writeTranSessionHolder;
		}

		private WriteTranSessionHolder getSessionHolder() {
			return this.writeTranSessionHolder;
		}
	}

}
