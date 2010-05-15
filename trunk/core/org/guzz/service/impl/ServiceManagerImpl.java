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
package org.guzz.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.Service;
import org.guzz.config.ConfigServer;
import org.guzz.exception.GuzzException;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;
import org.guzz.service.ServiceManager;
import org.guzz.util.CloseUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.web.context.ExtendedBeanFactoryAware;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ServiceManagerImpl implements ServiceManager {
	private static transient final Log log = LogFactory.getLog(ServiceManagerImpl.class) ;
	
	protected ConfigServer configServer ;
	
	private GuzzContextImpl guzzContext ;
	
	protected Map services = new HashMap() ;
	
	public ServiceManagerImpl(GuzzContextImpl guzzContext, ConfigServer configServer){
		this.guzzContext = guzzContext ;
		this.configServer = configServer ;
	}
	
	public Service getService(String serviceName) {
		return (Service) services.get(serviceName) ;
	}
	
	public Service createService(String serviceName, String configName, Class serviceImpl){
		ServiceInfo serviceInfo = new ServiceInfo(serviceName, configName, serviceImpl) ;
		
		return createNewService(this.guzzContext, this.configServer, serviceInfo) ;
	}
	
	public void putService(Service service){
		if(log.isInfoEnabled()){
			log.info("registering service:[" + service.getServiceInfo().getServiceName() + "]...") ;
		}
		
		services.put(service.getServiceInfo().getServiceName(), service) ;
	}	

	public void shutdown() {
		Iterator i = services.values().iterator() ;
		
		while(i.hasNext()){
			Service s = (Service) i.next() ;
			if(log.isInfoEnabled()){
				log.info("shutting down service:[" + s.getServiceInfo().getServiceName() + "]...") ;
			}
			
			CloseUtil.close(s) ;
		}
		
		this.services.clear() ;
	}
	
	
	public static Service createNewService(GuzzContextImpl guzzContext, ConfigServer configServer, ServiceInfo serviceInfo){
		Service s = (Service) BeanCreator.newBeanInstance(serviceInfo.getImplClass()) ;
		s.setServiceInfo(serviceInfo) ;
		
		if(s instanceof GuzzContextAware){
			guzzContext.registerContextStartedAware((GuzzContextAware) s) ;
		}
		
		boolean configOK = false ;
		
		ServiceConfig[] scs;
		try {
			scs = configServer.queryConfig(serviceInfo.getConfigName());
			
			configOK = s.configure(scs) ;		
		} catch (IOException e) {
			log.error("query config for service:[" + serviceInfo.getServiceName() + "], configName:[" + serviceInfo.getConfigName() + "] failed.", e) ;
			throw new GuzzException("query config for service:[" + serviceInfo.getServiceName() + "], configName:[" + serviceInfo.getConfigName() + "] failed.", e) ;
		}
		
		if(configOK){
			s.startup() ;
		}else{
			log.info("service:[" + serviceInfo.getServiceName() + "] is not started. configuration not exsit or failed. configName is :[" + serviceInfo.getConfigName() + "]") ;
		}
		
		if(s instanceof ExtendedBeanFactoryAware){
			guzzContext.registerExtendedBeanFactoryAware((ExtendedBeanFactoryAware) s) ;
		}
		
		return s ;
	}

}
