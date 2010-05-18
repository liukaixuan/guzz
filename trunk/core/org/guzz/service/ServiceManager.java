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

import org.guzz.Service;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ServiceManager{
	
	public Service getService(String serviceName) ;
	
//	public void registerService(Class serviceImpl) ;
	
	/**
	 * 1. 调用configure(ConfigServer)方法初始化<br>
	 * 2. 注册到ServiceManager中对外提供服务。
	 * 
	 */
//	public void registerService(Service service) ;
	
	/**保存已经初始化完毕的Service，ServiceManager不在调用configure和startup()方法*/
	public void registerService(Service service) ;
	
	/**初始化一个Service，并启动Service。但是并不注册中服务中。返回的Service由调用者自己管理和释放。*/
	public Service createService(String serviceName, String configName, Class serviceImpl) ;
	
	public void shutdown() ;

}
