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
package org.guzz.orm.mapping;

import org.guzz.GuzzContext;
import org.guzz.connection.DBGroup;
import org.guzz.exception.GuzzException;
import org.guzz.orm.ColumnDataLoader;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.ShadowTableView;
import org.guzz.orm.rdms.SimpleTable;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.util.Assert;
import org.guzz.util.ClassUtil;
import org.guzz.util.StringUtil;
import org.guzz.util.javabean.BeanCreator;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public abstract class ObjectMappingUtil {
	
//	/**
//	 * 
//	 * Helper method to create {@link ObjectMapping} for java bean mapping.
//	 * 
//	 * @param guzzContext guzzContext
//	 * @param beanClass the bean class. The beanClass can be a subclass of {@link Map}(eg:java.util.HashMap), or a POJO style javabean.
//	 * @param dbGroup the database group name.
//	 */
//	public static ObjectMapping createFormBeanMapping1(GuzzContext guzzContext, Class beanClass, String dbGroup){
//		if(StringUtil.isEmpty(dbGroup)){
//			dbGroup = "default" ;
//		}
//		
//		DBGroup db = guzzContext.getDBGroup(dbGroup) ;
//		
//		return new FormBeanBasedObjectMapping(beanClass, db) ;
//	}
//	
//	/**
//	 * 
//	 * Helper method to create {@link ObjectMapping} for java bean mapping.
//	 * 
//	 * @param guzzContext guzzContext
//	 * @param beanClass the bean class. The beanClass can be a subclass of {@link Map}(eg:java.util.HashMap), or a POJO style javabean.
//	 * @param dbGroup the database group name.
//	 * @param colsMapping colsMapping
//	 */
//	public static ObjectMapping createFormBeanMapping1(GuzzContext guzzContext, Class beanClass, String dbGroup, ObjectMapping colsMapping){
//		if(StringUtil.isEmpty(dbGroup)){
//			dbGroup = "default" ;
//		}
//		
//		DBGroup db = guzzContext.getDBGroup(dbGroup) ;
//		
//		return new FormBeanBasedObjectMapping(beanClass, db, colsMapping) ;
//	}
//	
//	/**
//	 * 
//	 * Helper method to create {@link ObjectMapping} for java bean mapping.
//	 * 
//	 * @param guzzContext guzzContext
//	 * @param beanClass the bean class. The beanClass can be a subclass of {@link Map}(eg:java.util.HashMap), or a POJO style javabean.
//	 * @param dbGroup the database group name.
//	 * @param colsOrmName business name OR global orm name
//	 */
//	public static ObjectMapping createFormBeanMapping1(GuzzContext guzzContext, Class beanClass, String dbGroup, String colsOrmName){
//		if(StringUtil.isEmpty(dbGroup)){
//			dbGroup = "default" ;
//		}
//		
//		DBGroup db = guzzContext.getDBGroup(dbGroup) ;
//		CustomTableView view = guzzContext.getObjectMappingManager().getCustomTableView(colsOrmName) ;
//		
//		if(view == null){
//			return new FormBeanBasedObjectMapping(beanClass, db, guzzContext.getObjectMappingManager().getStaticObjectMapping(colsOrmName)) ;
//		}else{
//			return new FormBeanBasedObjectMapping(beanClass, db, view) ;
//		}
//	}
	
	/**
	 * 
	 * Helper method to create {@link ObjectMapping} for ibatis style's mapping.
	 * 
	 * @param guzzContext guzzContext
	 * @param id as "id" attribute in "&lt;orm ..&gt;" in guzz.xml
	 * @param beanCls as "class" attribute in "&lt;orm ..&gt;" in guzz.xml
	 * @param dbGroup as "dbgroup" attribute in "&lt;orm ..&gt;" in guzz.xml
	 * @param shadowView as "shadow" attribute in "&lt;orm ..&gt;" in guzz.xml
	 * @param tableName as "table" attribute in "&lt;orm ..&gt;" in guzz.xml
	 */
	public static ResultMapBasedObjectMapping createResultMapping(GuzzContext guzzContext, String id, Class beanCls, String dbGroup, String shadowView, String tableName){
		if(StringUtil.isEmpty(dbGroup)){
			dbGroup = "default" ;
		}
		
		DBGroup db = guzzContext.getDBGroup(dbGroup) ;
				
		if(StringUtil.isEmpty(id)){
			throw new GuzzException("id cann't be null.") ;
		}
		
		//orm的shadow table支持。
		SimpleTable st = new SimpleTable(db.getDialect()) ;
		if(StringUtil.notEmpty(shadowView)){
			ShadowTableView sv = (ShadowTableView) BeanCreator.newBeanInstance(shadowView) ;
				
			guzzContext.getShadowTableViewManager().addShadowView(sv) ;
			st.setShadowTableView(sv) ;
		}		
	
		st.setTableName(tableName) ;
		st.setBusinessName(id) ;		
		
		ResultMapBasedObjectMapping map = new ResultMapBasedObjectMapping(db, id, beanCls, st) ;
		
		return map ;
	}
	
	/**
	 * Helper method to create and bind a table column.
	 * 
	 * @param guzzContext guzzContext
	 * @param mapping {@link AbstractObjectMapping}
	 * @param propName property name in javabean. As "property" attribute in "&lt;result ..&gt;" in guzz.xml
	 * @param colName column name in database. As "column" attribute in "&lt;result ..&gt;" in guzz.xml
	 * @param dataType data type. As "type" attribute in "&lt;result ..&gt;" in guzz.xml
	 * @param columnDataLoader As "loader" attribute in "&lt;result ..&gt;" in guzz.xml. If columnDataLoader is not null, the dataType parameter will be ignored.
	 * 
	 * @return modify the returned TableColumn if necessary.
	 */
	public static TableColumn createTableColumn(GuzzContext guzzContext, AbstractObjectMapping mapping, String propName, String colName, String dataType, String columnDataLoader){
		Assert.assertNotEmpty(propName, "invalid property." ) ;
		
		if(StringUtil.isEmpty(colName)){
			colName = propName ;
		}
		
		Table st = mapping.getTable() ;
		
		TableColumn col = new TableColumn(st) ;
		col.setColName(colName) ;
		col.setPropName(propName) ;
		col.setType(dataType) ;
		col.setAllowInsert(true) ;
		col.setAllowUpdate(true) ;
		col.setLazy(false) ;
		
		ColumnDataLoader dl = null ;
		if(StringUtil.notEmpty(columnDataLoader)){
			dl = (ColumnDataLoader) BeanCreator.newBeanInstance(ClassUtil.getClass(columnDataLoader)) ;
			dl.configure(mapping, st, col) ;
			
			//register the loader
			guzzContext.getDataLoaderManager().addDataLoader(dl) ;
		}
		
		mapping.initColumnMapping(col, dl) ;
	
		return col ;
	}
	
	/**
	 * 
	 * Add the column to the table.
	 * 
	 * @param mapping
	 * @param column 
	 */
	public static void addTableColumn(ObjectMapping mapping, TableColumn column){
		mapping.getTable().addColumn(column) ;
	}

}
