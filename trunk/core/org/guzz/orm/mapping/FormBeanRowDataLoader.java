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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.Guzz;
import org.guzz.orm.CustomTableView;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.type.SQLDataType;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;

/**
 * 
 * Map the ResultSet to any given class with set-xxx methods. The data type of each property is auto-detected
 * through the reflections of the given class.
 * 
 * <p>
 * If the class is a java.util.Map(for example:java.util.HashMap), the returned value will be put to the Map.
 * The map's key is the columnName, and the value is {@link ResultSet#getObject(columnName)} if no @param colsMapping could be found.
 * </p>
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public final class FormBeanRowDataLoader implements RowDataLoader {
	private final static Log log = LogFactory.getLog(FormBeanRowDataLoader.class) ;
	
	private final Class beanCls ;

	private final boolean isMap ;

	private final JavaBeanWrapper beanWrapper ;

	private final Map cachedDataTypes ;
	
	private final ObjectMapping colsMapping ;
	
	private final CustomTableView customTableView ;
	
	/**
	 * The column name in the database meta-data is case-insensitive, and the property name is case-sensitive.
	 * We map it with this. The key is low-cased property name, and the value is the original one.
	 */
	private final Map writableProps ;
	
	/**
	 * 
	 * Create a new instance of FormBeanRowDataLoader for class @param beanCls.
	 * <p>The beanCls's filed name should be the same as the column name in the {@link ResultSet}. 
	 * Or, it won't be possible for guzz to understand how to do the map.</p>
	 * 
	 * @param beanCls java object to store the queried {@link ResultSet}
	 */
	public static FormBeanRowDataLoader newInstanceForClass(Class beanCls){
		return new FormBeanRowDataLoader(null, null, beanCls) ;
	}
	
	/**
	 * 
	 * Create a new instance of FormBeanRowDataLoader for class @param beanCls.
	 * 
	 * @param colsMapping Specify how to map a {@link ResultSet} to the bean class.
	 * @param beanCls java object to store the queried {@link ResultSet}
	 */
	public static FormBeanRowDataLoader newInstanceForClass(ObjectMapping colsMapping, Class beanCls){
		return new FormBeanRowDataLoader(null, colsMapping, beanCls) ;
	}
	
	/**
	 * 
	 * Create a new instance of FormBeanRowDataLoader for class @param beanCls.
	 * 
	 * @param customTableView Specify how to map a {@link ResultSet} to the bean class.
	 * @param beanCls java object to store the queried {@link ResultSet}
	 */
	public static FormBeanRowDataLoader newInstanceForClass(CustomTableView customTableView, Class beanCls){
		return new FormBeanRowDataLoader(customTableView, null, beanCls) ;
	}
	
	protected FormBeanRowDataLoader(CustomTableView customTableView,ObjectMapping colsMapping, Class beanCls){
		this.beanCls = beanCls ;
		this.customTableView = customTableView ;
		this.colsMapping = colsMapping ;
		this.isMap = java.util.Map.class.isAssignableFrom(beanCls) ;
		
		if(!this.isMap){
			this.beanWrapper = BeanWrapper.createPOJOWrapper(beanCls) ;
			cachedDataTypes = new HashMap() ;
			this.writableProps = new HashMap() ;
			
			List beanProps = this.beanWrapper.getAllWritabeProps() ;
			for(int i = 0 ; i < beanProps.size() ; i++){
				String prop = (String) beanProps.get(i) ;
				writableProps.put(prop.toLowerCase(), prop) ;
			}
		}else{
			this.beanWrapper = null ;
			this.cachedDataTypes = null ;
			this.writableProps = null ;
		}
 	}

	public Object rs2Object(ObjectMapping mapping, ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData() ;
		int count = meta.getColumnCount() ;
		
		Object obj = BeanCreator.newBeanInstance(this.beanCls) ;
		
		if(this.customTableView != null){
			mapping = this.customTableView.getRuntimeObjectMapping(Guzz.getTableCondition()) ;
		}else if(this.colsMapping != null){
			mapping = this.colsMapping ;
		}
		
		if(isMap){
			for(int i = 1 ; i <= count ; i++){
				String colName = meta.getColumnLabel(i) ;
				TableColumn tc = mapping.getTable().getColumnByColNameInRS(colName) ;
				Object value = null ;
				
				if(tc != null){
					value = tc.getOrm().loadResult(rs, obj, i) ;
				}else{
					value = rs.getObject(i) ;
				}
				
				((Map) obj).put(tc == null ? colName : tc.getPropName(), value) ;
			}
		}else{
			for(int i = 1 ; i <= count ; i++){
				String colName = meta.getColumnLabel(i) ;
				TableColumn tc = mapping.getTable().getColumnByColNameInRS(colName) ;
				
				String propName = null ;
				Object value = null ;
				
				if(tc != null){
					propName = tc.getPropName() ;
					value = tc.getOrm().loadResult(rs, obj, i) ;
				}else{
					propName = (String) this.writableProps.get(colName.toLowerCase()) ;
					
					SQLDataType type = (SQLDataType) cachedDataTypes.get(propName) ;
					
					if(type != null){
						value = type.getSQLValue(rs, i) ;
					}else{
						String propType = this.beanWrapper.getPropertyTypeName(propName) ;
						type = mapping.getDbGroup().getDialect().getDataType(propType) ;
						value = type.getSQLValue(rs, i) ;
						cachedDataTypes.put(propName, type) ;
					}
				}
				
				if(propName == null){
					if(log.isWarnEnabled()){
						log.warn("rs column:[" + colName + "] cann't be mapped to java class:[" + this.beanCls.getName() + "]. The column is not writable.") ;
					}
					continue ;
				}
				
				this.beanWrapper.setValue(obj, propName, value) ;
			}
		}
		
		return obj ;
	}
	
	public Class getBeanCls() {
		return beanCls;
	}
	
	public JavaBeanWrapper getBeanWrapper() {
		return beanWrapper;
	}

}
