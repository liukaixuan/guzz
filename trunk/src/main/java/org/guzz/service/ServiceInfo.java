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

import java.io.Serializable;

import org.guzz.util.StringUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ServiceInfo implements Serializable {
	
	private String serviceName ;
	
	private String configName ;
	
	private Class implClass ;
	
	private String[] dependedServices ;
	
	private int usedCount ;
	
	public ServiceInfo(){}
	
	public ServiceInfo(String serviceName, String configName, Class implClass){
		this.serviceName = serviceName ;
		this.configName = configName ;
		this.implClass = implClass ;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String configName) {
		this.configName = configName;
	}

	public Class getImplClass() {
		return implClass;
	}

	public void setImplClass(Class implClass) {
		this.implClass = implClass;
	}

	public int getUsedCount() {
		return usedCount;
	}

	public void setUsedCount(int usedCount) {
		this.usedCount = usedCount;
	}

	public String[] getDependedServices() {
		return dependedServices;
	}

	public void setDependedServices(String[] dependedServices) {
		this.dependedServices = dependedServices;
	}
	
	/**service names separated by comma*/
	public void setDependedServices(String dependedServices) {
		if(StringUtil.isEmpty(dependedServices)){
			this.dependedServices = null ;
		}else{
			dependedServices = StringUtil.replaceString(dependedServices, ";", ",") ;
			String[] temp = StringUtil.splitString(dependedServices, ",") ;
			this.dependedServices = new String[temp.length] ;
			
			for(int i = 0 ; i < temp.length ; i++){
				this.dependedServices[i] = temp[i].trim() ;
			}
		}
	}
	
	public boolean hasDependedServices(){
		return this.dependedServices != null ;
	}
	
}
