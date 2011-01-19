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
import java.util.LinkedList;
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
import org.guzz.connection.DBGroup;
import org.guzz.connection.DBGroupManager;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.connection.VirtualDBView;
import org.guzz.dialect.Dialect;
import org.guzz.exception.DaoException;
import org.guzz.exception.GuzzException;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.io.FileResource;
import org.guzz.io.Resource;
import org.guzz.orm.Business;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.orm.ColumnDataLoader;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.ShadowTableView;
import org.guzz.orm.interpreter.BusinessInterpreterManager;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.CompiledSQLManager;
import org.guzz.orm.sql.impl.CompiledSQLBuilderImpl;
import org.guzz.orm.sql.impl.CompiledSQLManagerImpl;
import org.guzz.service.ServiceConfig;
import org.guzz.service.ServiceInfo;
import org.guzz.service.ServiceManager;
import org.guzz.service.core.DatabaseService;
import org.guzz.service.core.DebugService;
import org.guzz.service.core.DynamicSQLService;
import org.guzz.service.core.impl.DebugServiceImpl;
import org.guzz.service.core.impl.DebugServiceProxy;
import org.guzz.service.core.impl.DynamicSQLServiceProxy;
import org.guzz.service.core.impl.MultiMachinesDatabaseServiceImpl;
import org.guzz.service.core.impl.SingleMachineDatabaseServiceImpl;
import org.guzz.service.core.impl.SlowUpdateServiceImpl;
import org.guzz.service.core.impl.SlowUpdateServiceProxy;
import org.guzz.service.impl.ServiceManagerFactory;
import org.guzz.service.impl.ServiceManagerImpl;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.TransactionManagerFactory;
import org.guzz.util.CloseUtil;
import org.guzz.util.StringUtil;
import org.guzz.web.context.ExtendedBeanFactory;
import org.guzz.web.context.ExtendedBeanFactoryAware;
import org.guzz.web.context.GuzzContextAware;

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
	
	private List contextLifeCycles = new LinkedList() ;
	
	/**waiting for GuzzContext's full inited.*/
	private List contextLifeCyclesWaitingStart = new LinkedList() ;
	
	ObjectMappingManager objectMappingManager ;
	
	CompiledSQLBuilder compiledSQLBuilder ;
	
	CompiledSQLManagerImpl compiledSQLManager ;
	
	BusinessInterpreterManager businessInterpreterManager ;
	
	ConfigServer configServer ;
	
	TransactionManager transactionManager ;
	
	ServiceManager serviceManager ;
	
	DebugService debugService ;
	
	DynamicSQLService dynamicSQLService ;
	
	ProxyFactory proxyFactory ;
	
	ExtendedBeanFactory extendedBeanFactory ;
	
	Map globalIdGenerators = new HashMap() ;
	
	private List contextAwareListeners = new LinkedList() ;
	
	private List extendBeanFactoryAwareListeners = new LinkedList() ;
	
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
		this.debugService = new DebugServiceProxy((DebugService) ServiceManagerImpl.createNewService(this, configServer, new ServiceInfo(Service.FAMOUSE_SERVICE.GUZZ_DEBUG, "guzzDebug", DebugServiceImpl.class))) ;
		
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
		//3.1 加载annotated business
		if(builder.hasAnnotatedBusiness()){
			builder.buildGlobalIdGenerators(globalIdGenerators) ;
			
			List aghosts = builder.listAnnotatedBusinessObjectMappings() ;
			for(int i = 0 ; i < aghosts.size() ; i++){
				addNewGhostBusinessToSystem((POJOBasedObjectMapping) aghosts.get(i)) ;	
			}
		}
		//3.2 加载hbm.xml定义的business
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
				
		serviceManager.registerService((Service) debugService) ;
		
		this.dynamicSQLService = (DynamicSQLService) ServiceManagerImpl.createNewService(this, configServer, new ServiceInfo(Service.FAMOUSE_SERVICE.DYNAMIC_SQL, "guzzDynamicSQL", DynamicSQLServiceProxy.class)) ;
		compiledSQLManager.setDynamicSQLService(dynamicSQLService) ;
		serviceManager.registerService((Service) dynamicSQLService) ;
		
		//7. 启动事务
		if(log.isInfoEnabled()){
			log.info("Prepare transactions....") ;
		}
		
		transactionManager = TransactionManagerFactory.buildTransactionFactory(objectMappingManager, compiledSQLManager, 
				compiledSQLBuilder,
				this.debugService, dbGroupManager) ;
				
		Service sus = new SlowUpdateServiceProxy((SlowUpdateServiceImpl) ServiceManagerImpl.createNewService(this, configServer, new ServiceInfo(Service.FAMOUSE_SERVICE.SLOW_UPDATE, "guzzSlowUpdate", SlowUpdateServiceImpl.class))) ;

		serviceManager.registerService(sus) ;
		
		//加载应用自定义Service
		Map services = builder.loadServices() ;
		LinkedList queuedServices = new LinkedList() ;
		while(!services.isEmpty()){
			Iterator i = services.entrySet().iterator() ;
			Entry e = (Entry) i.next() ;
			i.remove() ;
			
			//String serviceName = (String) e.getKey() ;
			ServiceInfo serviceInfo = (ServiceInfo) e.getValue() ;
			
			initUnOrderedService(services, queuedServices, serviceInfo) ;
		}
		
		//One Service could be depend on others. So we have to compute the dependencies, and start the services in the correct order.
		for(int i = 0 ; i < services.size() ; i++){
			ServiceInfo info = (ServiceInfo) services.get(Integer.valueOf(i)) ;
			Service s = ServiceManagerImpl.createNewService(this, configServer, info) ;
			serviceManager.registerService(s) ;
		}
		
		//8. 完成启动
		fullStarted = true ;
		
		//9. 通知组件guzz已经完成启动
		for(int i = 0 ; i < contextAwareListeners.size() ; i++){
			GuzzContextAware aware = (GuzzContextAware) contextAwareListeners.get(i) ;
			aware.setGuzzContext(this) ;
		}
		
		//10. 通知组件开始进行初始化(startup)
		for(int i = 0 ; i < this.contextLifeCyclesWaitingStart.size() ; i++){
			ContextLifeCycle c = (ContextLifeCycle) this.contextLifeCyclesWaitingStart.get(i) ;
			
			c.startup() ;
		}
		this.contextLifeCyclesWaitingStart.clear() ;
	}
	
	protected void initUnOrderedService(Map services, LinkedList queuedServices, ServiceInfo serviceInfo){
		if(serviceInfo.hasDependedServices()){
			queuedServices.addLast(serviceInfo) ;
			
			String[] dependsOn = serviceInfo.getDependedServices() ;
			
			for(int k = 0 ; k < dependsOn.length ; k++){
				for(int i = 0 ; i < queuedServices.size() ; i++){
					String queueServiceName = ((ServiceInfo) queuedServices.get(i)).getServiceName() ;
					
					if(queueServiceName.equals(dependsOn[k])){
						throw new InvalidConfigurationException("cycle dependencies found in guzz services. From [" + queueServiceName + "] to [" + dependsOn[k] + "].") ;
					}
				}
				
				//add depended-services from un-inited-services to the queuedServices
				ServiceInfo si = (ServiceInfo) services.remove(dependsOn[k]) ;
				if(si != null){
					//process the depended service first.
					initUnOrderedService(services, queuedServices, si) ;
				}else{
					//the service may have already been registered to the ServiceManager
				}
			}
			
			//Depended services have been inited. Start the current one.
			Service s = ServiceManagerImpl.createNewService(this, configServer, serviceInfo) ;
			serviceManager.registerService(s) ;
			
			queuedServices.remove(serviceInfo) ;
		}else{
			Service s = ServiceManagerImpl.createNewService(this, configServer, serviceInfo) ;
			serviceManager.registerService(s) ;
		}
	}

	public void setExtendedBeanFactory(ExtendedBeanFactory extendedBeanFactory) {
		this.extendedBeanFactory = extendedBeanFactory;
		
		for(int i = 0 ; i < extendBeanFactoryAwareListeners.size() ; i++){
			ExtendedBeanFactoryAware a = (ExtendedBeanFactoryAware) extendBeanFactoryAwareListeners.get(i) ;
			a.setExtendedBeanFactory(extendedBeanFactory) ;
		}
		
		this.extendBeanFactoryAwareListeners.clear() ;
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
				ds.setServiceInfo(new ServiceInfo(serviceName, serviceName, SingleMachineDatabaseServiceImpl.class)) ;
				ds.configure(scs[0]) ;
				ds.startup() ;
				
				return ds ;
			}else{
				MultiMachinesDatabaseServiceImpl ds = new MultiMachinesDatabaseServiceImpl() ;
				ds.setServiceInfo(new ServiceInfo(serviceName, serviceName, MultiMachinesDatabaseServiceImpl.class)) ;
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
	public Business getBusiness(String name){
		return (Business) ghosts.get(name) ;
	}
	
	protected void addNewGhostBusinessToSystem(POJOBasedObjectMapping map){
		Business b = map.getBusiness() ;
		
		//已经注册过
		if(ghosts.get(b.getName()) != null){
			throw new GuzzException("business [" + b.getName() + "] already exsits.") ;
		}
		
		objectMappingManager.registerObjectMapping(map) ;
		this.compiledSQLManager.addDomainBusiness(map) ;
		
		ghosts.put(b.getDomainClass().getName(), b) ;
		ghosts.put(b.getName(), b) ;
	}
	
	protected void init(){
		//初始化顺序：加载xml文件，构造数据类型，连接ConfigServer读取配置，初始化Service，初始化事务管理。
		this.proxyFactory = new CglibProxyFactory() ;//TODO: read this from config file.
		objectMappingManager = new ObjectMappingManager() ;
		businessInterpreterManager = new BusinessInterpreterManager(this) ;
		dbGroupManager = new DBGroupManager() ;
		compiledSQLBuilder = new CompiledSQLBuilderImpl(this, objectMappingManager) ;
		
		compiledSQLManager = new CompiledSQLManagerImpl(compiledSQLBuilder) ;
	}
	
	public void registerVirtualDBView(VirtualDBView view){
		registerContextLifeCycle(view) ;
	}
	
	public void registerColumnDataLoader(ColumnDataLoader loader){
		registerContextLifeCycle(loader) ;
	}

	public void registerShadowTableView(ShadowTableView view) {
		registerContextLifeCycle(view) ;
	}
	
	public void registerContextLifeCycle(ContextLifeCycle c){
		this.contextLifeCycles.add(c) ;
		
		if(c instanceof GuzzContextAware){
			this.registerContextStartedAware((GuzzContextAware) c) ;
		}
		
		if(this.isFullStarted()){
			c.startup() ;
		}else{
			this.contextLifeCyclesWaitingStart.add(c) ;
		}
		
		if(c instanceof ExtendedBeanFactoryAware){
			this.registerExtendedBeanFactoryAware((ExtendedBeanFactoryAware) c) ;
		}
	}
	
	public void shutdown(){
		for(int i = 0 ; i < this.contextLifeCycles.size() ; i++){
			ContextLifeCycle c = (ContextLifeCycle) this.contextLifeCycles.get(i) ;
			
			try {
				c.shutdown() ;
			} catch (Exception e) {
				log.error("error while shutting down :" + c.getClass(), e) ;
			}
		}
		this.contextLifeCycles.clear() ;
		
		
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
		
		this.globalIdGenerators.clear() ;
				
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
		if(StringUtil.isEmpty(ghostName)){
			throw new GuzzException("business name cann't be empty.") ;
		}
		
		BusinessInterpreter ii = businessInterpreterManager.newInterpreter(ghostName, intepretClass, domainClass) ;
		
		Business business = new Business(ghostName, dbGroup) ;
		business.setInterpret(ii) ; //ii可能由于domainClass为null而为null
		if(domainClass != null){
			business.setDomainClass(domainClass) ;
		}
		
		return business ;
	}
	
	public DBGroup getDBGroup(String groupName) throws DaoException{
		DBGroup g = this.dbGroupManager.getGroup(groupName) ;
				
		return g;
	}
	
	public PhysicsDBGroup getPhysicsDBGroup(String groupName) throws DaoException{
		return this.dbGroupManager.getPhysicsDBGroup(groupName) ;
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

	public ProxyFactory getProxyFactory() {
		return proxyFactory;
	}
	
	/**
	 * Get the global declared Id Generator (from the annotation).
	 */
	public Object getGlobalIdGenerator(String name){
		return globalIdGenerators.get(name) ;
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
	
	/**
	 * Add a callback on guzz's full started.
	 */
	public void registerContextStartedAware(GuzzContextAware aware){
		if(this.fullStarted){
			aware.setGuzzContext(this) ;
		}else{
			this.contextAwareListeners.add(aware) ;
		}
	}
	
	/**
	 * Add a callback on guzz's {@link ExtendedBeanFactory} setted.
	 */
	public void registerExtendedBeanFactoryAware(ExtendedBeanFactoryAware aware){
		if(this.extendedBeanFactory != null){
			aware.setExtendedBeanFactory(extendedBeanFactory) ;
		}else{
			this.extendBeanFactoryAwareListeners.add(aware) ;
		}
	}

}
