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

import org.guzz.transaction.WriteTranSession;
import org.springframework.transaction.support.ResourceHolderSupport;

/**
 * Session holder, wrapping a Guzz <code>WriteTranSession</code>.
 * GuzzTransactionManager binds instances of this class to the thread,
 * for a given GuzzContext.
 *
 * <p>Note: This is an SPI class, not intended to be used by applications.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 * @see GuzzTransactionManager
 * @see TransactionManagerUtils
 */
public class WriteTranSessionHolder extends ResourceHolderSupport {

	private WriteTranSession writeTranSession;

	public WriteTranSessionHolder(WriteTranSession writeTranSession) {
		this.writeTranSession = writeTranSession ;
	}

	public WriteTranSession getWriteTranSession() {
		return writeTranSession ;
	}

	public WriteTranSession removeWriteTranSession() {
		return this.writeTranSession = null ;
	}

	public boolean isEmpty() {
		return this.writeTranSession == null ;
	}

	public void clear() {
		super.clear();
		this.writeTranSession = null;
	}

}
