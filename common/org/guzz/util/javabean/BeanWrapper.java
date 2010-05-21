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

import java.lang.reflect.Method;

import org.guzz.util.JRTInfo;

/**
 * 
 * 用于对POJO对象进行读写。BeanWrapper在初始化后会缓存bean的方法信息，构造完毕后可以重复使用。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class BeanWrapper {
	
	public static JavaBeanWrapper createPOJOWrapper(Class beanClass){
		return new JavaBeanWrapper(beanClass) ;
	}
		
	public abstract void setValue(Object beanInstance, String propName, Object value) ;
	
	/**
	 * 跳过proxy的方法拦截，直接设置对象的原始值。
	 * <p/>
	 * 通过此方法设置的属性，dynamic-update和lazy更新记录无法识别；也就是说如果开启了dynamic-update或者属性为lazy，设置的值在保存到数据库时将丢失。
	 */
	public abstract void setValueUnderProxy(Object beanInstance, String propName, Object value) ;
	
	public abstract Object getValue(Object beanInstance, String propName) ;
	
	/**
	 * 跳过proxy的方法拦截，直接获取对象的原始值。避免lazy方法进行lazy调用。
	 */
	public abstract Object getValueUnderProxy(Object beanInstance, String propName) ;
	
	public abstract Method getReadMethod(String propName) ;
	
	public abstract Method getWriteMethod(String propName) ;
	
	public abstract Class getPropertyType(String propName) ;
	
	public String getPropertyTypeName(String propName){
		Class type = getPropertyType(propName) ;
		
		if(JRTInfo.isJDK50OrHigher()){
			if(type.isEnum()){
				return  "enum.ordinal|" + type.getName() ;
			}
		}
		
		return type.getName() ;
	}

}
