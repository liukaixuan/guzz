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
 * 请求的操作资源缺少，或者不可以抛出的异常。<br>
 * 例如：请求一个不存在的文章。
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class NoSuchResourceException extends GuzzException {	
	private static final long serialVersionUID = 7260924168532340233L;
	
	private Object item ;
	
	public NoSuchResourceException(String msg){
		super(msg) ;
	}

	public NoSuchResourceException(Object item) {
		super(item.toString());
		this.item = item;
	}
	
	public NoSuchResourceException(String msg, Object item){
		super(msg) ;
		this.item = item;
	}
	
	public Object getItem(){
		return item ;
	}
	
}
