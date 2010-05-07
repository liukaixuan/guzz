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
package org.guzz.service.remote;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;

/**
 * 
 * Factory and helper methods for <tt>RemoteRPCProxy</tt> framework.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class RPCServiceProxyImplFactory {
	private static final HashMap RPCProxyProviders = new HashMap();
	
	static {
		RPCProxyProviders.put("burlap", BurlapServiceProxyImpl.class.getName()) ;
		RPCProxyProviders.put("hessian", HessianServiceProxyImpl.class.getName()) ;
		RPCProxyProviders.put("phprpc", PHPRPCServiceProxyImpl.class.getName()) ;
	}
	
	public static String getRPCProxyProviderClass(String name){
		return (String) RPCProxyProviders.get(name.toLowerCase()) ;
	}
	
	public static void setProperties(Object providerFactory, Properties props){
		JavaBeanWrapper bw = BeanWrapper.createPOJOWrapper(providerFactory.getClass()) ;
		
		Iterator i = props.entrySet().iterator() ;
		
		while(i.hasNext()){
			Entry e = (Entry) i.next() ;
			String key = (String) e.getKey() ;
			
			if(!key.startsWith(RemoteRPCProxy.RPC_PARAM_PREFIX)){
				continue ;
			}
			
			key = key.substring(RemoteRPCProxy.RPC_PARAM_PREFIX.length()) ;
			String value = (String) e.getValue() ;
			bw.setValueAutoConvert(providerFactory, key, value) ;
			
			i.remove() ;
		}
	}

}
