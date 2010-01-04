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
package org.guzz;

import org.guzz.service.ServiceChangeCommand;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;

/**
 * 
 * Service用于模块化定义，Service一般为远程的服务，如数据库DataSource Service，过滤词Service，用户认证Service等。
 * Service的具体服务者可以运行在远程，也可以运行在本地。
 * <p>
 * Service初始化过程：
 * 1. 根据Service的类名初始化对象, 
 * 2. 根据GuzzContextAware进行注入, 
 * 3. 调用configure(ConfigServer)方法初始化, 
 * 4. 调用startup(),
 * 5. 保存到ServiceManager中对外服务。
 *</p>
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface Service {
	
	public static final _FamousService FAMOUSE_SERVICE = new _FamousService() ;
	
	static class _FamousService{
		public final String MASTER_DATABASE = "masterDB" ;
		public final String SLAVE_DATABASE = "slaveDB" ;
		public final String GUZZ_DEBUG = "guzzDebug" ;
		public final String SLOW_UPDATE = "slowUpdate" ;
	}
	
//	public void configure(ConfigServer configServer) ;
	
	/**
	 * 如果没有配置此项，@param scs 传入null 
	 * 
	 * @return 是否成功配置，返回true则继续执行startup，否则打印异常并跳过此服务的启动。
	 */
	public boolean configure(ServiceConfig[] scs) ;
	
	/**执行来自中心配置服务器的调整。如停用即将下线的服务器等。*/
	public void executeCommand(ServiceChangeCommand cmd) ;
	
	public ServiceInfo getServiceInfo() ;
	
	public void setServiceInfo(ServiceInfo serviceInfo) ;
	
//	public String getServiceName() ;
//	
//	public void setServiceName(String serviceName) ;
	
	public boolean isAvailable() ;
	
	/**
	 * 初始化Service环境。调用此方法时，GuzzContext已经初始化完毕。
	 * <p/>此方法在 {@link configure(ConfigServer configServer} 方法以及相关引用注入<b>后</b>执行。
	 * */
	public void startup() ;
	
	public void shutdown() ;
	
}
