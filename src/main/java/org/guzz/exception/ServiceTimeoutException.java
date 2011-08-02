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
package org.guzz.exception;

/**
 * 
 * 服务超时异常。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ServiceTimeoutException extends GuzzException {

	private long timeout ;
	
	public ServiceTimeoutException(long timeout) {
		super();
		this.timeout = timeout ;
	}

	public ServiceTimeoutException(String message, long timeout) {
		super(message);
		this.timeout = timeout ;
	}

	public ServiceTimeoutException(String message, long timeout, Throwable cause) {
		super(message, cause);
		this.timeout = timeout ;
	}

	public ServiceTimeoutException(long timeout, Throwable cause) {
		super(cause);
		this.timeout = timeout ;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

}
