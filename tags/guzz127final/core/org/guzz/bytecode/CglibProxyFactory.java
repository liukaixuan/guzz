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
package org.guzz.bytecode;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.guzz.bytecode.BusinessDescriptor.LazyColumn;
import org.guzz.exception.GuzzException;
import org.guzz.pojo.GuzzProxy;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class CglibProxyFactory implements ProxyFactory{

	public GuzzProxy proxy(BusinessDescriptor descriptor) {
		Class superClass = descriptor.getDomainClass() ;
		
		Enhancer e = new Enhancer() ;
		e.setSuperclass(superClass) ;
		e.setInterfaces(descriptor.getMustProxiedInterfaces()) ;
		e.setCallback(new LazyCallback(descriptor)) ;
		
		return (GuzzProxy) e.create() ;
	}

	static class LazyCallback implements MethodInterceptor{
		private BusinessDescriptor descriptor ;
		
		private boolean[] changedProps ;
		private boolean[] changedLazyProps ;
		
		private boolean recordingPropChange = true ;
		
		/**
		 * dynamic update时，忽略对lazy属性的更新(lazy属性的更新包含在dynamic-update中)。
		 */
		private boolean dynamicUpdateEnable ;
				
		public LazyCallback(BusinessDescriptor descriptor){
			this.descriptor = descriptor ;
			this.dynamicUpdateEnable = descriptor.getBusiness().getTable().isDynamicUpdateEnable() ;
			
			String[] updatableProps = descriptor.getOrderedAllUpdatableProps() ;
			String[] lazyProps = descriptor.getOrderedAllUpdatableLazyProps() ;
						
			changedProps = new boolean[updatableProps.length] ;
			
			if(!dynamicUpdateEnable){
				changedLazyProps = new boolean[lazyProps.length] ;
			}
		}
		
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
			String name = method.getName() ;
			
			if(args.length == 2){
				if("invokeProxiedMethod".equals(name)){//pass the proxy
					MethodProxy mp = MethodProxy.find(obj.getClass(), ReflectUtils.getSignature((Member) args[0])) ;
					return mp.invokeSuper(obj, (Object[]) args[1]) ;
				}
			}
			
			if(args.length == 0){
				if("toString".equals(name)){
					return descriptor.getDomainClass().getName() + "@" + System.identityHashCode(obj);
				}else if("equals".equals(name)){
					return args[0] instanceof Factory && ( ( Factory ) args[0] ).getCallback( 0 ) == this
							? Boolean.TRUE
				            : Boolean.FALSE;
				}else if("hashCode".equals(name)){
					return new Integer(System.identityHashCode(obj));
				}else if("markReading".equals(name)){
					this.recordingPropChange = false ;
					return null ;
				}else if("unmarkReading".equals(name)){
					this.recordingPropChange = true ;
					return null ;
				}else if("getProxiedClass".equals(name)){
					return descriptor.getDomainClass() ;
				}else if("getChangedProps".equals(name)){//do with dynamic update
					int changedPropsCount = 0 ;
					for(int i = 0 ; i < this.changedProps.length ; i++){
						if(changedProps[i]){
							changedPropsCount++ ;
						}
					}
					
					//nothing changed.
					if(changedPropsCount == 0) return new String[0] ;
					
					String[] m_props = new String[changedPropsCount] ;
					String[] updatableProps = descriptor.getOrderedAllUpdatableProps() ;
					
					for(int i = 0, k = 0 ; i < this.changedProps.length ; i++){
						if(changedProps[i]){
							m_props[k++] = updatableProps[i] ;
						}
					}
					
					return m_props ;
				}else if("getChangedLazyProps".equals(name)){
					if(dynamicUpdateEnable){
						throw new GuzzException("error. dynamic-update is enabled.") ;
					}
					
					int changedPropsCount = 0 ;
					for(int i = 0 ; i < this.changedLazyProps.length ; i++){
						if(changedLazyProps[i]){
							changedPropsCount++ ;
						}
					}
					
					//nothing changed.
					if(changedPropsCount == 0) return new String[0] ;
					
					String[] m_props = new String[changedPropsCount] ;
					String[] lazyProps = descriptor.getOrderedAllUpdatableLazyProps() ;
					
					for(int i = 0, k = 0 ; i < this.changedLazyProps.length ; i++){
						if(changedLazyProps[i]){
							m_props[k++] = lazyProps[i] ;
						}
					}
					
					return m_props ;
				}
			}
			
			LazyColumn lc = descriptor.match(name) ;
			
			if(lc != null){
				//do lazy loading
				return lc.loadProperty(obj) ;
			}
			
			if(this.recordingPropChange){
				Integer index = (Integer) descriptor.getIndexOfWritedProp(name) ;
				if(index != null){
					this.changedProps[index.intValue()] = true ;
				}
				
				if(!dynamicUpdateEnable){
					index = (Integer) descriptor.getIndexOfWritedLazyProp(name) ;
					if(index != null){
						this.changedLazyProps[index.intValue()] = true ;
					}
				}
			}
			
			return proxy.invokeSuper(obj, args) ;
		}
		
	}

}


