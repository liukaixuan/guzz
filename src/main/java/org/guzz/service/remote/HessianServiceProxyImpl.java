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

import java.net.MalformedURLException;
import java.util.Properties;

import org.guzz.exception.ServiceExecutionException;

import com.caucho.hessian.client.HessianProxyFactory;

/**
 * 
 * Hessian protocol(http://hessian.caucho.com/).
 * 
 * <p>Accepted parameters:rpc.url(remote service url) and all fields that has a POJO style's set-xxx method in {@link HessianProxyFactory}.
 * <br>
 * The corresponding parameter name of the field would be: "rpc." + field name. 
 * eg: rpc.user, rpc.password, rpc.connectionFactoryName, rpc.debug, rpc.overloadEnabled, rpc.chunkedPost, rpc.readTimeout, rpc.hessian2Request.
 * </p>
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class HessianServiceProxyImpl implements RemoteRPCProxy{
	
	private String url ;
	private HessianProxyFactory factory ;
	
	public void startup(Properties props){
		this.url = (String) props.remove(RPC_PARAM_PREFIX + "url") ;
		this.factory = new HessianProxyFactory();
		
		RPCServiceProxyImplFactory.setProperties(factory, props) ;
	}

	public Object getRemoteStub(Class serviceInterface) {
		try {
			return factory.create(serviceInterface, url) ;
		} catch (MalformedURLException e) {
			throw new ServiceExecutionException("Unable to create service for interface:[" + serviceInterface + "]", e) ;
		}
	}	

	public void close(){
	}

}
