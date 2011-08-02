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
package org.guzz.util;

import org.guzz.exception.IllegalParameterException;

/**
 * 
 * 
 * @author liu kaixuan
 */
public abstract class Assert {
	
	public static void assertBigger(int param, int toCompare, String errorMsg){
		if(param <= toCompare){
			throw new IllegalParameterException(errorMsg) ;
		}
	}
	
	public static void assertSmaller(int param, int toCompare, String errorMsg){
		if(param >= toCompare){
			throw new IllegalParameterException(errorMsg) ;
		}
	}
	
	public static void assertNotEmpty(String param, String msg){
		if(StringUtil.isEmpty(param)){
			throw new IllegalParameterException(msg) ;
		}
	}
	
	public static void assertNotNull(Object param, String msg){
		if(param == null){
			throw new IllegalParameterException(msg) ;
		}
	}
	
	public static void assertNull(Object param, String msg){
		if(param != null){
			throw new IllegalParameterException(msg) ;
		}
	}
	
	public static void assertTrue(boolean param, String msg){
		if(!param){
			throw new IllegalParameterException(msg) ;
		}
	}
	
	public static void assertFalse(boolean param, String msg){
		if(param){
			throw new IllegalParameterException(msg) ;
		}
	}
	
	public static void assertEquals(int param1, int param2, String msg){
		if(param1 != param2){
			throw new IllegalParameterException(msg) ;
		}
	}
	
	/**
	 * 判断一个给定的资源不为null，如果为null，抛出<code>NoSuchResourceException</code>异常。
	 * @param resource 要判断的资源
	 * @param msg 异常中的说明文字
	 * @exception NoSuchResourceException。如果resource == null
	 */
	public static void assertResouceNotNull(Object resource, String msg){
		if(resource == null){
			throw new IllegalParameterException(msg) ;
		}
	}

	public static void assertEquals(String param1, String params2, String msg) {
		if(param1 == null){
			if(params2 != null){
				throw new IllegalParameterException(msg) ;
			}
		}else{
			if(!param1.equals(params2)){
				throw new IllegalParameterException(msg) ;
			}
		}
		
	}
}
