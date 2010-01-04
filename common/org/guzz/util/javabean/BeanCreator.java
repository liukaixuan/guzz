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
package org.guzz.util.javabean;

import org.guzz.exception.GuzzException;

/**
 * 
 * 用于创建bean对象。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class BeanCreator {
	
	public static Object newBeanInstance(Class cls){
		try {
			return cls.newInstance() ;
		} catch (InstantiationException e) {
			throw new GuzzException("cann't init bean instance:" + cls, e) ;
		} catch (IllegalAccessException e) {
			throw new GuzzException("cann't init bean instance:" + cls, e) ;
		}
	}
	
	public static Object newBeanInstance(String className){
		try {
			return Class.forName(className).newInstance() ;
		} catch (InstantiationException e) {
			throw new GuzzException("cann't init bean instance:" + className, e) ;
		} catch (IllegalAccessException e) {
			throw new GuzzException("cann't init bean instance:" + className, e) ;
		} catch (ClassNotFoundException e) {
			throw new GuzzException("cann't init bean instance:" + className, e) ;
		}
	}

}
