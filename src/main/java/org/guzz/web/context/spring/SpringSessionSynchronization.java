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


import org.guzz.transaction.TransactionManager;
import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * Callback for resource cleanup at the end of a Spring-managed JTA transaction,
 * that is, when participating in a JtaTransactionManager transaction.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see TransactionManagerUtils
 */
class SpringSessionSynchronization implements TransactionSynchronization, Ordered {

	private final WriteTranSessionHolder sessionHolder;

	private final TransactionManager transactionManager;

	private final boolean newSession;

	private boolean holderActive = true;

	public SpringSessionSynchronization(WriteTranSessionHolder sessionHolder, TransactionManager transactionManager, boolean newSession) {
		this.sessionHolder = sessionHolder;
		this.transactionManager = transactionManager;
		this.newSession = newSession;
	}

//	/**
//	 * Check whether there is a Hibernate Session for the current JTA
//	 * transaction. Else, fall back to the default thread-bound Session.
//	 */
//	private WriteTranSession getCurrentSession() {
//		return this.sessionHolder.getWriteTranSession() ;
//	}


	public int getOrder() {
		return TransactionManagerUtils.SESSION_SYNCHRONIZATION_ORDER;
	}

	public void suspend() {
		if (this.holderActive) {
			TransactionSynchronizationManager.unbindResource(this.transactionManager);
//			// Eagerly disconnect the Session here, to make release mode "on_close" work on JBoss.
//			getCurrentSession().disconnect();
			
			//TODO: 不清楚怎么做，会出现什么情况，连接先保持吧。如果遇到反馈，再处理。
		}
	}

	public void resume() {
		if (this.holderActive) {
			TransactionSynchronizationManager.bindResource(this.transactionManager, this.sessionHolder);
		}
	}

	public void beforeCompletion() {
		// We'll only get here if there was no specific JTA transaction to handle.
		if (this.newSession) {
			// Default behavior: unbind and close the thread-bound Hibernate Session.
			TransactionSynchronizationManager.unbindResource(this.transactionManager);
			this.holderActive = false;
		}
	}

	public void afterCommit() {
	}

	public void afterCompletion(int status) {
		if (this.newSession) {
			TransactionManagerUtils.closeSession(this.sessionHolder.getWriteTranSession());
		}
	}

	public void flush() {
		
	}

	public void beforeCommit(boolean readOnly) {
		
	}

}
