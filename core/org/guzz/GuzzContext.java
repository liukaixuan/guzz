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

import org.guzz.dialect.Dialect;
import org.guzz.exception.GuzzException;
import org.guzz.io.Resource;
import org.guzz.orm.Business;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.pojo.ColumnDataLoaderManager;
import org.guzz.service.core.DatabaseService;
import org.guzz.service.core.DebugService;
import org.guzz.transaction.DBGroup;
import org.guzz.transaction.TransactionManager;
import org.guzz.web.context.ExtendedBeanFactory;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface GuzzContext {
	
	/**
	 * 获取DatabaseService，如果已经创建过直接返回，如果不存在试图创建。
	 * @param serviceName 服务名称
	 * @return 如果不存在对应的服务配置：如果为dubug模式返回null，否则抛出{@link GuzzException}
	 */
	public DatabaseService getOrCreateDataService(String serviceName) throws GuzzException;
	
	/**
	 * 根据ghost名称或者域对象的完整的类名获取Ghost对象，如果不存在返回null。
	 */
	public Business getGhost(String name);
	
	
	/**
	 * 添加hbm领域对象定义文件。添加后 @param resource 不会自动关闭。
	 * @throws Exception */
	public void addHbmConfig(Business business, Resource resource) throws Exception;
	
	/**
	 * 添加hbm领域对象定义文件。
	 * @param fileName 完整文件名
	 */
	public void addHbmConfigFile(Business business, String fileName) throws Exception;
	
	public Business instanceNewGhost(String ghostName, String dbGroup, Class intepretClass, Class domainClass) throws ClassNotFoundException;
	
	public DBGroup getDBGroup(String name);
	
	public Service getService(String serviceName);
	
	public DebugService getDebugService();
	
	public Dialect getDialect(String name);
	
	public TransactionManager getTransactionManager() ;
	
	public ObjectMappingManager getObjectMappingManager() ;

	public ColumnDataLoaderManager getDataLoaderManager() ;
	
	/**
	 * Retrieve the Extended Bean Factory. for example: a Bean Factory linked to spring's ApplicationContext if the GuzzContext is initialized within springframework.
	 * 
	 * @return could be null
	 */
	public ExtendedBeanFactory getExtendedBeanFactory() ;
	
	/**
	 * Retrieve a bean in the extended bean factory.
	 */
	public Object getExtendedBean(String beanName) ;
	
	public void shutdown() ;

}
