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
package org.guzz.service;

import java.util.concurrent.TimeUnit;

/**
 * return a given result.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DummyFutureResult<T> extends FutureResult<T> {
	public static final DummyFutureResult NULL = new DummyFutureResult(null) ;
	public static final DummyFutureResult<Boolean> TRUE = new DummyFutureResult<Boolean>(Boolean.TRUE) ;
	public static final DummyFutureResult<Boolean> FALSE = new DummyFutureResult<Boolean>(Boolean.FALSE) ;
			
	private final T value ;
	
	public DummyFutureResult(T value){
		this.value = value ;
	}
	
	public T get() throws Exception{
		return value ;
	}
	
	public T getIgnoreException(){
		return value ;
	}
	
	public T get(long timeout, TimeUnit unit) throws Exception{
		return value ;
	}

	public T getNoWait(boolean suppressException){
		return value ;
	}

	public T getNoQueue(boolean suppressException){
		return value ;
	}
	
	public T getNoQueue(long timeout, TimeUnit unit, boolean suppressException){
		return value ;
	}

}
