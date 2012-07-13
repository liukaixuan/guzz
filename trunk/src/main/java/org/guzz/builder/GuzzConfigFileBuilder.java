/*
 * Copyright 2008-2012 the original author or authors.
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
package org.guzz.builder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.Constants;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.builder.HbmXMLBuilder.BusinessValidChecker;
import org.guzz.config.ConfigServer;
import org.guzz.connection.PhysicsDBGroup;
import org.guzz.connection.VirtualDBGroup;
import org.guzz.connection.VirtualDBView;
import org.guzz.dialect.Dialect;
import org.guzz.exception.GuzzException;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.io.FileResource;
import org.guzz.io.Resource;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.ObjectMappingUtil;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.mapping.ResultMapBasedObjectMapping;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.sql.TemplatedCompiledSQL;
import org.guzz.orm.type.SQLDataType;
import org.guzz.service.ServiceInfo;
import org.guzz.service.core.TemplatedSQLService;
import org.guzz.transaction.DefaultTranSessionLocatorImpl;
import org.guzz.transaction.SpringTranSessionLocatorImpl;
import org.guzz.transaction.TranSessionLocator;
import org.guzz.util.Assert;
import org.guzz.util.ClassUtil;
import org.guzz.util.CloseUtil;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.xml.sax.SAXException;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzConfigFileBuilder {
	private transient static final Log log = LogFactory.getLog(GuzzConfigFileBuilder.class) ;
	
	protected Element rootDoc  ;
	
	protected GuzzContextImpl gf  ;
	
	protected Resource mainConfigResource ;
	
	private GuzzConfigFileBuilder(){
	}
	
	public static GuzzConfigFileBuilder build(GuzzContextImpl gf, Resource r, String encoding) throws Exception{
		GuzzConfigFileBuilder b = new GuzzConfigFileBuilder() ;
		b.gf = gf ;
		
		b.setupMainConfigDocument(gf, r, encoding) ;
		
		return b ;
	}
	
	//解析Include包含的子配置文件
	protected Document loadFullConfigFile(Resource resource, String encoding) throws DocumentException, IOException, SAXException{
		SAXReader reader = null;
		Document document = null;

		reader = new SAXReader();	
		reader.setValidation(false) ;
		// http://apache.org/xml/features/nonvalidating/load-external-dtd"  
		reader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);  
		
	    InputStreamReader isr = new InputStreamReader(resource.getInputStream(), encoding);
	    
		document = reader.read(isr);
		final Element root = document.getRootElement();
		
        List list = document.selectNodes("//import");
        
        for(int i = 0 ; i < list.size() ; i++){
        	Element  n = (Element ) list.get(i) ;
        	
        	String file = n.attribute("resource").getValue() ;
        	
        	//load included xml file.
        	FileResource fr = new FileResource(resource, file) ;
        	Document includedDoc = null ;
        	
        	try{
        		includedDoc = loadFullConfigFile(fr, encoding) ;
        	}finally{
        		CloseUtil.close(fr) ;
        	}
        	
        	List content = root.content() ;
        	int indexOfPos = content.indexOf(n) ;
        	
        	content.remove(indexOfPos) ;
        	
        	//appends included docs
        	Element ie = includedDoc.getRootElement() ;        	
        	List ie_children = ie.content() ;
        	
        	for(int k = ie_children.size() - 1 ; k >=0  ; k--){
        		content.add(indexOfPos, ie_children.get(k)) ;
        	}
        }
        
        return document ;
	}
	
	protected void setupMainConfigDocument(GuzzContext gf, Resource r, String encoding) throws DocumentException, IOException, SAXException{
		rootDoc = loadFullConfigFile(r, encoding).getRootElement() ;
		
    	this.mainConfigResource = r ;
	}
	
	public List listDBGroups(){
		/*
		 <tran>
			<dbgroup name="default" masterDBConfigName="masterDB" slaveDBConfigName="slaveDB" dialectName="mysql5dialect" />
			<dbgroup name="activeLog" masterDBConfigName="masterLogDB" defaultDialect="h2dialect" />
		
			<virtualdbgroup name="log" dialectName="h2dialect" shadow="xxx.VirtualDBGroupView">
				<dbgroup name="log.old.1" masterDBConfigName="masterLogDB2" />
				<dbgroup name="log.old.2" masterDBConfigName="masterLogDB3" />
				<dbgroup name="log.old.3" masterDBConfigName="masterLogDB4" />
			</virtualdbgroup>
		 </tran>
		*/
		
		
		LinkedList dbGroups = new LinkedList() ;
		
		List rootDBGroups = parseForPhysicsDBGroup(this.rootDoc.selectNodes("tran/dbgroup"), "default") ;
		if(rootDBGroups != null ){
			dbGroups.addAll(rootDBGroups) ;
		}
		
		//Load virtual dbGroup
		List vss = this.rootDoc.selectNodes("tran/virtualdbgroup") ;
		
		if(vss != null && !vss.isEmpty()){
			for(int i = 0 ; i < vss.size() ; i++){
				Element e = (Element) vss.get(i) ;
				
				VirtualDBGroup db = new VirtualDBGroup() ;
				String groupName = e.attributeValue("name") ;
				String dialectName = e.attributeValue("dialectName") ;
				String shadow = e.attributeValue("shadow") ;
				
				if(StringUtil.isEmpty(groupName)){
					db.setGroupName("default") ;
				}else{
					db.setGroupName(groupName) ;
				}
				
				if(StringUtil.isEmpty(dialectName)){
					dialectName = "default" ;
				}
				
				Dialect dt = this.gf.getDialect(dialectName) ;
				if(dt == null){
					throw new InvalidConfigurationException("dialect:[" + dialectName + "] not found for dbgroup:[" + e.asXML() + "]") ;
				}
					
				db.setDialect(dt) ;
				
				//shadow
				if(StringUtil.isEmpty(shadow)){
					throw new InvalidConfigurationException("missing attribute [shadow] in virtualdbgroup:[" + e.asXML() + "]") ;
				}
				
				Object vv = BeanCreator.newBeanInstance(shadow) ;
				
				if(vv instanceof VirtualDBView){
					VirtualDBView vdv = (VirtualDBView) vv ;
					vdv.setConfiguredVirtualDBGroup(db) ;
					
					this.gf.registerVirtualDBView(vdv) ;
					
					db.setVirtualDBGroupView(vdv) ;
				}else{
					throw new InvalidConfigurationException("attribute [shadow] must be a subclass of + " + VirtualDBView.class.getName() + " for virtualdbgroup:[" + e.asXML() + "]") ;
				}
				
				dbGroups.addLast(db) ;
				
				//Load virtualdbgroup's sub dbgroup.
				List subDBGroups = parseForPhysicsDBGroup(e.selectNodes("dbgroup"), dialectName) ;
				if(subDBGroups != null ){
					dbGroups.addAll(subDBGroups) ;
				}
			}
		}
		
		return dbGroups ;
	}
	
	public Class getTranLocator(){
		/*
		 <tran locator="spring">
			.....
		 </tran>
		*/
		
		List vss = this.rootDoc.selectNodes("tran") ;
		
		if(vss != null && !vss.isEmpty()){
			Element e = (Element) vss.get(0) ;
			
			String className = e.attributeValue("locator") ;
			if(StringUtil.isEmpty(className)){
				return null ;
			}
			
			if("solo".equals(className)){
				className = DefaultTranSessionLocatorImpl.class.getName() ;
			}else if("default".equals(className)){
				className = DefaultTranSessionLocatorImpl.class.getName() ;
			}else if("spring".equals(className)){
				className = SpringTranSessionLocatorImpl.class.getName() ;
			}
			
			Class cls =  ClassUtil.getClass(className) ;
			
			if(!TranSessionLocator.class.isAssignableFrom(cls)){
				throw new GuzzException("tran class must be a implementor of:[" + TranSessionLocator.class.getName() + "]") ;
			}
			
			return cls ;
		}
		
		return null ;
	}
	
	protected List parseForPhysicsDBGroup(List segElements, String defaultDialect){
		if(segElements == null) return null ;
		
		if(segElements.isEmpty()) return null ;
		
		LinkedList dbGroups = new LinkedList() ;
		
		for(int i = 0 ; i < segElements.size() ; i++){
			Element e = (Element) segElements.get(i) ;
			
			PhysicsDBGroup db = new PhysicsDBGroup() ;
			String groupName = e.attributeValue("name") ;
			String masterName = e.attributeValue("masterDBConfigName") ;
			String slaveName = e.attributeValue("slaveDBConfigName") ;
			String dialectName = e.attributeValue("dialectName") ;
			
			if(StringUtil.isEmpty(groupName)){
				db.setGroupName("default") ;
			}else{
				db.setGroupName(groupName) ;
			}
			
			if(StringUtil.notEmpty(masterName)){
				db.setMasterDB(this.gf.getOrCreateDataService(masterName)) ;
			}
			
			if(StringUtil.notEmpty(slaveName)){
				db.setSlaveDB(this.gf.getOrCreateDataService(slaveName)) ;
			}
			
			if(StringUtil.isEmpty(dialectName)){
				dialectName = defaultDialect ;
			}
			
			Dialect dt = this.gf.getDialect(dialectName) ;
			if(dt == null){
				throw new GuzzException("dialect:[" + dialectName + "] not found for dbgroup:[" + e.asXML() + "]") ;
			}
				
			db.setDialect(dt) ;
			
			dbGroups.addLast(db) ;
		}
		
		return dbGroups ;
	}
	
	public Map getConfiguredDialect(){
		List ls = this.rootDoc.selectNodes("dialect") ;
		
		if(ls == null) return null ;
		if(ls.isEmpty()) return null ;
		
		HashMap ds = new HashMap() ;
		
		for(int i = 0 ; i < ls.size() ; i++){
			Element e = (Element) ls.get(i) ;
			String d_cls = e.attributeValue("class") ;
			String d_name = e.attributeValue("name") ;
			
			if(StringUtil.isEmpty(d_name)){
				d_name = "default" ;
			}
			
			Dialect dialect = (Dialect) BeanCreator.newBeanInstance(d_cls) ;
			//注册用户自定义的类型
			
			List types = e.selectNodes("type") ;
			for(int j = 0 ; j < types.size() ; j++){
				Element t = (Element) types.get(j) ;
				
				String typeName = t.attributeValue("name") ;
				String className = t.attributeValue("class") ;
				
				Class cls = ClassUtil.getClass(className) ;
				Assert.assertTrue(SQLDataType.class.isAssignableFrom(cls), "user-defined data type must be a instance of type:" + SQLDataType.class.getName()) ;
				
				dialect.registerUserDefinedTypes(typeName, cls) ;
			}
			
			ds.put(d_name, dialect) ;
		}
		
		return ds ;
	}
	
	/**
	 * 加载领域对象
	 * @throws Exception 
	 * @return List (@link POJOBasedObjectMapping)对象列表
	 */
	public List listBusinessObjectMappings() throws Exception{
		/*
		 <business name="user" class="org.guzz.test.User" interpret="" file="classpath:com/guzz/test/User.hbm.xml" />
		 */
		LinkedList mappings = new LinkedList() ;
		
		List bus = this.rootDoc.selectNodes("business") ;
		
		for(int i = 0 ; i < bus.size() ; i++){
			Element e = (Element) bus.get(i) ;
			
			String m_name = e.attributeValue("name") ;
			String m_class = e.attributeValue("class") ;
			String m_interpret = e.attributeValue("interpret") ;
			String m_file = e.attributeValue("file") ;
			String m_dbgroup = e.attributeValue("dbgroup") ;
			
			if(StringUtil.isEmpty(m_name)){
				throw new GuzzException("bussiness name not found. xml:[" + e.asXML() + "]") ;
			}
			
			if(StringUtil.isEmpty(m_file)){
				throw new GuzzException("file not found. xml:[" + e.asXML() + "]") ;
			}
			
			Class i_cls = StringUtil.notEmpty(m_interpret) ? Class.forName(m_interpret) : null ;
			Class d_cls = StringUtil.notEmpty(m_class) ? Class.forName(m_class) : null ;
			
			Resource r = new FileResource(m_file) ;
			
			try{
				POJOBasedObjectMapping map = HbmXMLBuilder.parseHbmStream(gf, m_dbgroup, null, m_name, d_cls, i_cls, r.getInputStream()) ;
				mappings.addLast(map) ;
			}finally{
				CloseUtil.close(r) ;
			}
		}
		
        return mappings;
	}
	
	public void loadScanedBusinessesToGuzz() throws IOException{
		/*
		 <business-scan dbgroup="default" resources="classpath*:org/guzz/test/*.hbm.xml" />
		 */		
		List bus = this.rootDoc.selectNodes("business-scan") ;
		
		for(int i = 0 ; i < bus.size() ; i++){
			Element e = (Element) bus.get(i) ;

			String m_dbgroup = e.attributeValue("dbgroup") ;
			String resources = e.attributeValue("resources") ;
			
			if(StringUtil.isEmpty(resources)){
				throw new GuzzException("attribute resources not found. xml:[" + e.asXML() + "]") ;
			}
			
			PathMatchingResourcePatternResolver pr = new PathMatchingResourcePatternResolver() ;
		    MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(pr);		    
		    org.springframework.core.io.Resource[] rs = pr.getResources(resources) ;
		    
			for(org.springframework.core.io.Resource r : rs){
				if(r.isReadable()){
					try{
						//Load as annotated class first.
						MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);
					    String className = metadataReader.getClassMetadata().getClassName() ;
					    
					    if(gf.getBusiness(className) != null){
					    	log.info("business-scan ignored domain class [" + className + "] in resource [" + r.getURL() + "] business already registered.") ;
					    	continue ;
					    }
					    					    
					    POJOBasedObjectMapping map = JPA2AnnotationsBuilder.parseDomainClass(gf, m_dbgroup, null, Class.forName(className)) ;

						log.info("business-scan add business [" + r.getURL() + "]") ;
						gf.addNewGhostBusinessToSystem(map) ;
					}catch(GuzzException e1){
						log.debug("business-scan ignored invalid resource [" + r.getURL() + "]. msg:" + e1.getMessage()) ;
					}catch(Exception eee){
						//Not a class? Try to interpret it as a hbm.xml file.						
						try{							
							POJOBasedObjectMapping map = HbmXMLBuilder.parseHbmStream(gf, m_dbgroup, 
									new BusinessValidChecker() {
										public boolean shouldParse(Class domainClass) {
											return gf.getBusiness(domainClass.getName()) == null;
										}
									},
									null, null, null, r.getInputStream()) ;
							
							if(map == null){
								log.info("business-scan ignored [" + r.getURL() + "]. business already registered.") ;
						    	continue ;
							}
							
							log.info("business-scan add business [" + r.getURL() + "]") ;
							gf.addNewGhostBusinessToSystem(map) ;
						}catch(GuzzException e1){
							log.debug("business-scan ignored invalid resource [" + r.getURL() + "]. msg:" + e1.getMessage()) ;
						}catch(Exception eeeee){
							log.debug("business-scan ignored invalid resource [" + r.getURL() + "].") ;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Has any annotated businesses declared?
	 */
	public boolean hasAnnotatedBusiness(){
		List bus = this.rootDoc.selectNodes("a-business") ;
		
		return !bus.isEmpty() ;
	}
	
	/**
	 * 构建全局性的Id Generator
	 * @throws Exception 
	 * @return List (@link POJOBasedObjectMapping)对象列表
	 */
	public void buildGlobalIdGenerators(Map globalIds) throws Exception{
		/*
		 <a-business name="user" class="org.guzz.test.User"/>
		*/
		
		List bus = this.rootDoc.selectNodes("a-business") ;
		
		for(int i = 0 ; i < bus.size() ; i++){
			Element e = (Element) bus.get(i) ;
			String m_class = e.attributeValue("class") ;
			
			if(StringUtil.isEmpty(m_class)){
				throw new GuzzException("domain class name not found. xml:[" + e.asXML() + "]") ;
			}
			
			JPA2AnnotationsBuilder.parseForIdGenerators(globalIds, Class.forName(m_class)) ;
		}
	}
		
	/**
	 * 加载annotated领域对象
	 * @throws Exception 
	 * @return List (@link POJOBasedObjectMapping)对象列表
	 */
	public List listAnnotatedBusinessObjectMappings() throws Exception{
		/*
		 <a-business name="user" class="org.guzz.test.User"/>
		 */
		LinkedList mappings = new LinkedList() ;
		
		List bus = this.rootDoc.selectNodes("a-business") ;
		
		for(int i = 0 ; i < bus.size() ; i++){
			Element e = (Element) bus.get(i) ;
			
			String m_name = e.attributeValue("name") ;
			String m_dbgroup = e.attributeValue("dbgroup") ;
			String m_class = e.attributeValue("class") ;
			
			if(StringUtil.isEmpty(m_class)){
				throw new GuzzException("domain class name not found. xml:[" + e.asXML() + "]") ;
			}
			
			POJOBasedObjectMapping map = JPA2AnnotationsBuilder.parseDomainClass(gf, m_dbgroup, m_name, Class.forName(m_class)) ;
			mappings.addLast(map) ;
		}
		
        return mappings;
	}
	
	public List listGlobalORMs() throws IOException, ClassNotFoundException{
		List ls = this.rootDoc.selectNodes("orm") ;
		
		LinkedList list = new LinkedList() ;
		
		if(ls.isEmpty()) return list ;
		
		for(int i = 0 ; i < ls.size() ; i++){
			Element e = (Element) ls.get(i) ;
			ResultMapBasedObjectMapping map = loadORM(gf, null, e) ;
			list.addLast(map) ;
		}
		
		return list ;
	}
	
	protected static ResultMapBasedObjectMapping loadORM(GuzzContext gf, String parentDBGroup, Element ormFragment) throws IOException, ClassNotFoundException{
		/*
		 <orm id="userObjectMap" class="org.guzz.test.UserModel">
			<result property="id" column="pk"/>
		    <result property="name" column="userName"/>
		    <result property="favCount" column="FAV_COUNT"/>
		    <result property="vip" column="VIP_USER"/>
		 </orm>
		 */
		if(!"orm".equals(ormFragment.getName())){
			throw new GuzzException("xml document should be in <orm></orm>") ;
		}
		
		String m_id = ormFragment.attributeValue("id") ;
		String m_class = ormFragment.attributeValue("class") ;
		String m_dbgroup = ormFragment.attributeValue("dbgroup") ;
		String shadow = ormFragment.attributeValue("shadow") ;
		String table = ormFragment.attributeValue("table") ;
		
		if(StringUtil.isEmpty(m_dbgroup)){
			m_dbgroup = parentDBGroup ;
		}
		
		Assert.assertNotEmpty(m_id, "missing attribute [id] in:" + ormFragment.asXML()) ;
		
		//2011-08-18 fix bug，在update操作时，不需要class属性。
		//Assert.assertNotEmpty(m_class, "missing attribute [class] in" + ormFragment.asXML()) ;
		
		ResultMapBasedObjectMapping map =  ObjectMappingUtil.createResultMapping(gf, m_id, StringUtil.isEmpty(m_class) ? null : Class.forName(m_class), m_dbgroup, shadow, table) ;
		
		List results = ormFragment.selectNodes("result") ;
		
		for(int i = 0 ; i < results.size() ; i++){
			Element e = (Element) results.get(i) ;
			
			String loader = e.attributeValue("loader") ;
			String property = e.attributeValue("property") ;
			String column = e.attributeValue("column") ;
			String nullValue = e.attributeValue("null") ;
			String type = e.attributeValue("type") ;
			
			Assert.assertNotEmpty(property, "invalid property. xml is:" + e.asXML()) ;		
			
			if(StringUtil.isEmpty(column)){
				column = property ;
			}
			
			TableColumn col = ObjectMappingUtil.createTableColumn(gf, map, property, column, type, loader) ;
			col.setNullValue(nullValue) ;
			
			ObjectMappingUtil.addTableColumn(map, col) ;
		}
		
		return map ;
	}
	
	/**
	 * @return (@link Map) id~~CompiledSQL
	 */
	public Map listConfiguedCompiledSQLs(TemplatedSQLService templatedSQLService) throws IOException, ClassNotFoundException{
		/*
		<sqlMap>
			....
		</sqlMap>
		
		<sqlMap>
			....
		</sqlMap>
		 */
		
		List sqlMaps = this.rootDoc.selectNodes("sqlMap") ;
		HashMap sqls = new HashMap() ;
		
		for(int i = 0 ; i < sqlMaps.size() ; i++){
			Element e = (Element) sqlMaps.get(i) ;
			
			sqls.putAll(loadSQLMap(gf, templatedSQLService, gf.getObjectMappingManager(), gf.getCompiledSQLBuilder(), e, true)) ;
		}
		
		return sqls ;
	}
	
	/**
	 * 加载配置的sql语句。sql语句加载时自动和ObjectMapping进行关联。其中在<sqlMap></sqlMap>内定义的orm只在本sqlMap有效，
	 * 不会保存到系统的 @link ObjectMappingManager 中，只对本sqlMap内的sql语句有效。
	 * @return (@link Map) id~~CompiledSQL
	 */
	public static Map loadSQLMap(GuzzContext gf, TemplatedSQLService templatedSQLService, ObjectMappingManager omm, CompiledSQLBuilder compiledSQLBuilder, Element fragment, boolean registerIdToTemplatedService) throws IOException, ClassNotFoundException{
		/*
		<sqlMap dbgroup="user">
			<select id="selectUser" orm="user">
				select * from @@user where @id = :id 
			</select>
		
			<update id="updateUserFavCount" orm="userObjectMap">
				update @@user set @favCount = favCount + 1
			</update>
			
			<select id="selectUsers" orm="userObjectMap">
				select @id, @name, @vip, @favCount from @@user
			</select>
					
			<orm id="userObjectMap" class="org.guzz.test.UserModel" dbgroup="user">
				<generator  class="native" >
					<param name="column" value="pk" />
				</generator>
				
				<result property="id" column="pk"/>
			    <result property="name" column="userName"/>
			    <result property="favCount" column="FAV_COUNT"/>
			    <result property="vip" column="VIP_USER"/>
			</orm>
		</sqlMap>
		 */
		
		if(!"sqlMap".equals(fragment.getName())){
			throw new GuzzException("xml fragment should be insided in <sqlMap></sqlMap>") ;
		}

		String m_dbgroup = fragment.attributeValue("dbgroup") ;	
				
		//首先提取本sqlmap内的orm信息备用，这些orm优先于global orm定义。
		List orms = fragment.selectNodes("orm") ;
		HashMap local_orms = new HashMap() ;
		
		for(int i = 0 ; i < orms.size() ; i++){
			Element e = (Element) orms.get(i) ;
			ResultMapBasedObjectMapping m_orm = loadORM(gf, m_dbgroup, e) ;
			
			String[] ids = m_orm.getUniqueName() ;
			
			for(int k = 0 ; k < ids.length ; k++){
				local_orms.put(ids[k], m_orm) ;
			}
		}
		
		HashMap css = new HashMap() ;
		
		//select可以接收@xxx的orm，update不允许接收。必须分开。
		List select_nodes = fragment.selectNodes("select") ;
		for(int i = 0 ; i < select_nodes.size() ; i++){
			Element s_node = (Element) select_nodes.get(i) ;
			String m_id = s_node.attributeValue("id") ;
			String m_orm = s_node.attributeValue("orm") ;
			String resultClass = s_node.attributeValue("result-class") ;
			boolean templated = "true".equalsIgnoreCase(s_node.attributeValue("templated")) ;
			String value = s_node.getTextTrim() ;
			value = StringUtil.replaceString(value, "\r\n", " ") ;
			value = StringUtil.replaceString(value, "\n", " ") ;
			
			if(registerIdToTemplatedService && m_id == null){
				throw new InvalidConfigurationException("attribute id is required! xml:" + s_node.asXML()) ;
			}
			
			Class beanCls = StringUtil.notEmpty(resultClass) ? ClassUtil.getClass(resultClass) : null ;
			CompiledSQL cs = null ;
			
			ObjectMapping localORM = (ObjectMapping) local_orms.get(m_orm) ;
			if(localORM != null){
				if(templated){
					if(registerIdToTemplatedService){
						cs = TemplatedCompiledSQL.buildAndRegisterById(templatedSQLService, localORM, m_id, value) ;
					}else{
						cs = TemplatedCompiledSQL.buildBySql(templatedSQLService, localORM, value) ;
					}
				}else{
					cs = compiledSQLBuilder.buildCompiledSQL(localORM, value) ;
				}
			}else{
				if(templated){
					if(registerIdToTemplatedService){
						cs = TemplatedCompiledSQL.buildAndRegisterById(templatedSQLService, m_orm, m_id, value) ;
					}else{
						cs = TemplatedCompiledSQL.buildBySql(templatedSQLService, m_orm, value) ;
					}
				}else{
					cs = compiledSQLBuilder.buildCompiledSQL(m_orm, value) ;
				}
			}
			
			if(beanCls != null){
				cs.setResultClass(beanCls) ;
			}
			
			//Register parameters' types.
			loadParamPropsMapping(cs, (Element) s_node.selectSingleNode("paramsMapping")) ;
			
			css.put(m_id, cs) ;
		}
		
		List update_nodes = fragment.selectNodes("update") ;
		for(int i = 0 ; i < update_nodes.size() ; i++){
			Element s_node = (Element) update_nodes.get(i) ;
			String m_id = s_node.attributeValue("id") ;
			String m_orm = s_node.attributeValue("orm") ;
			boolean templated = "true".equalsIgnoreCase(s_node.attributeValue("templated")) ;
			String value = s_node.getTextTrim() ;
			value = StringUtil.replaceString(value, "\r\n", " ") ;
			value = StringUtil.replaceString(value, "\n", " ") ;
			
			ObjectMapping localORM = (ObjectMapping) local_orms.get(m_orm) ;
			CompiledSQL cs = null ;
			
			if(localORM != null){
				if(templated){
					cs = compiledSQLBuilder.buildTemplatedCompiledSQL(localORM, value) ;
				}else{
					cs = compiledSQLBuilder.buildCompiledSQL(localORM, value) ;
				}
			}else{
				if(templated){
					cs = compiledSQLBuilder.buildTemplatedCompiledSQL(m_orm, value) ;
				}else{
					cs = compiledSQLBuilder.buildCompiledSQL(m_orm, value) ;
				}
			}
			
			//Register parameters' types.
			loadParamPropsMapping(cs, (Element) s_node.selectSingleNode("paramsMapping")) ;
			
			css.put(m_id, cs) ;
		}
		
		return css ;
	}
	
	/**
	 * Load query params for the sql segment into the {@link CompiledSQL}.
	 * 
	 * <p/>The XML segment format should be:
	 * <pre>
	 * &lt;paramsMapping&gt;
	 *		&lt;map paramName="city" propName="cityName" /&gt;
	 *		&lt;map paramName="timeStart" type="datetime|yyyy-MM-dd HH:mm:ss" /&gt;
	 *		&lt;map paramName="voteId" propName="voteId" /&gt;
	 * &lt;/paramsMapping&gt;
	 * </pre>
	 * 
	 * @return return null if paramsMappingNode is null.
	 */
	public static void loadParamPropsMapping(CompiledSQL cs, Element paramsMappingNode) throws IOException, ClassNotFoundException{
		if(paramsMappingNode == null){
			return ;
		}
		
		List ps = paramsMappingNode.selectNodes("map") ;
		
		for(int i = 0 ; i < ps.size() ; i++){
			Element xml_p = (Element) ps.get(i) ;
			
			String paramName = xml_p.attributeValue("paramName") ;
			String propName = xml_p.attributeValue("propName") ;
			String type = xml_p.attributeValue("type") ;
			
			//Link the markedSQL's parameter names with the orm's propertyNames to satisfy SQLDataType's better user-defined data binding. 
			if(StringUtil.notEmpty(type)){
				cs.registerParamType(paramName, type) ;
			}else{
				cs.addParamPropMapping(paramName, propName) ;
			}
		}
	}
	
	public ConfigServer loadConfigServer() throws IOException, ClassNotFoundException{
		/*
		 <config-server>
			<server class="org.guzz.config.LocalFileConfigServer">
				<property name="resource" value="guzz_test2.properties" />
			</server>
		 </config-server>
		*/
		
		List es = this.rootDoc.selectNodes("config-server/server") ;
		
		if(es.isEmpty()){
			return null ;
		}
		
		Element e = (Element) es.get(0) ;
		
		String className = e.attributeValue("class") ;
		if(StringUtil.isEmpty(className)){
			throw new GuzzException("attribute [class] is null. " + e.asXML()) ;
		}
		
		ConfigServer server = (ConfigServer) BeanCreator.newBeanInstance(className) ;
		
		JavaBeanWrapper bw = BeanWrapper.createPOJOWrapper(server.getClass()) ;		
		
		//set params
		List xml_params = e.selectNodes("param") ;
		for(int i = 0 ; i < xml_params.size() ; i++){
			Element xml_param = (Element) xml_params.get(i) ;
			String propName = xml_param.attributeValue("name") ;
			String value = xml_param.attributeValue("value") ;
			
			if(Resource.class.isAssignableFrom(bw.getPropertyType(propName))){				
				FileResource fr = new FileResource(mainConfigResource, value) ;
				try{
					bw.setValue(server, propName, fr) ;
				}finally{
					CloseUtil.close(fr) ;
				}
			}else{
				bw.setValue(server, propName, value) ;
			}
		}
		
		server.startup() ;
		
		return server ;
	}
	
	public Map loadServices() throws IOException, ClassNotFoundException{
		/*
		 <service class="org.guzz.test.sample.SampleTestService1" />
		 <service class="org.guzz.test.sample.SampleTestService2" />
		*/

		HashMap services = new HashMap() ;
		
		List es = this.rootDoc.selectNodes("service") ;
		
		if(es.isEmpty()){
			return services ;
		}
		
		for(int i = 0 ; i < es.size() ; i++){
			Element e = (Element) es.get(i) ;
			ServiceInfo info = new ServiceInfo() ;
			
			String className = e.attributeValue("class") ;
			String serviceName = e.attributeValue("name") ;
			String configName = e.attributeValue("configName") ;
			String dependsOn = e.attributeValue("dependsOn") ;
			if(StringUtil.isEmpty(className)){
				throw new GuzzException("attribute [class] is null. " + e.asXML()) ;
			}
			if(StringUtil.isEmpty(serviceName)){
				throw new GuzzException("attribute [serviceName] is null. " + e.asXML()) ;
			}
			
			info.setConfigName(configName) ;
			info.setServiceName(serviceName) ;
			info.setImplClass(Class.forName(className)) ;
			info.setDependedServices(dependsOn) ;
			
			services.put(serviceName, info) ;
		}
		
		return services ;
	}
	
	//加载properties配置文件
	protected Properties loadPropertyFile(Document doc) throws IOException{
		List ls = doc.selectNodes("properties/@file") ;
		
		if(ls == null || ls.isEmpty()){
			return null ;
		}else if(ls.size() != 1){ //含有多个Properties file
			throw new GuzzException("only one properties file is supported.") ;
		}
		
		String fileName = ((Attribute) ls.get(0)).getValue() ;
		FileResource r = new FileResource(mainConfigResource, fileName) ;
		try{
			Properties p = new Properties() ;
			p.load(r.getInputStream()) ;
			
			return p ;
		}finally{
			CloseUtil.close(r) ;
		}
	}

}
