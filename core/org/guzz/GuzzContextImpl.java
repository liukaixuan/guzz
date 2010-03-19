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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.builder.GuzzConfigFileBuilder;
import org.guzz.builder.HbmXMLBuilder;
import org.guzz.bytecode.CglibProxyFactory;
import org.guzz.bytecode.ProxyFactory;
import org.guzz.config.ConfigServer;
import org.guzz.dialect.Dialect;
import org.guzz.exception.GuzzException;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.io.FileResource;
import org.guzz.io.Resource;
import org.guzz.orm.Business;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.interpreter.BusinessInterpreterManager;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.ShadowTableViewManager;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.orm.sql.impl.CompiledSQLBuilderImpl;
import org.guzz.orm.sql.impl.CompiledSQLManagerImpl;
import org.guzz.pojo.ColumnDataLoaderManager;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;
import org.guzz.service.ServiceManager;
import org.guzz.service.core.DatabaseService;
import org.guzz.service.core.DebugService;
import org.guzz.service.core.impl.DebugServiceImpl;
import org.guzz.service.core.impl.MultiMachinesDatabaseServiceImpl;
import org.guzz.service.core.impl.SingleMachineDatabaseServiceImpl;
import org.guzz.service.core.impl.SlowUpdateServiceImpl;
import org.guzz.service.impl.ServiceManagerFactory;
import org.guzz.service.impl.ServiceManagerImpl;
import org.guzz.transaction.DBGroup;
import org.guzz.transaction.DBGroupManager;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.TransactionManagerFactory;
import org.guzz.util.CloseUtil;
import org.guzz.web.context.ExtendedBeanFactory;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzContextImpl implements GuzzContext{
	
	private transient static final Log log = LogFactory.getLog(GuzzContext.class) ;
		
	private Map dialects ;
		
	private DBGroupManager dbGroupManager = null ;
	
	ObjectMappingManager objectMappingManager ;
	
	CompiledSQLBuilder compiledSQLBuilder ;
	
	CompiledSQLManager compiledSQLManager ;
	
	BusinessInterpreterManager businessInterpreterManager ;
	
	ConfigServer configServer ;
	
	TransactionManager transactionManager ;
	
	ServiceManager serviceManager ;
	
	DebugService debugService ;
	
	ColumnDataLoaderManager columnDataLoaderManager ;
	
	ShadowTableViewManager shadowTableViewManager ;
	
	ProxyFactory proxyFactory ;
	
	ExtendedBeanFactory extendedBeanFactory ;
	
	private boolean fullStarted ;
	
	protected GuzzContextImpl(){
		init() ;
	}
	
	/**
	 * 加载主配置文件，进行初始化。
	 * @throws Exception 
	 */
	protected void initFromMainConfig(Resource config) throws Exception{
		GuzzConfigFileBuilder builder = GuzzConfigFileBuilder.build(this, config, "UTF-8") ;
		if(log.isInfoEnabled()){
			log.info("Loading guzz config file:" + config) ;
		}
		
		//1. 加载dialect初始化数据类型
		Map ds = builder.getConfiguredDialect() ;
		if(ds == null){
			log.warn("dialect(s) not found.") ;
		}else{
			this.dialects = ds ;
		}
		
		//2. 加载config-server信息，与配置服务器进行通讯.
		//需要先建立数据库连接池，根据连接池情况水平分布数据库表。
		if(log.isInfoEnabled()){
			log.info("Contacting Config Server....") ;
		}
		
		configServer = builder.loadConfigServer() ;
		if(configServer == null){
			throw new GuzzException("config-server is not available.") ;
		}
		
		//加载系统内核Service
		this.debugService = (DebugService) ServiceManagerImpl.createNewService(this, configServer, new ServiceInfo(Service.FAMOUSE_SERVICE.GUZZ_DEBUG, "guzzDebug", DebugServiceImpl.class)) ;
		
		//2. 加载数据库连接池
		List groups = builder.listDBGroups() ;
		if(groups == null){
			log.warn("no dbgroup found.") ;
		}else{
			for(int i = 0 ; i < groups.size() ; i++){
				DBGroup m_group = (DBGroup) groups.get(i) ;
				
				this.dbGroupManager.put(m_group.getGroupName(), m_group) ;
			}
		}		
		
		//2. 加载全局ORM
		List globalORMs = builder.listGlobalORMs() ;
		for(int i = 0 ; i < globalORMs.size() ; i++){
			this.objectMappingManager.registerObjectMapping((ObjectMapping) globalORMs.get(i)) ;
		}
		
		//3. 加载ghost business object
		List ghosts = builder.listBusinessObjectMappings() ;
		for(int i = 0 ; i < ghosts.size() ; i++){
			addNewGhostBusinessToSystem((POJOBasedObjectMapping) ghosts.get(i)) ;	
		}		
		
		//4. 加载配置的sql语句
		Map predefinedSQLs = builder.listConfiguedCompiledSQLs() ;
		Iterator entries = predefinedSQLs.entrySet().iterator() ;
		
		while(entries.hasNext()){
			Entry entry = (Entry) entries.next() ;
			String key = (String) entry.getKey() ;
			CompiledSQL cs = (CompiledSQL) entry.getValue() ;
			
			this.compiledSQLManager.addCompliedSQL(key, cs) ;
		}			
		
		//6. 加载Service服务
		if(log.isInfoEnabled()){
			log.info("Loading Services....") ;
		}
		
		serviceManager = ServiceManagerFactory.buildServiceManager(this) ;
		//初始话Service
				
		serviceManager.putService((Service) debugService) ;
		
		//7. 启动事务
		if(log.isInfoEnabled()){
			log.info("Prepare transactions....") ;
		}
		
		transactionManager = TransactionManagerFactory.buildTransactionFactory(objectMappingManager, compiledSQLManager, 
				compiledSQLBuilder,
				this.debugService, dbGroupManager) ;
				
		SlowUpdateServiceImpl sus = (SlowUpdateServiceImpl) ServiceManagerImpl.createNewService(this, configServer, new ServiceInfo(Service.FAMOUSE_SERVICE.SLOW_UPDATE, "guzzSlowUpdate", SlowUpdateServiceImpl.class)) ;

		serviceManager.putService(sus) ;
		
		//加载应用自定义Service
		List services = builder.loadServices() ;
		for(int i = 0 ; i < services.size() ; i++){
			ServiceInfo info = (ServiceInfo) services.get(i) ;
			Service s = ServiceManagerImpl.createNewService(this, configServer, info) ;
			serviceManager.putService(s) ;
		}
		
		//8. 完成启动
		fullStarted = true ;
		
		//9. 通知组件完成全部启动
		this.businessInterpreterManager.onGuzzFullStarted() ;
		this.columnDataLoaderManager.onGuzzFullStarted() ;
		this.shadowTableViewManager.onGuzzFullStarted() ;
	}

	public void setExtendedBeanFactory(ExtendedBeanFactory extendedBeanFactory) {
		this.extendedBeanFactory = extendedBeanFactory;
		
		//通知ExtendedBeanFactory可用。此方法一般落后于onGuzzFullStarted()的调用。
		this.businessInterpreterManager.onExtendedBeanFactorySetted(extendedBeanFactory) ;
		this.columnDataLoaderManager.onExtendedBeanFactorySetted(extendedBeanFactory) ;
		this.shadowTableViewManager.onExtendedBeanFactorySetted(extendedBeanFactory) ;
	}
	
	
	/**
	 * 获取DatabaseService，如果已经创建过直接返回，如果不存在试图创建。
	 * @param serviceName 服务名称
	 * @return 如果不存在对应的服务配置：如果为dubug模式返回null，否则抛出{@link GuzzException}
	 */
	public DatabaseService getOrCreateDataService(String serviceName) throws GuzzException{
		try {
			ServiceConfig[] scs = this.configServer.queryConfig(serviceName) ;
			
			if(scs == null || scs.length == 0){
				if(this.debugService.isDebugMode()){
					return null ;
				}else{
					throw new GuzzException("cann't find database service:[" + serviceName + "].") ;
				}
			}else if(scs.length == 1){
				SingleMachineDatabaseServiceImpl ds = new SingleMachineDatabaseServiceImpl() ;
				ds.configure(scs[0]) ;
				ds.startup() ;
				
				return ds ;
			}else{
				MultiMachinesDatabaseServiceImpl ds = new MultiMachinesDatabaseServiceImpl() ;
				ds.configure(scs) ;
				ds.startup() ;
				
				return ds ;
			}
		} catch (IOException e) {
			throw new GuzzException(e) ;
		}
	}
			
	private Map ghosts = new HashMap() ;
	
	/**根据ghost名称或者域对象的完整的类名获取Ghost对象，如果不存在返回null。*/
	public Business getGhost(String name){
		return (Business) ghosts.get(name) ;
	}
	
	protected void addNewGhostBusinessToSystem(POJOBasedObjectMapping map){
		objectMappingManager.registerObjectMapping(map) ;
		this.compiledSQLManager.addDomainBusiness(map) ;
		
		Business b = map.getBusiness() ;
		ghosts.put(b.getDomainClass().getName(), b) ;
		ghosts.put(b.getName(), b) ;
	}
	
	protected void init(){
		//初始化顺序：加载xml文件，构造数据类型，连接ConfigServer读取配置，初始化Service，初始化事务管理。
		
		this.proxyFactory = new CglibProxyFactory() ;//TODO: read this from config file.
		columnDataLoaderManager = new ColumnDataLoaderManager(this) ;
		shadowTableViewManager = new ShadowTableViewManager(this) ;
		objectMappingManager = new ObjectMappingManager() ;
		businessInterpreterManager = new BusinessInterpreterManager(this) ;
		dbGroupManager = new DBGroupManager() ;
		compiledSQLBuilder = new CompiledSQLBuilderImpl(objectMappingManager) ;
		compiledSQLManager = new CompiledSQLManagerImpl(compiledSQLBuilder) ;
	}
	
//	protected TransactionManager buildNewTranManager(Dialect dialect, DatabaseService mdb, DatabaseService sdb){		
//		return TransactionManagerFactory.buildTransactionFactory(this.objectMappingManager, compiledSQLManager, compiledSQLBuilder, debugService, dbGroupManager);
//	}
	
	public void shutdown(){	
		columnDataLoaderManager.shutdown() ;
		shadowTableViewManager.shutdown() ;
		
		if(serviceManager != null){
			serviceManager.shutdown() ;
		}
		
		if(this.dbGroupManager != null){
			this.dbGroupManager.shutdown() ;
		}
		
		this.businessInterpreterManager.shutdown() ;
		
		if(this.configServer != null){
			this.configServer.shutdown() ;
		}
				
		if(log.isInfoEnabled()){
			log.info("Guzz closed.") ;
		}
	}
	
	
	/**添加hbm领域对象定义文件。添加后 @param resource 不会自动关闭。
	 * @throws Exception */
	public void addHbmConfig(Business business, Resource resource) throws Exception{
		POJOBasedObjectMapping map = HbmXMLBuilder.parseHbmStream(this, this.getDBGroup(business.getDbGroup()), business, resource) ;
		
		if(business.getInterpret() == null){ //如果interpret为null，在完成hbm加载后，重新构建一次。
			BusinessInterpreter ii = businessInterpreterManager.newInterpreter(business.getName(), null, business.getDomainClass()) ;
			if(ii == null) throw new InvalidConfigurationException("cann't create new instance of ghost: " + business.getName()) ;
			business.setInterpret(ii) ;
		}
		
		addNewGhostBusinessToSystem(map) ;
	}
	
	/**添加hbm领域对象定义文件。
	 * @param fileName 完整文件名
	 */
	public void addHbmConfigFile(Business business, String fileName) throws Exception{
		Resource r = new FileResource(fileName) ;
		try{
			addHbmConfig(business, r) ;
		}finally{
			CloseUtil.close(r) ;
		}
	}
	
	public Business instanceNewGhost(String ghostName, String dbGroup, Class intepretClass, Class domainClass) throws ClassNotFoundException{
		BusinessInterpreter ii = businessInterpreterManager.newInterpreter(ghostName, intepretClass, domainClass) ;
		
		Business business = new Business(ghostName, dbGroup) ;
		business.setInterpret(ii) ; //ii可能由于domainClass为null而为null
		if(domainClass != null){
			business.setDomainClass(domainClass) ;
		}
		
		return business ;
	}

//	public Dialect getDefaultDialect() {
//		return defaultDialect;
//	}
	
	public DBGroup getDBGroup(String name) {
		DBGroup g = this.dbGroupManager.getGroup(name) ;
		
		return g;
	}
	
	public Service getService(String serviceName){
		return this.serviceManager.getService(serviceName);
	}
	
	public DebugService getDebugService(){
		return (DebugService) this.serviceManager.getService(Service.FAMOUSE_SERVICE.GUZZ_DEBUG);
	}
	
	public Dialect getDialect(String name) {
		return (Dialect) dialects.get(name) ;
	}

	public ObjectMappingManager getObjectMappingManager() {
		return objectMappingManager;
	}

	public BusinessInterpreterManager getGhostInterpreterManager() {
		return businessInterpreterManager;
	}

	public TransactionManager getTransactionManager() {
		return transactionManager;
	}

	public ConfigServer getConfigServer() {
		return configServer;
	}

	public ServiceManager getServiceManager() {
		return serviceManager;
	}

	public CompiledSQLBuilder getCompiledSQLBuilder() {
		return compiledSQLBuilder;
	}

	public CompiledSQLManager getCompiledSQLManager() {
		return compiledSQLManager;
	}

	public boolean isFullStarted() {
		return fullStarted;
	}

	public ColumnDataLoaderManager getDataLoaderManager() {
		return columnDataLoaderManager;
	}

	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}

	public ShadowTableViewManager getShadowTableViewManager() {
		return shadowTableViewManager;
	}

	public ExtendedBeanFactory getExtendedBeanFactory() {
		return extendedBeanFactory;
	}
	
	public Object getExtendedBean(String beanName) {
		if(this.extendedBeanFactory == null){
			throw new GuzzException("ExtendedBeanFactory is not inited yet. forgot to config it?") ;
		}
		
		return extendedBeanFactory.getBean(beanName);
	}

}
