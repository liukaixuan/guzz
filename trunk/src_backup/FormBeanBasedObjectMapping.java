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
import java.sql.SQLException;

import org.guzz.orm.ColumnORM;
import org.guzz.orm.CustomTableView;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.type.SQLDataType;
import org.guzz.transaction.DBGroup;
import org.guzz.util.Assert;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * Map the query result to a given class. The data type of each property is auto-detected
 * through the reflection from the given class.
 * 
 * <p>
 * If the class is a java.util.Map(for example:java.util.HashMap), the returned value will be put to the Map.
 * The map's key is the columnName, while the value is rs.getObject(columnName).
 * </p>
 * 
 * <p>
 * In the configuration file, the given class must be a qualified class name in the attribute "result-class". eg:
 * &ltselect id="listCommentsByName" orm="user" result-class="java.util.HashMap"&gt OR &ltselect id="listCommentsByName" result-class="org.guzz.SomeBean"&gt
 * </p>
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public final class FormBeanBasedObjectMapping implements ObjectMapping {

	private final FormBeanRowDataLoader rowLoader ;
	
	private final DBGroup dbGroup ;
	
	private ObjectMapping colsMapping ;
		
	public FormBeanBasedObjectMapping(Class beanCls, DBGroup dbGroup){
		this.dbGroup = dbGroup ;
		Assert.assertResouceNotNull(dbGroup, "dbGroup cann't be null. beanCls:" + beanCls) ;
		
		rowLoader = FormBeanRowDataLoader.newInstanceForClass(beanCls) ;
	}

	public FormBeanBasedObjectMapping(Class beanCls, DBGroup dbGroup, ObjectMapping colsMapping){
		this.dbGroup = dbGroup ;
		this.colsMapping = colsMapping ;
		Assert.assertResouceNotNull(dbGroup, "dbGroup cann't be null. beanCls:" + beanCls) ;
		
		rowLoader = FormBeanRowDataLoader.newInstanceForClass(colsMapping, beanCls) ;
	}
	
	public FormBeanBasedObjectMapping(Class beanCls, DBGroup dbGroup, CustomTableView customTableView){
		this.dbGroup = dbGroup ;
		Assert.assertResouceNotNull(dbGroup, "dbGroup cann't be null. beanCls:" + beanCls) ;
		
		rowLoader = FormBeanRowDataLoader.newInstanceForClass(customTableView, beanCls) ;
	}

	public Object rs2Object(ResultSet rs) throws SQLException {
		return this.rowLoader.rs2Object(this, rs) ;
	}

	public BeanWrapper getBeanWrapper() {
		return this.rowLoader.getBeanWrapper() ;
	}

	public DBGroup getDbGroup() {
		return dbGroup ;
	}
	
	protected ObjectMapping getMapping(){
		if(colsMapping != null){
			return colsMapping ;
		}else{
			throw new UnsupportedOperationException("bean class has no mappings. class:" + this.rowLoader.getBeanCls().getName()) ;
		}
	}

	public String getColNameByPropNameForSQL(String propName) {
		return getMapping().getColNameByPropNameForSQL(propName) ;
	}

	public ColumnORM getORMByProperty(String propName) {
		return getMapping().getORMByProperty(propName) ;
	}

	public SQLDataType getSQLDataTypeOfProperty(String propName) {
		return getMapping().getSQLDataTypeOfProperty(propName) ;
	}

	public Table getTable() {
		return getMapping().getTable() ;
	}

	public String[] getUniqueName() {
		return new String[0] ;
	}

}
