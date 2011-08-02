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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.service.FactoryService;
import org.guzz.service.ProxyService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;
import org.guzz.service.ServiceManager;
import org.guzz.util.CloseUtil;
import org.guzz.util.StringUtil;
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
	
	public void registerService(Service service){
		if(service instanceof FactoryService){
			service = ((FactoryService) service).createService() ;
		}
		
		String serviceName = service.getServiceInfo().getServiceName() ;
		
		if(log.isInfoEnabled()){
			log.info("registering service:[" + serviceName + "]...") ;
		}
		
		Service oldService = (Service) this.services.get(serviceName) ;
		if(oldService != null){
			log.info("override service from :[" + oldService.getServiceInfo().getImplClass() + "] to [" + service.getServiceInfo().getImplClass() + "]") ;
		}
		
		if(oldService != null && oldService instanceof ProxyService && !(service instanceof ProxyService)){
			((ProxyService) oldService).setServiceImpl(service) ;
		}else{
			services.put(serviceName, service) ;
		}
	}

	public void shutdown() {
		//TODO: services are started in sequence, so shutdown them in order too.
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
	
	public static Service createNewService(final GuzzContextImpl guzzContext, final ConfigServer configServer, final ServiceInfo serviceInfo){
		final Service s = (Service) BeanCreator.newBeanInstance(serviceInfo.getImplClass()) ;
		s.setServiceInfo(serviceInfo) ;
		
		//inject depended services.
		if(serviceInfo.hasDependedServices()){
			Method[] ms = s.getClass().getMethods() ;
			String[] dependsOn = serviceInfo.getDependedServices() ;
			
			for(int i = 0 ; i < dependsOn.length ; i++){
				Service ds = guzzContext.getService(dependsOn[i]) ;
				
				if(ds == null){
					throw new InvalidConfigurationException("depended service [" + dependsOn[i] + "] not found for service: " + serviceInfo.getServiceName()) ;
				}
				
				boolean injected = false ;
				
				//Inject the service.
				for(int k = 0 ; k < ms.length ; k++){
					Method m = ms[k] ;
					
					if(!m.getName().startsWith("set")) continue ;
					if(!m.getName().endsWith("Service")) continue ;
					if(m.getParameterTypes().length != 1) continue ;
					
					if(m.getParameterTypes()[0].isAssignableFrom(ds.getClass())){
						if(injected){
							//Has already injected. 
							throw new InvalidConfigurationException("ambiguous setXXXService methods in service [" + serviceInfo.getServiceName() + "] for depended service [" + dependsOn[i] + "]") ;
						}
						
						try {
							m.invoke(s, new Object[]{ds}) ;
						} catch (IllegalArgumentException e) {
							throw new InvalidConfigurationException("cann't set depended service [" + dependsOn[i] + "] to service: " + serviceInfo.getServiceName(), e) ;
						} catch (IllegalAccessException e) {
							throw new InvalidConfigurationException("cann't set depended service [" + dependsOn[i] + "] to service: " + serviceInfo.getServiceName(), e) ;
						} catch (InvocationTargetException e) {
							throw new InvalidConfigurationException("cann't set depended service [" + dependsOn[i] + "] to service: " + serviceInfo.getServiceName(), e) ;
						}
						
						injected = true ;
					}
				}
				
				if(!injected){
					//Has already injected. 
					throw new InvalidConfigurationException("NO setXXXService method in service [" + serviceInfo.getServiceName() + "] for depended service [" + dependsOn[i] + "] to be injected.") ;
				}
			}
		}
		
		if(s instanceof GuzzContextAware){
			guzzContext.registerContextStartedAware(
					new GuzzContextAware(){
						public void setGuzzContext(GuzzContext guzzContext) {
							((GuzzContextAware) s).setGuzzContext(guzzContext) ;
							startupService(configServer, s) ;
						}
					}
			) ;
		}else{
			startupService(configServer, s) ;
		}
		
		if(s instanceof ExtendedBeanFactoryAware){
			guzzContext.registerExtendedBeanFactoryAware((ExtendedBeanFactoryAware) s) ;
		}
		
		return s ;
	}
	
	private static void startupService(ConfigServer configServer, Service s){
		ServiceInfo serviceInfo = s.getServiceInfo() ;
		String configName = serviceInfo.getConfigName() ;
		boolean configOK = false ;
		
		ServiceConfig[] scs;
		try {
			if(StringUtil.notEmpty(configName)){
				scs = configServer.queryConfig(configName);
			}else{
				scs = new ServiceConfig[0] ;
			}
			
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
	}

}
