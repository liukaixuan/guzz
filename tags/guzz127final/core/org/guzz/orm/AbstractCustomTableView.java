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
package org.guzz.orm;

import java.lang.reflect.Method;

import org.guzz.GuzzContext;
import org.guzz.GuzzContextImpl;
import org.guzz.exception.DaoException;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.transaction.DBGroup;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.util.javabean.JavaBeanWrapper;
import org.guzz.web.context.GuzzContextAware;

/**
 * 
 * Basic implementation of the CustomTableView.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractCustomTableView extends BeanWrapper implements CustomTableView, GuzzContextAware {

	private POJOBasedObjectMapping configuredMapping ;
	
	protected GuzzContext guzzContext ;
	
	protected JavaBeanWrapper basicBeanWrapper ;
	
	protected String configuredTableName ;

	public void setConfiguredTableName(String tableName) {
		this.configuredTableName = tableName ;
	}

	public String getConfiguredTableName() {
		return configuredTableName;
	}	
	
	public void setConfiguredObjectMapping(POJOBasedObjectMapping configuredMapping){
		this.configuredMapping = configuredMapping ;
		
		//默认初始化的BeanWrapper必然是JavaBeanWrapper
		this.basicBeanWrapper = (JavaBeanWrapper) configuredMapping.getBusiness().getBeanWrapper() ;
	}
	
	public POJOBasedObjectMapping createRuntimeObjectMapping(Object tableCondition){
		Business newBusiness = this.configuredMapping.getBusiness().newCopy() ;
		Table newTable = newBusiness.getTable().newCopy() ;
		
		newBusiness.setTable(newTable) ;
		newBusiness.setBeanWrapper(this) ;
		newBusiness.setConfiguredBeanWrapper(basicBeanWrapper) ;
		
		DBGroup dbGroup = this.configuredMapping.getDbGroup() ;
		
		POJOBasedObjectMapping newMap = new POJOBasedObjectMapping((GuzzContextImpl) this.guzzContext, dbGroup, newTable) ;
		newBusiness.setMapping(newMap) ;
		newMap.setBusiness(newBusiness) ;
		
		initCustomTableColumn(newMap, tableCondition) ;
		
		return newMap ;
	}
	
	/**
	 * Init the special mapping for the giving tableCondition. Called by {@link #createRuntimeObjectMapping(Object)}.
	 * <p>
	 * Only mappings besides the configured mapping in the xml are needed to add here.
	 * </p>
	 * 
	 * @param mapping
	 * @param tableCondition
	 */
	protected abstract void initCustomTableColumn(POJOBasedObjectMapping mapping, Object tableCondition) ;
	
	/**
	 * helper method to create a table column.
	 * 
	 * @param mapping
	 * @param propName property name in javabean
	 * @param colName column name in database
	 * @param dataType data type as in hbm.xml
	 * @param columnDataLoader as 'loader' in hbm.xml. If columnDataLoader is not null, parameter dataType will be ignored.
	 * 
	 * @return modify the returned TableColumn if necessary.
	 */
	protected TableColumn createTableColumn(POJOBasedObjectMapping mapping, String propName, String colName, String dataType, Class columnDataLoaderCls){
		TableColumn tc = new TableColumn(mapping.getTable()) ;
		tc.setPropName(propName) ;
		tc.setColName(colName) ;
		tc.setType(dataType) ;
		
		ColumnDataLoader dl = null ;
		if(columnDataLoaderCls != null){
			dl = (ColumnDataLoader) BeanCreator.newBeanInstance(columnDataLoaderCls) ;
			dl.configure(mapping, mapping.getTable(), propName, colName) ;
			
			//register the loader
			this.guzzContext.getDataLoaderManager().addDataLoader(dl) ;
		}
		
		mapping.initColumnMapping(tc, dl) ;
		
		return tc ;
	}
	
	/**
	 * 
	 * add the column to the table.
	 * 
	 * @param mapping
	 * @param column 
	 */
	protected void addTableColumn(POJOBasedObjectMapping mapping, TableColumn column){
		mapping.getTable().addColumn(column) ;
	}
	
	protected Class getCustomPropertyType(String propName){
		throw new DaoException("Cann't determinate the type of custom property:[" + propName + "] in CustomTableView:" + this.getClass()) ;
	}
	
	public Method getCustomPropertyReadMethod(String propName) {
		throw new DaoException("Cann't determinate the read method of custom property:[" + propName + "] in CustomTableView:" + this.getClass()) ;
	}

	/**
	 * Read the value of a special property(property doesn't own a read method in javabean form) in the domain object.
	 * 
	 * @param beanInstance the instance of the domain object
	 * @param propName property name
	 */
	public abstract Object getCustomPropertyValue(Object beanInstance, String propName) ;

	public Object getCustomPropertyValueUnderProxy(Object beanInstance, String propName) {
		return getCustomPropertyValue(beanInstance, propName) ;
	}

	public Method getCustomPropertyWriteMethod(String propName) {
		throw new DaoException("Cann't determinate the write method of custom property:[" + propName + "] in CustomTableView:" + this.getClass()) ;
	}

	/**
	 * Store value for a special property(property doesn't own a write method in javabean form) in the domain object.
	 * 
	 * @param beanInstance the instance of the domain object
	 * @param propName property name
	 * @param value property value
	 */
	public abstract void setCustomPropertyValue(Object beanInstance, String propName, Object value) ;
	
	public void setCustomPropertyValueUnderProxy(Object beanInstance, String propName, Object value) {
		setCustomPropertyValue(beanInstance, propName, value) ;
	}

	////////////////////////////////////////////////////bean wrapper///////////////////////
	public Class getPropertyType(String propName) {
		if(this.basicBeanWrapper.hasProperty(propName)){
			return this.basicBeanWrapper.getPropertyType(propName) ;
		}
		
		return getCustomPropertyType(propName) ;
	}

	public Method getReadMethod(String propName) {
		if(this.basicBeanWrapper.hasReadMethod(propName)){
			return this.basicBeanWrapper.getReadMethod(propName) ;
		}
		
		return getCustomPropertyReadMethod(propName) ;
	}

	public Object getValue(Object beanInstance, String propName) {
		if(this.basicBeanWrapper.hasReadMethod(propName)){
			return this.basicBeanWrapper.getValue(beanInstance, propName) ;
		}
		
		return getCustomPropertyValue(beanInstance, propName) ;
	}

	public Object getValueUnderProxy(Object beanInstance, String propName) {
		if(this.basicBeanWrapper.hasReadMethod(propName)){
			return this.basicBeanWrapper.getValueUnderProxy(beanInstance, propName) ;
		}
		
		return getCustomPropertyValueUnderProxy(beanInstance, propName) ;
	}

	public Method getWriteMethod(String propName) {
		if(this.basicBeanWrapper.hasWriteMethod(propName)){
			return this.basicBeanWrapper.getWriteMethod(propName) ;
		}
		
		return getCustomPropertyWriteMethod(propName) ;
	}

	public void setValue(Object beanInstance, String propName, Object value) {
		if(this.basicBeanWrapper.hasWriteMethod(propName)){
			this.basicBeanWrapper.setValue(beanInstance, propName, value) ;
		}else{
			setCustomPropertyValue(beanInstance, propName, value) ;
		}
	}

	public void setValueUnderProxy(Object beanInstance, String propName, Object value) {
		if(this.basicBeanWrapper.hasWriteMethod(propName)){
			this.basicBeanWrapper.setValueUnderProxy(beanInstance, propName, value) ;
		}else{
			setCustomPropertyValueUnderProxy(beanInstance, propName, value) ;
		}
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext ;
	}
	
	public void startup() {
		//更新GuzzContext中注册的Business的实际使用BeanWrapper
		configuredMapping.getBusiness().setBeanWrapper(this) ;
	}
	
	public void shutdown() throws Exception {
	}

}

