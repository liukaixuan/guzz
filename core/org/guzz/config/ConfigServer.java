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
package org.guzz.config;

import java.io.IOException;

import org.guzz.Service;
import org.guzz.service.ServiceConfig;

/**
 * 
 * 中心配置服务，用于管理系统的主要配置项。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ConfigServer {
	
	/**注册服务，以便在系统配置发生变化时，@link ControlCenter 通知对应的Service */
	public void registerService(String serviceName, Service service) ;
	
	/**
	 * 
	 * Query configuration for a service.
	 * 
	 * @return return new ServiceConfig[0] if no configuration found.
	 */
	public ServiceConfig[] queryConfig(String serviceName) throws IOException ;
	
	public void startup() ;
	
	public void shutdown() ;

}
