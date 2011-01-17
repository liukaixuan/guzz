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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.connection.DBGroup;
import org.guzz.exception.DataTypeException;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.type.SQLDataType;
import org.guzz.util.javabean.BeanWrapper;

/**
 * 
 * 用于支持对象POJO与数据库之间的字段映射和字段值绑定。
 * 
 * 分为针对POJO对象的映射，基于ResultMap的映射2种。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ObjectMapping {
	
	/**根据属性名获许对应的数据库字段名。如果没有映射，返回null*/
	public String getColNameByPropNameForSQL(String propName) ;
	
//	public String getPropNameByColName(String colName) ;
	
	/**
	 * Map one row of the {@link ResultSet} to a instance of the resultClass.
	 * 
	 * @param rs The queried {@link ResultSet}.
	 * @param resultClass The resultClass to be mapped. Use the configured domain-class if this parameter is null.
	 */
	public Object rs2Object(ResultSet rs, Class resultClass) throws SQLException ;
	
	/** 返回此ObjectMapping唯一的标识，可以有多个标识。
	 * 
	 * 如域对象ghostName, 域对象完整类名称，也可以是xml中配置的ResultMap名称。
	 * */
	public String[] getUniqueName() ;
	
	/**如果不支持抛出异常@link {@link DataTypeException}*/
//	public SQLDataType getSQLDataTypeOfColumn(String colName) ;
	
	/** throw {@link DataTypeException} if no {@link SQLDataType} associated. */
	public SQLDataType getSQLDataTypeOfProperty(String propName) ;
	
	/** throw {@link DataTypeException} if col not found. */
//	public ColumnORM getORMByColumn(String colName) ;
	
	/** throw {@link DataTypeException} if property not found. */
	public ColumnORM getORMByProperty(String propName) ;
	
	public DBGroup getDbGroup() ;
	
	public BeanWrapper getBeanWrapper() ;
	
	public Table getTable() ;
		
}
