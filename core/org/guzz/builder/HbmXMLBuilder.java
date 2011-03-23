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
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.xerces.impl.Constants;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Visitor;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;
import org.guzz.GuzzContextImpl;
import org.guzz.connection.DBGroup;
import org.guzz.exception.GuzzException;
import org.guzz.id.Configurable;
import org.guzz.id.IdentifierGenerator;
import org.guzz.id.IdentifierGeneratorFactory;
import org.guzz.io.Resource;
import org.guzz.orm.Business;
import org.guzz.orm.CustomTableView;
import org.guzz.orm.ShadowTableView;
import org.guzz.orm.mapping.ObjectMappingUtil;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.SimpleTable;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.util.Assert;
import org.guzz.util.ClassUtil;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;
import org.guzz.web.context.GuzzContextAware;
import org.xml.sax.SAXException;


/**
 * 
 * parse hbm.xml formatting orm.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class HbmXMLBuilder {
	
	public static String getDomainClassName(Resource r) throws DocumentException, IOException, SAXException{
		SAXReader reader = null;
		Document document = null;

		reader = new SAXReader();
		reader.setValidation(false) ;
		// http://apache.org/xml/features/nonvalidating/load-external-dtd"  
		reader.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.LOAD_EXTERNAL_DTD_FEATURE, false);  
		
		document = reader.read(r.getInputStream());
		final Element root = document.getRootElement();
		String packageName = root.attributeValue("package") ;
		
		List bus = root.selectNodes("//class") ;
		
		if(bus == null) return null ;
		if(bus.size() != 1){
			throw new DocumentException("too many class name") ;
		}
		
		Element e = (Element) bus.get(0) ;
		
		String className = e.attributeValue("name") ;
		if(StringUtil.notEmpty(packageName)){
			className = packageName + "." + className ;
		}
		
		return className ;
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
		final SimpleTable st = new SimpleTable(dbGroup.getDialect()) ;
		final POJOBasedObjectMapping map = new POJOBasedObjectMapping(gf, dbGroup, st) ;
		
		business.setTable(st) ;
		business.setMapping(map) ;
		
		//关联business名称
		if(business.getName() != null){
			st.setBusinessName(business.getName()) ;
		}else{
			st.setBusinessName(business.getDomainClass().getName()) ;
		}
		
		//properties defined.
		final LinkedList props = new LinkedList() ;
		
		Visitor visitor = new VisitorSupport() {
			
			private String packageName ;
			
			public void visit(Element e) {
				
				//package
				if("hibernate-mapping".equalsIgnoreCase(e.getName()) || "guzz-mapping".equalsIgnoreCase(e.getName())){
					this.packageName = e.attributeValue("package") ;
					
				}else if("class".equalsIgnoreCase(e.getName())){
					String className = e.attributeValue("name") ;
					String tableName = e.attributeValue("table") ;
					String shadow = e.attributeValue("shadow") ;
					boolean dynamicUpdate = StringUtil.toBoolean(e.attributeValue("dynamic-update"), false) ;
					
					if(StringUtil.notEmpty(this.packageName)){
						className = this.packageName + "." + className ;
					}
					
					//business中已经提前从hbml.xml中解析到了class name，并且按照business定义优先级作出最高优先级的class选择。
					Class cls = business.getDomainClass() ;
					if(cls == null){ //动态添加的business，没有按照流程设置domainClassName
						cls = ClassUtil.getClass(className) ;
					}
										
					Assert.assertNotNull(cls, "invalid class name") ;
					Assert.assertNotEmpty(tableName, "invalid table name") ;
					
					JavaBeanWrapper configBeanWrapper = BeanWrapper.createPOJOWrapper(cls) ;
					business.setDomainClass(cls) ;
					business.setConfiguredBeanWrapper(configBeanWrapper) ;
					
					map.setBusiness(business) ;
					
					//shadow设置要在tableName之前。
					if(StringUtil.notEmpty(shadow)){
						ShadowTableView sv = (ShadowTableView) BeanCreator.newBeanInstance(shadow) ;
						sv.setConfiguredTableName(tableName) ;
						
						//CustomTableView是一类特殊的ShadowTableView
						if(sv instanceof CustomTableView){
							CustomTableView ctv = (CustomTableView) sv ;
							ctv.setConfiguredObjectMapping(map) ;
							
							st.setCustomTableView(ctv) ;
						}

						st.setShadowTableView(sv) ;
						gf.registerShadowTableView(sv) ;
					}
					
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
					
					TableColumn col = ObjectMappingUtil.createTableColumn(gf, map, name, column, type, null) ;

					st.addPKColumn(col) ;
					
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
					
					TableColumn col = ObjectMappingUtil.createTableColumn(gf, map, name, column, type, loader) ;
					col.setNullValue(nullValue) ;
					col.setAllowInsert(insertIt) ;
					col.setAllowUpdate(updateIt) ;
					col.setLazy("true".equalsIgnoreCase(lazy)) ;
					
					st.addColumn(col) ;
				}
			}
		};
		
		root.accept(visitor);
				
		//初始化主键generator
		//读取generator信息
		List generator = root.selectNodes("//class/id/generator") ;
		if(generator.size() != 1){
			throw new GuzzException("id generator is not found for resource: " + r) ;
		}
		
		Element ge = (Element) generator.get(0) ;
		
		String m_clsName = ge.attributeValue("class") ;
		
		if("native".equalsIgnoreCase(m_clsName)){ //native的generator由dialect来定。
			m_clsName = dbGroup.getDialect().getNativeIDGenerator() ;
		}
		
		String realClassName = (String) IdentifierGeneratorFactory.getGeneratorClass(m_clsName) ;
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
		
		//register callback for GuzzContext's full starting.
		if(ig instanceof GuzzContextAware){
			gf.registerContextStartedAware((GuzzContextAware) ig) ;
		}
		
		st.setIdentifierGenerator(ig) ;
		
		return map ;
	}

}
