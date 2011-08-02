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

import java.util.Properties;

/**
 * 
 * 服务配置
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class ServiceConfig implements java.io.Serializable {
		
	/**ServiceConfig的唯一标识，多个ServiceConfig之间不允许重复。*/
	private String uniqueIdentifer ;
	
	private String appName ;
	
	private String configName ;
	
	private String IP ;
	
	private int maxLoad ;
	
	private Properties props ;

	public String getAppName() {
		return appName;
	}

	public void setAppName(String appName) {
		this.appName = appName;
	}

	public String getConfigName() {
		return configName;
	}

	public void setConfigName(String serviceName) {
		this.configName = serviceName;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String ip) {
		IP = ip;
	}

	public int getMaxLoad() {
		return maxLoad;
	}

	public void setMaxLoad(int maxLoad) {
		this.maxLoad = maxLoad;
	}

	public Properties getProps() {
		return props;
	}

	public void setProps(Properties props) {
		this.props = props;
	}

	public String getUniqueIdentifer() {
		return uniqueIdentifer;
	}

	public void setUniqueIdentifer(String uniqueIdentifer) {
		this.uniqueIdentifer = uniqueIdentifer;
	}
	
}
