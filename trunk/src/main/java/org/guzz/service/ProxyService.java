/*
 * Copyright 2008-2010 the original author or authors.
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

import org.guzz.Service;

/**
 * 
 * In guzz, when you register a new service with the same name of a old one, the old service is overrided in the registration map.
 * Any newly required Service of that name returns the new service instance, but in the meantime,  some components in your system may have cached the old instance and never got the chance to update it. This would cause inconsistent.
 * 
 * <p>
 * To solve this problem, we need a service to be replaced in the inner implementation without the reference(pointer) changed. 
 * {@link ProxyService} is just the key to handle this.
 * </p>
 * 
 * <p>
 * If the old service is a subclass of {@link ProxyService}, and the new one is not, 
 * guzz will call the old service's {@link ProxyService#setServiceImpl(newService)} to replace the implementation with the new one without changing references during the registration.
 * </p>
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public abstract class ProxyService implements Service{

	/**
	 * service current used.
	 */
	public abstract Service getServiceImpl() ;
	
	public abstract void setServiceImpl(Service service) ;

	public boolean configure(ServiceConfig[] scs) {
		return getServiceImpl().configure(scs) ;
	}

	public void executeCommand(ServiceChangeCommand cmd) {
		getServiceImpl().executeCommand(cmd) ;
	}

	public ServiceInfo getServiceInfo() {
		return getServiceImpl().getServiceInfo() ;
	}

	public boolean isAvailable() {
		return getServiceImpl().isAvailable() ;
	}

	public void setServiceInfo(ServiceInfo serviceInfo) {
		getServiceImpl().setServiceInfo(serviceInfo) ;
	}

	public void shutdown() {
		getServiceImpl().shutdown() ;
	}

	public void startup() {
		getServiceImpl().startup() ;
	}
	
}
