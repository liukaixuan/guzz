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
package org.guzz.builder;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.xerces.impl.Constants;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.config.ConfigServer;
import org.guzz.dialect.Dialect;
import org.guzz.exception.GuzzException;
import org.guzz.io.FileResource;
import org.guzz.io.Resource;
import org.guzz.orm.Business;
import org.guzz.orm.ColumnORM;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.ShadowTableView;
import org.guzz.orm.mapping.ObjectMappingManager;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.mapping.ResultMapBasedObjectMapping;
import org.guzz.orm.rdms.SimpleTable;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.orm.sql.CompiledSQLBuilder;
import org.guzz.orm.type.SQLDataType;
import org.guzz.pojo.ColumnDataLoader;
import org.guzz.service.ServiceInfo;
import org.guzz.transaction.DBGroup;
import org.guzz.util.Assert;
import org.guzz.util.CloseUtil;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.xml.sax.SAXException;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GuzzConfigFileBuilder {
	
	protected Document rootDoc  ;
	
	protected GuzzContextImpl gf  ;
	
	protected File mainConfigFile ;
	
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
		
        List list = document.selectNodes( "//import" );
        
        for(int i = 0 ; i < list.size() ; i++){
        	Element  n = (Element ) list.get(i) ;
        	
        	String file = n.attribute("resource").getValue() ;
        	File relatedFile = null ;
        	//load included xml file.
        	if(resource instanceof FileResource){
        		relatedFile = ((FileResource) resource).getFile() ;
        	}
        	
        	FileResource fr = new FileResource(relatedFile, file) ;
        	Document includedDoc = null ;
        	
        	try{
        		includedDoc = loadFullConfigFile(fr, encoding) ;
        	}finally{
        		CloseUtil.close(fr) ;
        	}
        	
        	List content = root.content() ;
        	int indexOfPos = content.indexOf(n) ;
        	
        	content.remove(indexOfPos) ;
        	
        	//append inclued docs
        	Element ie = includedDoc.getRootElement() ;        	
        	List ie_children = ie.content() ;
        	
        	for(int k = ie_children.size() - 1 ; k >=0  ; k--){
        		content.add(indexOfPos, ie_children.get(k)) ;
        	}
        }
        
        return document ;
	}
	
	protected void setupMainConfigDocument(GuzzContext gf, Resource r, String encoding) throws DocumentException, IOException, SAXException{
		rootDoc = loadFullConfigFile(r, encoding) ;
		
		if(r instanceof FileResource){
    		this.mainConfigFile = ((FileResource) r).getFile() ;
    	}
	}
	
	public List listDBGroups(){
		/*
		 <tran>
			<dbgroup name="default" masterDBConfigName="masterDB" slaveDBConfigName="slaveDB" dialectName="mysql5dialect" />
			<dbgroup name="log" masterDBConfigName="masterLogDB" defaultDialect="h2dialect" />
		</tran>
		*/
		
		List ls = this.rootDoc.selectNodes("//tran/dbgroup") ;
		
		if(ls == null) return null ;
		if(ls.isEmpty()) return null ;
		
		LinkedList dbGroups = new LinkedList() ;
		
		for(int i = 0 ; i < ls.size() ; i++){
			Element e = (Element) ls.get(i) ;
			
			DBGroup db = new DBGroup() ;
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
				dialectName = "default" ;
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
		List ls = this.rootDoc.selectNodes("//dialect") ;
		
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
				
				SQLDataType sdt = (SQLDataType) BeanCreator.newBeanInstance(className) ;
				
				dialect.registerUserDefinedTypes(typeName, sdt) ;
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
		
		List bus = this.rootDoc.selectNodes("//business") ;
		
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
			
			Class i_cls = null ;
			if(StringUtil.notEmpty(m_interpret)){
				i_cls = Class.forName(m_interpret) ;
			}			
			
			if(StringUtil.isEmpty(m_dbgroup)){
				m_dbgroup = "default" ;
			}
			
			DBGroup db = this.gf.getDBGroup(m_dbgroup) ;
			
			if(StringUtil.isEmpty(m_class)){//从hbm文件中读取className
				Resource r = new FileResource(m_file) ;
				try{
					m_class = HbmXMLBuilder.getDomainClassName(r) ;
				}finally{
					CloseUtil.close(r) ;
				}
			}
						
			Business business = gf.instanceNewGhost(m_name, m_dbgroup, i_cls, Class.forName(m_class)) ;
			if(business.getInterpret() == null){
				throw new GuzzException("cann't create new instance of ghost: " + business.getName()) ;
			}
			
			Resource r = new FileResource(m_file) ;
			
			try{
				POJOBasedObjectMapping map = HbmXMLBuilder.parseHbmStream(gf, db, business, r) ;				
				mappings.addLast(map) ;
			}finally{
				CloseUtil.close(r) ;
			}
		}		
		
        return mappings;
	}
	
	public List listGlobalORMs() throws IOException, ClassNotFoundException{
		List ls = this.rootDoc.selectNodes("//orm") ;
		
		LinkedList list = new LinkedList() ;
		
		if(ls.isEmpty()) return list ;
		
		for(int i = 0 ; i < ls.size() ; i++){
			Element e = (Element) ls.get(i) ;
			ResultMapBasedObjectMapping map = loadORM(null, e) ;
			list.addLast(map) ;
		}
		
		return list ;
	}
	
	protected ResultMapBasedObjectMapping loadORM(String parentDBGroup, Element ormFragment) throws IOException, ClassNotFoundException{
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
		if(StringUtil.isEmpty(m_dbgroup)){
			m_dbgroup = "default" ;
		}
		
		DBGroup db = this.gf.getDBGroup(m_dbgroup) ;
				
		if(StringUtil.isEmpty(m_id)){
			throw new GuzzException("orm id cann't be null. xml is:" + ormFragment.asXML()) ;
		}
		
		//orm的shadow table支持。
		SimpleTable st = new SimpleTable() ;
		if(StringUtil.notEmpty(shadow)){
			ShadowTableView sv = (ShadowTableView) BeanCreator.newBeanInstance(shadow) ;
				
			gf.getShadowTableViewManager().addShadowView(sv) ;
			st.setShadowTableView(sv) ;
		}		
	
		st.setTableName(table) ;
		st.setBusinessName(m_id) ;		
		
		ResultMapBasedObjectMapping map = new ResultMapBasedObjectMapping(db, m_id, Class.forName(m_class), st) ;
		
		List results = ormFragment.selectNodes("result") ;
		
		for(int i = 0 ; i < results.size() ; i++){
			Element e = (Element) results.get(i) ;
			
			String loader = e.attributeValue("loader") ;
			String property = e.attributeValue("property") ;
			String column = e.attributeValue("column") ;
			String nullValue = e.attributeValue("null") ;
			String type = e.attributeValue("type") ;
			
			Assert.assertNotEmpty(property, "invalid property") ;
			
			if(StringUtil.isEmpty(column)){
				column = property ;
			}
			
			TableColumn col = new TableColumn(st) ;
			col.setColName(column) ;
			col.setPropName(property) ;
			col.setType(type) ;
			col.setNullValue(nullValue) ;
			col.setAllowInsert(true) ;
			col.setAllowUpdate(true) ;
			col.setLazy(false) ;
			
			ColumnDataLoader dl = null ;
			if(StringUtil.notEmpty(loader)){
				dl = (ColumnDataLoader) BeanCreator.newBeanInstance(loader) ;
				dl.configure(map, null, property, column) ;
				
				//register the loader
				gf.getDataLoaderManager().addDataLoader(dl) ;
			}
			
			ColumnORM orm = map.createColumnMapping(col, dl) ;
			col.setOrm(orm) ;
			
			st.addColumn(col) ;
		}
		
		return map ;
	}
	
	/**
	 * @return (@link Map) id~~CompiledSQL
	 */
	public Map listConfiguedCompiledSQLs() throws IOException, ClassNotFoundException{
		/*
		<sqlMap>
			....
		</sqlMap>
		
		<sqlMap>
			....
		</sqlMap>
		 */
		
		List sqlMaps = this.rootDoc.selectNodes("//sqlMap") ;
		HashMap sqls = new HashMap() ;
		
		for(int i = 0 ; i < sqlMaps.size() ; i++){
			Element e = (Element) sqlMaps.get(i) ;
			
			sqls.putAll(loadSQLMap(gf.getObjectMappingManager(), gf.getCompiledSQLBuilder(), e)) ;
		}
		
		return sqls ;
	}
	
	/**
	 * 加载配置的sql语句。sql语句加载时自动和ObjectMapping进行关联。其中在<sqlMap></sqlMap>内定义的orm只在本sqlMap有效，
	 * 不会保存到系统的 @link ObjectMappingManager 中，只对本sqlMap内的sql语句有效。
	 * @return (@link Map) id~~CompiledSQL
	 */
	protected Map loadSQLMap(ObjectMappingManager omm, CompiledSQLBuilder compiledSQLBuilder, Element fragment) throws IOException, ClassNotFoundException{
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
			throw new GuzzException("xml document should be in <sqlMap></sqlMap>") ;
		}

		String m_dbgroup = fragment.attributeValue("dbgroup") ;	
				
		//首先提取本sqlmap内的orm信息备用，这些orm优先于global orm定义。
		List orms = fragment.selectNodes("orm") ;
		HashMap local_orms = new HashMap() ;
		
		for(int i = 0 ; i < orms.size() ; i++){
			Element e = (Element) orms.get(i) ;
			ResultMapBasedObjectMapping m_orm = loadORM(m_dbgroup, e) ;
			
			String[] ids = m_orm.getUniqueName() ;
			
			for(int k = 0 ; k < ids.length ; k++){
				local_orms.put(ids[k], m_orm) ;
			}
		}
		
		List select_nodes = fragment.selectNodes("select") ;
		List update_nodes = fragment.selectNodes("update") ;
		
		select_nodes.addAll(update_nodes) ;
		
		HashMap css = new HashMap() ;
		
		for(int i = 0 ; i < select_nodes.size() ; i++){
			Element s_node = (Element) select_nodes.get(i) ;
			String m_id = s_node.attributeValue("id") ;
			String m_orm = s_node.attributeValue("orm") ;
			String value = s_node.getTextTrim() ;
			value = StringUtil.replaceString(value, "\r\n", " ") ;
			value = StringUtil.replaceString(value, "\n", " ") ;
			
			ObjectMapping map = (ObjectMapping) local_orms.get(m_orm) ;
			
			if(map == null){
				map = omm.getObjectMappingByName(m_orm) ;
			}
			
			if(map == null){
				throw new GuzzException("unknown object mapping:[" + m_orm + "] in:" + s_node.asXML()) ;
			}
			
			CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(map, value) ;
			//TODO: find some way to link the markedSQL's param names with the orm's propertyNames to satisify SQLDataType's better user-defined data binding. 
			//
			
			css.put(m_id, cs) ;
		}
		
		return css ;
	}
	
	public ConfigServer loadConfigServer() throws IOException, ClassNotFoundException{
		/*
		 <config-server>
			<server class="org.guzz.config.LocalFileConfigServer">
				<property name="resource" value="guzz_test2.properties" />
			</server>
		 </config-server>
		*/
		
		List es = this.rootDoc.selectNodes("//config-server/server") ;
		
		if(es.isEmpty()){
			return null ;
		}
		
		Element e = (Element) es.get(0) ;
		
		String className = e.attributeValue("class") ;
		if(StringUtil.isEmpty(className)){
			throw new GuzzException("attribute [class] is null. " + e.asXML()) ;
		}
		
		ConfigServer server = (ConfigServer) BeanCreator.newBeanInstance(className) ;
		
		BeanWrapper bw = new BeanWrapper(server.getClass()) ;		
		
		//set params
		List xml_params = e.selectNodes("param") ;
		for(int i = 0 ; i < xml_params.size() ; i++){
			Element xml_param = (Element) xml_params.get(i) ;
			String propName = xml_param.attributeValue("name") ;
			String value = xml_param.attributeValue("value") ;
			
			if(Resource.class.isAssignableFrom(bw.getPropertyType(propName))){				
				FileResource fr = new FileResource(this.mainConfigFile.getParentFile(), value) ;
				bw.setValue(server, propName, fr) ;
			}else{
				bw.setValue(server, propName, value) ;
			}
		}
		
		server.startup() ;
		
		return server ;
	}
	
	public List loadServices() throws IOException, ClassNotFoundException{
		/*
		 <service class="org.guzz.test.sample.SampleTestService1" />
		 <service class="org.guzz.test.sample.SampleTestService2" />
		*/

		LinkedList services = new LinkedList() ;
		
		List es = this.rootDoc.selectNodes("//service") ;
		
		if(es.isEmpty()){
			return services ;
		}
		
		for(int i = 0 ; i < es.size() ; i++){
			Element e = (Element) es.get(i) ;
			ServiceInfo info = new ServiceInfo() ;
			
			String className = e.attributeValue("class") ;
			String serviceName = e.attributeValue("name") ;
			String configName = e.attributeValue("configName") ;
			if(StringUtil.isEmpty(className)){
				throw new GuzzException("attribute [class] is null. " + e.asXML()) ;
			}
			if(StringUtil.isEmpty(serviceName)){
				throw new GuzzException("attribute [serviceName] is null. " + e.asXML()) ;
			}
			if(StringUtil.isEmpty(configName)){
				throw new GuzzException("attribute [configName] is null. " + e.asXML()) ;
			}
			
			info.setConfigName(configName) ;
			info.setServiceName(serviceName) ;
			info.setImplClass(Class.forName(className)) ;
			
			services.add(info) ;
		}
		
		return services ;
	}
	
	//加载properties配置文件
	protected Properties loadPropertyFile(Document doc) throws IOException{
		List ls = doc.selectNodes("//properties/@file") ;
		
		if(ls == null || ls.isEmpty()){
			return null ;
		}else if(ls.size() != 1){ //含有多个Properties file
			throw new GuzzException("only one properties file is supported.") ;
		}
		
		String fileName = ((Attribute) ls.get(0)).getValue() ;
		FileResource r = new FileResource(this.mainConfigFile, fileName) ;
		try{
			Properties p = new Properties() ;
			p.load(r.getInputStream()) ;
			
			return p ;
		}finally{
			CloseUtil.close(r) ;
		}
	}

}
