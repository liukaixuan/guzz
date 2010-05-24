/*
 * Copyright 2008-2010 the original author or authors.
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
package org.guzz.service.core.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xerces.impl.Constants;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.guzz.exception.GuzzException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.mapping.ObjectMappingUtil;
import org.guzz.orm.mapping.ResultMapBasedObjectMapping;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.service.ServiceConfig;
import org.guzz.util.Assert;
import org.guzz.util.ClassUtil;
import org.guzz.util.CloseUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * Provide sqls from the file system. The file name is the "id", and xml content contains the sql statement.
 * <br>One File, One Sql. The file format is similar to the definition in guzz.xml. For example:
 * <pre>
 * &lt;sqlMap dbgroup="default"&gt;
 *	&lt;select orm="userMap"&gt;
 *		select * from @@user
 *		 where 
 *		 	&#64;id = :id 
 *	&lt;/select&gt;
 *	
 *	&lt;orm id="userMap" class="org.guzz.test.UserModel" table="TB_COMMENT" shadow="org.guzz.test.CommentShadowView"&gt;
 *		&lt;result property="id" column="id" type="int"/&gt;
 *	    &lt;result property="userId" column="userId" type="int"/&gt;
 *	    &lt;result property="userName" column="userName" type="string" /&gt;
 *	    &lt;result property="createdTime" column="createdTime" type="datetime" /&gt;
 *	&lt;/orm&gt;
 * &lt;/sqlMap&gt;<pre>
 * 
 * <b>OR, you just want to map the result into a Map:</b>
 * <pre>
 * &lt;sqlMap dbgroup="default"&gt;
 *	&lt;select orm="@java.util.HashMap"&gt;
 *		select * from @@user
 *		 where 
 *		 	&#64;id = :id 
 *	&lt;/select&gt;
 * &lt;/sqlMap&gt;<pre>
 * 
 * The "orm" attribute can be a business name, a global orm defined in guzz.xml, or a local orm in this file.<br>
 * The 'orm' attribute can also be a wildcard fully qualified javabean or java.util.Map class name with the prefix of '@'. 
 * <br>
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class FileDynamicSQLServiceImpl extends AbstractDynamicSQLService {
	
	protected String folder ;
	
	protected String encoding ;
	
	protected boolean overrideSqlInGuzzXML ;
	
	private boolean available = false ;
	
	private boolean useCache ;
	
	protected Map cachedCS = new HashMap() ;

	public CompiledSQL getSql(String id) {
		if(!useCache){
			return loadCompiledSQLById(id) ;
		}
		
		CachedCompiledSQL ccs = getFromCache(id) ;
		if(ccs == null){
			CompiledSQL cs = loadCompiledSQLById(id) ;
			if(cs != null){
				ccs = new CachedCompiledSQL(id, cs) ;
				putToCache(id, ccs) ;
			}
			
			return cs ;
		}else{
			if(isDirtyInCache(ccs)){
				CompiledSQL cs = loadCompiledSQLById(id) ;
				if(cs == null){
					removeFromCache(id) ;
				}else{
					ccs.setCompiledSQL(cs) ;
					putToCache(id, ccs) ;
				}
				return cs ;
			}else{
				return ccs.getCompiledSQL() ;
			}
		}
	}
	
	public CachedCompiledSQL getFromCache(String id){
		return (CachedCompiledSQL) cachedCS.get(id) ;
	}
	
	public void putToCache(String id, CachedCompiledSQL ccs){
		File f = getSqlFile(id) ;
		long time = f.lastModified() ;
		
		ccs.setMark(new Long(time)) ;
		
		cachedCS.put(id, ccs) ;
	}
	
	public void removeFromCache(String id){
		cachedCS.remove(id) ;
	}
	
	public boolean isDirtyInCache(CachedCompiledSQL ccs){
		File f = getSqlFile(ccs.getKey()) ;
		long timeNow = f.lastModified() ;
		
		long timeBefore = ((Long) ccs.getMark()).longValue() ;
		
		return timeNow != timeBefore ;
	}
	
	protected CompiledSQL loadCompiledSQLById(String id){
		File file = getSqlFile(id) ;
		FileInputStream fis = null ;
		
		try {
			fis = new FileInputStream(file) ;
			
			return loadCSFromStream(id, fis) ;
		} catch (Exception e) {
			log.error("cann't load sql. id:{" + id + "], file:" + file.getAbsolutePath()) ;
		}finally{
			CloseUtil.close(fis) ;
		}
		
		return null ;
	}
	
	protected File getSqlFile(String id){
		return new File(folder, id + ".xml") ;
	}
	
	protected CompiledSQL loadCSFromStream(String id, InputStream is) throws Exception{
		SAXReader reader = null;
		Document document = null;
		
		reader = new SAXReader();	
		reader.setValidation(false) ;
			
		//http://apache.org/xml/features/nonvalidating/load-external-dtd"  
		reader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);
		
		InputStreamReader isr = new InputStreamReader(is, encoding) ;
		    
		document = reader.read(isr);
		final Element root = document.getRootElement();
			
		CompiledSQL cs = loadCompiledSQL(id, root) ;
	        
	    return cs ;
	}

	/**
	 * 加载配置的sql语句。sql语句加载时自动和ObjectMapping进行关联。其中在<sqlMap></sqlMap>内定义的orm只在本sqlMap有效，
	 * 不会保存到系统的 @link ObjectMappingManager 中，只对本sqlMap内的sql语句有效。
	 * @return (@link Map) id~~CompiledSQL
	 */
	protected CompiledSQL loadCompiledSQL(String id, Element root) throws IOException, ClassNotFoundException{
		if(!"sqlMap".equals(root.getName())){
			throw new GuzzException("xml document should be in <sqlMap></sqlMap>") ;
		}
		
		String m_dbgroup = root.attributeValue("dbgroup") ;
		
		//select可以接收@xxx的orm，update不允许接收。必须分开。
		Element s_node = (Element) root.selectSingleNode("//sqlMap/select") ;
		if(s_node != null){
			String m_orm = s_node.attributeValue("orm") ;
			String value = s_node.getTextTrim() ;
			value = StringUtil.replaceString(value, "\r\n", " ") ;
			value = StringUtil.replaceString(value, "\n", " ") ;
			
			ObjectMapping map = null ;
				
			if(m_orm.startsWith("@")){
				Class beanCls = ClassUtil.getClass(m_orm.substring(1)) ;
				map = ObjectMappingUtil.createFormBeanMapping(this.guzzContext, beanCls, m_dbgroup) ;
			}else{
				//首先提取本sqlmap内的orm信息，这些orm优先于global orm定义。
				map = this.loadORM(root, m_orm, m_dbgroup) ;
				
				if(map == null){
					if(this.guzzContext.getBusiness(m_orm) != null){
						//build cs with the business name which supports custom table.
						return compiledSQLBuilder.buildCompiledSQL(m_orm, value) ;
					}
					
					map = this.guzzContext.getObjectMappingManager().getStaticObjectMapping(m_orm) ;
				}
			}
			
			if(map == null){
				throw new GuzzException("unknown object mapping:[" + m_orm + "] in:" + s_node.asXML()) ;
			}
			
			return compiledSQLBuilder.buildCompiledSQL(map, value) ;
		}
		
		
		Element u_node = (Element) root.selectSingleNode("//sqlMap/update") ;
		if(u_node != null){
			String m_orm = u_node.attributeValue("orm") ;
			String value = u_node.getTextTrim() ;
			value = StringUtil.replaceString(value, "\r\n", " ") ;
			value = StringUtil.replaceString(value, "\n", " ") ;
			
			//首先提取本sqlmap内的orm信息，这些orm优先于global orm定义。
			ObjectMapping map = this.loadORM(root, m_orm, m_dbgroup) ;
			
			if(map == null){
				if(this.guzzContext.getBusiness(m_orm) != null){
					//build cs with the business name which supports custom table.
					return compiledSQLBuilder.buildCompiledSQL(m_orm, value) ;
				}
				
				map = this.guzzContext.getObjectMappingManager().getStaticObjectMapping(m_orm) ;
			}
			
			if(map == null){
				throw new GuzzException("unknown object mapping:[" + m_orm + "] in:" + u_node.asXML()) ;
			}
			
			CompiledSQL cs = compiledSQLBuilder.buildCompiledSQL(map, value) ;
			
			return cs ;
		}
		
		throw new GuzzException("no sql found for id:[" + id + "]") ;
	}
	
	protected ResultMapBasedObjectMapping loadORM(Element root, String ormId, String parentDbGroup) throws IOException, ClassNotFoundException{
		List ormFragments = root.selectNodes("orm") ;
		if(ormFragments.isEmpty()) return null ;
		
		for(int i = 0 ; i < ormFragments.size() ; i++){
			Element ormFragment = (Element) ormFragments.get(0) ;
			
			String m_id = ormFragment.attributeValue("id") ;
			Assert.assertNotEmpty(m_id, "invalid id. xml is:" + ormFragment.asXML()) ;
			
			if(!ormId.equals(m_id)) continue ;
			
			String m_class = ormFragment.attributeValue("class") ;
			String m_dbgroup = ormFragment.attributeValue("dbgroup") ;
			String shadow = ormFragment.attributeValue("shadow") ;
			String table = ormFragment.attributeValue("table") ;
			
			if(StringUtil.isEmpty(m_dbgroup)){
				m_dbgroup = parentDbGroup ;
			}
			
			ResultMapBasedObjectMapping map =  ObjectMappingUtil.createResultMapping(this.guzzContext, m_id, Class.forName(m_class), m_dbgroup, shadow, table) ;
			
			List results = ormFragment.selectNodes("result") ;
			
			for(int k = 0 ; k < results.size() ; k++){
				Element e = (Element) results.get(k) ;
				
				String loader = e.attributeValue("loader") ;
				String property = e.attributeValue("property") ;
				String column = e.attributeValue("column") ;
				String nullValue = e.attributeValue("null") ;
				String type = e.attributeValue("type") ;
				
				Assert.assertNotEmpty(property, "invalid property. xml is:" + e.asXML()) ;		
				
				if(StringUtil.isEmpty(column)){
					column = property ;
				}
				
				TableColumn col = ObjectMappingUtil.createTableColumn(this.guzzContext, map, property, column, type, loader) ;
				col.setNullValue(nullValue) ;
				
				ObjectMappingUtil.addTableColumn(map, col) ;
			}
		
			return map ;
		}
		
		return null ;
	}
	
	
	public boolean overrideSqlInGuzzXML() {
		return overrideSqlInGuzzXML ;
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs.length == 0){
			log.warn("FileDynamicSQLServiceImpl is not started, no configuration found.") ;
			return false ;
		}
		
		ServiceConfig sc = scs[0] ;
		
		this.folder = sc.getProps().getProperty("folder", ".") ;
		this.encoding = sc.getProps().getProperty("encoding", "UTF-8") ;
		this.overrideSqlInGuzzXML = StringUtil.toBoolean(sc.getProps().getProperty("overrideSqlInGuzzXML"), false) ;
		this.useCache = StringUtil.toBoolean(sc.getProps().getProperty("useCache"), true) ;
		
		return true ;
	}

	public boolean isAvailable() {
		return available ;
	}

	public void shutdown() {
		available = false ;
	}

	public void startup() {
		available = true ;
	}
	
	public static class CachedCompiledSQL {
		private CompiledSQL compiledSQL ;
		private String key ;
		private Serializable mark ;
		
		public CachedCompiledSQL(String key, CompiledSQL compiledSQL){
			this.key = key ;
			this.compiledSQL = compiledSQL ;
		}
				
		public CompiledSQL getCompiledSQL() {
			return compiledSQL;
		}
		
		public void setCompiledSQL(CompiledSQL compiledSQL) {
			this.compiledSQL = compiledSQL;
		}
		
		public String getKey() {
			return key;
		}
		
		public void setKey(String key) {
			this.key = key;
		}

		public Serializable getMark() {
			return mark;
		}

		public void setMark(Serializable mark) {
			this.mark = mark;
		}
	}

}
