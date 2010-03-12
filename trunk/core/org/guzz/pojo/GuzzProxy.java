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
package org.guzz.pojo;

import java.lang.reflect.Method;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface GuzzProxy {
	
	/**
	 * mark the object is reading and tranforming by the guzz orm. 
	 * 
	 * The proxy should ignore recording "property has been changed."
	 */
	public void markReading() ;
	
	/**
	 * unset the mark.
	 */
	public void unmarkReading() ;
	
	public Class getProxiedClass() ;
	
	/**
	 * invoke the proxied class's orginal method. intercept is ignored.
	 * 
	 * @param method
	 * @param args
	 */
	public Object invokeProxiedMethod(Method method, Object[] args) ;

}
