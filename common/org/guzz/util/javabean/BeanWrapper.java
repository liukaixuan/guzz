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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.guzz.exception.ORMException;
import org.guzz.orm.interpreter.JavaTypeHandlers;
import org.guzz.pojo.GuzzProxy;

/**
 * 
 * 用于对POJO对象进行读写。BeanWrapper在初始化后会缓存bean的方法信息，构造完毕后可以重复使用。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BeanWrapper {
	
	private Map propertyDescriptors = new HashMap() ;
	private Class beanClass ;
	
	public BeanWrapper(Class beanClass){
		this.beanClass = beanClass ;
		
		BeanInfo bi;
		try {
			bi = Introspector.getBeanInfo(beanClass);
		} catch (IntrospectionException e) {
			throw new ORMException("fail to contruct beanwrapper:" + beanClass, e) ;
		}
		
		PropertyDescriptor[] pd = bi.getPropertyDescriptors();
		
		for(int i = 0 ; i < pd.length ; i++){
			this.propertyDescriptors.put(pd[i].getName(), pd[i]) ;
		}	
	}
	
	public void setValue(Object beanInstance, String propName, Object value){
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknow property[" + propName + "] in :" + this.beanClass) ;
		}
		
		Method writeMethod = pd.getWriteMethod();
		if (writeMethod != null) {
			try {
				writeMethod.invoke(beanInstance, new Object[]{value});
			} catch (IllegalArgumentException e) {
				throw new ORMException("property:" + propName + " no writable in :" + this.beanClass, e) ;
			} catch (IllegalAccessException e) {
				throw new ORMException("property:" + propName + " no writable in :" + this.beanClass, e) ;
			} catch (InvocationTargetException e) {
				throw new ORMException("property:" + propName + " no writable in :" + this.beanClass, e) ;
			}
		}else{
			throw new ORMException("property:" + propName + " no writable in :" + this.beanClass) ;
		}
	}
	
	/**
	 * 跳过proxy的方法拦截，直接设置对象的原始值。
	 * <p/>
	 * 通过此方法设置的属性，dynamic-update和lazy更新记录无法识别；也就是说如果开启了dynamic-update或者属性为lazy，设置的值在保存到数据库时将丢失。
	 */
	public void setValueUnderProxy(Object beanInstance, String propName, Object value){
		if(!(beanInstance instanceof GuzzProxy)){ //not proxy
			setValue(beanInstance, propName, value) ;
			return ;
		}
		
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknow property[" + propName + "] in :" + this.beanClass) ;
		}
		
		Method writeMethod = pd.getWriteMethod();
		if (writeMethod != null) {
			try {
				((GuzzProxy) beanInstance).invokeProxiedMethod(writeMethod, new Object[]{value}) ;
			} catch (IllegalArgumentException e) {
				throw new ORMException("property:" + propName + " no writable in :" + this.beanClass, e) ;
			}
		}else{
			throw new ORMException("property:" + propName + " no writable in :" + this.beanClass) ;
		}
	}
	
	public void setValueAutoConvert(Object beanInstance, String propName, String value){
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknow property[" + propName + "] in :" + this.beanClass) ;
		}
		
		Method writeMethod = pd.getWriteMethod();
		if (writeMethod != null) {
			Class paramClass = writeMethod.getParameterTypes()[0] ;
			
			try {
				writeMethod.invoke(beanInstance, new Object[]{JavaTypeHandlers.convertValueToType(value, paramClass.getName())});
			} catch (IllegalArgumentException e) {
				throw new ORMException("property:" + propName + " no writable in :" + this.beanClass, e) ;
			} catch (IllegalAccessException e) {
				throw new ORMException("property:" + propName + " no writable in :" + this.beanClass, e) ;
			} catch (InvocationTargetException e) {
				throw new ORMException("property:" + propName + " no writable in :" + this.beanClass, e) ;
			}
		}else{
			throw new ORMException("property:" + propName + " no writable in :" + this.beanClass) ;
		}
	}
	
	public Object getValue(Object beanInstance, String propName){
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknow property[" + propName + "] in :" + this.beanClass) ;
		}
		
		Method readMethod = pd.getReadMethod();
		if (readMethod != null) {
			try {
				return readMethod.invoke(beanInstance, new Object[0]);
			} catch (IllegalArgumentException e) {
				throw new ORMException("property:" + propName + " no readable in :" + this.beanClass, e) ;
			} catch (IllegalAccessException e) {
				throw new ORMException("property:" + propName + " no readable in :" + this.beanClass, e) ;
			} catch (InvocationTargetException e) {
				throw new ORMException("property:" + propName + " no readable in :" + this.beanClass, e) ;
			}
		}else{
			throw new ORMException("property:" + propName + " no readable in :" + this.beanClass) ;
		}
	}
	
	/**
	 * 跳过proxy的方法拦截，直接获取对象的原始值。
	 */
	public Object getValueUnderProxy(Object beanInstance, String propName){
		if(!(beanInstance instanceof GuzzProxy)){ //not proxy
			return getValue(beanInstance, propName) ;
		}
		
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknow property[" + propName + "] in :" + this.beanClass) ;
		}
		
		Method readMethod = pd.getReadMethod();
		if (readMethod != null) {
			try {
				return ((GuzzProxy) beanInstance).invokeProxiedMethod(readMethod, new Object[0]) ;
			} catch (IllegalArgumentException e) {
				throw new ORMException("property:" + propName + " no readable in :" + this.beanClass, e) ;
			}
		}else{
			throw new ORMException("property:" + propName + " no readable in :" + this.beanClass) ;
		}
	}
	
	public Method getReadMethod(String propName){
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknow property[" + propName + "] in :" + this.beanClass) ;
		}
		
		return pd.getReadMethod();
	}
	
	public Method getWriteMethod(String propName){
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknow property[" + propName + "] in :" + this.beanClass) ;
		}
		
		return pd.getWriteMethod() ;
	}
	
	public Class getPropertyType(String propName){
		PropertyDescriptor pd = (PropertyDescriptor) this.propertyDescriptors.get(propName) ;
		if(pd == null){
			throw new ORMException("unknown property:" + propName + " in :" + this.beanClass) ;
		}
		
		return pd.getPropertyType() ;
	}
	
	
	

}
