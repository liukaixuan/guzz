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
 * 错误参数异常
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class IllegalParameterException extends GuzzException {
	private static final long serialVersionUID = -2932998484723109414L;
	
	private Object paramValue ;
	private String paramName ;
	
	public IllegalParameterException(String s){
		super(s) ;
	}
	
	/**
	 * @param paramName 参数名称
	 * @param paramValue 参数的值
	 */
	public IllegalParameterException(String paramName, Object paramValue){
		super("参数 " + paramName + " 的值 " + paramValue + " 无效！") ;
		this.paramName = paramName ;
		this.paramValue = paramValue ;
	}
	
	public Object getParamValue() {
		return paramValue;
	}
	
	public String getParamName(){
		return paramName ;
	}
	
}
