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

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.xerces.impl.Constants;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Visitor;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.guzz.GuzzContextImpl;
import org.guzz.dao.PersistListener;
import org.guzz.exception.GuzzException;
import org.guzz.id.AssignedIdGenerator;
import org.guzz.id.AutoIncrementIdGenerator;
import org.guzz.id.Configurable;
import org.guzz.id.GUIDIdGenerator;
import org.guzz.id.IdentifierGenerator;
import org.guzz.id.SequenceIdGenerator;
import org.guzz.id.SlientIdGenerator;
import org.guzz.io.Resource;
import org.guzz.orm.Business;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.orm.ObjectMapping.x$ORM;
import org.guzz.orm.interpreter.AbstractBusinessInterpreter;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.SimpleTable;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.pojo.ColumnDataLoader;
import org.guzz.transaction.DBGroup;
import org.guzz.util.Assert;
import org.guzz.util.ClassUtil;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.xml.sax.SAXException;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class HbmXMLBuilder {
	
	static Map generators = new HashMap() ;
	
	static{
		generators.put("increment", AutoIncrementIdGenerator.class.getName()) ;
		generators.put("assigned", AssignedIdGenerator.class.getName()) ;
		generators.put("sequence", SequenceIdGenerator.class.getName()) ;
		generators.put("slient", SlientIdGenerator.class.getName()) ;
		generators.put("guid", GUIDIdGenerator.class.getName()) ;
	}
	
	public static String getDomainClassName(Resource r) throws DocumentException, IOException, SAXException{
		SAXReader reader = null;
		Document document = null;

		reader = new SAXReader();
		reader.setValidation(false) ;
		// http://apache.org/xml/features/nonvalidating/load-external-dtd"  
		reader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);  
		
		document = reader.read(r.getInputStream());
		final Element root = document.getRootElement();
		
		List bus = root.selectNodes("//class") ;
		
		if(bus == null) return null ;
		if(bus.size() != 1){
			throw new DocumentException("too many class name") ;
		}
		
		Element e = (Element) bus.get(0) ;
		
		return e.attributeValue("name") ;
	}
	
	public static POJOBasedObjectMapping parseHbmStream(final GuzzContextImpl gf, final DBGroup dbGroup, final Business business, Resource r) throws DocumentException, IOException, SAXException, ClassNotFoundException{
		SAXReader reader = null;
		Document document = null;

		reader = new SAXReader();
		reader.setValidation(false) ;
		// http://apache.org/xml/features/nonvalidating/load-external-dtd"  
		reader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);  
		
		document = reader.read(r.getInputStream());
		final Element root = document.getRootElement();
		final POJOBasedObjectMapping map = new POJOBasedObjectMapping(gf, dbGroup, business) ;
		final SimpleTable st = new SimpleTable() ;
		business.setTable(st) ;
		
		//properties defined.
		final LinkedList props = new LinkedList() ;
		
		Visitor visitor = new VisitorSupport() {
			
			public void visit(Element e) {				
				
				//遇到了一个类
				if("class".equalsIgnoreCase(e.getName())){
					String className = e.attributeValue("name") ;
					String tableName = e.attributeValue("table") ;
					boolean dynamicUpdate = StringUtil.toBoolean(e.attributeValue("dynamic-update"), false) ;
					
					if(StringUtil.isEmpty(className)){ //如果className为空，采用ghost中携带的class
						className = business.getDomainClass().getName() ;
					}
					
					//TODO: business class should be setted here.
					
					Assert.assertNotEmpty(className, "invalid class name") ;
					Assert.assertNotEmpty(tableName, "invalid table name") ;
					
					Class cls = ClassUtil.getClass(className) ;
					
					map.setDomainClass(cls) ;
					
					//TODO: 按照实际数据库具体类型，采用更加准备的子类进行初始化。
					st.setTableName(tableName) ;
					st.setDynamicUpdate(dynamicUpdate) ;
				}else if("id".equalsIgnoreCase(e.getName())){
					String name = e.attributeValue("name") ;
					String type = e.attributeValue("type") ;
					String column = e.attributeValue("column") ;
					if(StringUtil.isEmpty(column)){
						column = name ;
					}
					
					props.addLast(name) ;
					
					st.setPKColName(column) ;
					st.setPKPropName(name) ;
					
					TableColumn col = new TableColumn() ;
					col.setColName(column) ;
					col.setPropName(name) ;
					col.setType(type) ;
					col.setAllowInsert(true) ;
					col.setAllowUpdate(true) ;
					col.setLazy(false) ;
					col.setDataLoader(null) ;
					
					st.addColumn(col) ;
					
					x$ORM orm = map.addPropertyMap(name, column, type, null, null) ;
					col.setSqlDataType(orm.sqlDataType) ;
				}else if("property".equalsIgnoreCase(e.getName())){
					String name = e.attributeValue("name") ;
					String type = e.attributeValue("type") ;
					String nullValue = e.attributeValue("null") ;
					String column = e.attributeValue("column") ;
					String lazy = e.attributeValue("lazy") ;
					String loader = e.attributeValue("loader") ;
					
					boolean insertIt = StringUtil.toBoolean(e.attributeValue("insert"), true) ;
					boolean updateIt = StringUtil.toBoolean(e.attributeValue("update"), true) ;
					
					if(StringUtil.isEmpty(column)){
						column = name ;
					}
					
					props.addLast(name) ;
															
					TableColumn col = new TableColumn() ;
					col.setColName(column) ;
					col.setPropName(name) ;
					col.setType(type) ;
					col.setNullValue(nullValue) ;
					col.setAllowInsert(insertIt) ;
					col.setAllowUpdate(updateIt) ;
					col.setLazy("true".equalsIgnoreCase(lazy)) ;
					
					if(StringUtil.notEmpty(loader)){
						ColumnDataLoader dl = (ColumnDataLoader) BeanCreator.newBeanInstance(loader) ;
						dl.configure(map, st, name, column) ;
						
						//register the loader
						col.setDataLoader(dl) ;
						gf.getDataLoaderManager().addDataLoader(dl) ;
					}
					
					st.addColumn(col) ;
					
					x$ORM orm = map.addPropertyMap(name, column, type, nullValue, col.getDataLoader()) ;
					col.setSqlDataType(orm.sqlDataType) ;
					
					//是否实现了PersistListener
					if(col.getDataLoader() instanceof PersistListener){
						st.addPersistListener((PersistListener) col.getDataLoader()) ;
					}
				}
			}
		};
		
		root.accept(visitor);
		
		//初始化Interpreter
		BusinessInterpreter bi = business.getInterpret() ;
		if(bi instanceof AbstractBusinessInterpreter){
			((AbstractBusinessInterpreter) bi).initUsingDomainClass(business.getDomainClass(), props) ;
		}
		
		//初始化主键generator
		//读取generator信息
		List generator = root.selectNodes("//class/id/generator") ;
		if(generator.size() != 1){
			throw new GuzzException("id generator is not found.") ;
		}
		
		Element ge = (Element) generator.get(0) ;
		
		String m_clsName = ge.attributeValue("class") ;
		
		if("native".equalsIgnoreCase(m_clsName)){ //native的generator由dialect来定。
			m_clsName = dbGroup.getDialect().getNativeIDGenerator() ;
		}
		
		String realClassName = (String) generators.get(m_clsName) ;
		if(realClassName == null){
			realClassName = m_clsName ;
		}
		
		IdentifierGenerator ig = (IdentifierGenerator) BeanCreator.newBeanInstance(realClassName) ;
		
		//读取generator的配置参数		
		List m_params = ge.selectNodes("param") ;
		Properties p = new Properties() ;
		for(int i = 0 ; i < m_params.size() ; i++){
			Element mp = (Element) m_params.get(i) ;
			p.put(mp.attributeValue("name"), mp.getTextTrim()) ;
		}
		
		if(ig instanceof Configurable){
			((Configurable) ig).configure(dbGroup.getDialect(), map, p) ;						
		}
		
		st.setIdentifierGenerator(ig) ;
		
		return map ;
	}

}
