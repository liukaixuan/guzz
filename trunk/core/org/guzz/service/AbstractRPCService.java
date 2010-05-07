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
package org.guzz.service;

import org.guzz.exception.GuzzException;
import org.guzz.service.remote.RPCServiceProxyImplFactory;
import org.guzz.service.remote.RemoteRPCProxy;
import org.guzz.util.javabean.BeanCreator;

/**
 * 
 * remote procedure call service
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractRPCService<T> extends AbstractRemoteService<T> {
	
	protected RemoteRPCProxy remoteRPCProxy ;	
	
	public boolean configure(ServiceConfig[] scs) {
		if(super.configure(scs)){
			//TODO: add support for many servers.
			
			String protocol = (String) scs[0].getProps().remove(RemoteRPCProxy.RPC_PARAM_PREFIX + "protocol") ;
			if(protocol == null){
				throw new GuzzException("property:[protocol] is required. it should be a fcn implements RemoteRPCProxy") ;
			}
			
			String protocolClassName = RPCServiceProxyImplFactory.getRPCProxyProviderClass(protocol) ;
			if(protocolClassName == null){
				protocolClassName = protocol ;
			}
			
			remoteRPCProxy = (RemoteRPCProxy) BeanCreator.newBeanInstance(protocolClassName) ;
			remoteRPCProxy.startup(scs[0].getProps()) ;
			
			return true ;
		}
		
		return false ;
	}

	public boolean isAvailable() {
		return super.isAvailable() && executorService != null ;
	}

	public void shutdown() {
		super.shutdown() ;
		
		if(remoteRPCProxy != null){
			remoteRPCProxy.close() ;
		}
	}

}
