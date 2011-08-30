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
package org.guzz.web.context.spring;

import org.guzz.GuzzContext;
import org.guzz.Service;
import org.guzz.exception.GuzzException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 
 * Helper class to export a guzz service as a spring bean.
 * 
 * <p>Perform {@link ApplicationContextAware#setApplicationContext(applicationContext)} if the service implements {@link ApplicationContextAware}</p>
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class GuzzServiceFactoryBean extends AbstractFactoryBean implements ApplicationContextAware {
	
	private GuzzContext guzzContext ;
	
	private ApplicationContext applicationContext ;
	
	private String serviceName ;
	
	private Service service ;

	protected Object createInstance() throws Exception {
		if(guzzContext == null){
			guzzContext = (GuzzContext) this.getBeanFactory().getBean("guzzContext") ;
		}
		
		if(guzzContext == null){
			throw new GuzzException("guzzContext not found. put guzzContext bean in front of this bean.") ;
		}
		
		service = guzzContext.getService(serviceName) ;
		
		if(service == null){
			throw new GuzzException("service not found. service name is:" + this.serviceName) ;
		}
		
		if(service instanceof ApplicationContextAware){
			((ApplicationContextAware) service).setApplicationContext(applicationContext) ;
		}
		
		return service;
	}

	public Class getObjectType() {
		return service.getClass() ;
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext ;
	}

}
