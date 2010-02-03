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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.GuzzException;
import org.guzz.service.remote.RemoteServiceProxy;
import org.guzz.util.javabean.BeanCreator;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractRemoteService extends AbstractService {
	protected transient final Log log = LogFactory.getLog(this.getClass()) ;
	
	protected RemoteServiceProxy remoteServiceProxy ;	
	
	public boolean configure(ServiceConfig[] scs) {
		if(scs == null || scs.length == 0){
			log.warn("remoteServiceProxy proxy not started. no configuration found.") ;
			return false;
		}
		
		//TODO: add support for many servers.
		ServiceConfig sc = scs[0] ;
		String protocol = (String) sc.getProps().remove("protocol") ;
		if(protocol == null){
			throw new GuzzException("property:[protocol] is required. it should be fcn implements RemoteServiceProxy") ;
		}
		
		remoteServiceProxy = (RemoteServiceProxy) BeanCreator.newBeanInstance(protocol) ;
		remoteServiceProxy.startup(sc.getProps()) ;
		
		return true ;
	}

	public boolean isAvailable() {
		return remoteServiceProxy != null ;
	}

	public void shutdown() {
		if(remoteServiceProxy != null){
			remoteServiceProxy.close() ;
		}
	}

}
