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

import org.guzz.orm.type.SQLDataType;
import org.guzz.pojo.ColumnDataLoader;
import org.guzz.transaction.DBGroup;

/**
 * 
 * 用于支持对象POJO与数据库之间的字段映射和字段值绑定。
 * 
 * 分为针对POJO对象的映射，基于ResultMap的映射2种。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface ObjectMapping {
	
	public static class x$ORM{
		public final String propName ;
		public final String colName ;
		public final String dataTypeName ;
		public final SQLDataType sqlDataType ;
		public final ColumnDataLoader columnDataLoader ;
		
		public x$ORM(String p, String c, String d, SQLDataType type, ColumnDataLoader columnDataLoader){
			this.propName = p ;
			this.colName = c ;
			this.dataTypeName =d ;
			this.sqlDataType = type ;
			this.columnDataLoader = columnDataLoader ;
		}
		
		public Object loadResult(ResultSet rs, Object objectFetching, int index) throws SQLException{
			if(columnDataLoader != null){
				return columnDataLoader.loadData(rs, objectFetching, index) ;
			}else{
				return sqlDataType.getSQLValue(rs, index) ;
			}
		}
		
	}
	
	/**根据属性名获许对应的数据库字段名。如果没有映射，返回null*/
	public String getColNameByPropName(String propName) ;
	
	public String getPropNameByColName(String colName) ;
	
	public Object rs2Object(ResultSet rs) throws SQLException ;
	
	/** 返回此ObjectMapping唯一的标识，可以有多个标识。
	 * 
	 * 如域对象ghostName, 域对象完整类名称，也可以是xml中配置的ResultMap名称。
	 * */
	public String[] getUniqueName() ;
	
	/**如果不支持抛出异常@link {@link DataTypeException}*/
	public SQLDataType getSQLDataTypeOfColumn(String colName) ;
	
	/** throw {@link DataTypeException} if no {@link SQLDataType} associated. */
	public SQLDataType getSQLDataTypeOfProperty(String propName) ;
	
	/** throw {@link DataTypeException} if col not found. */
	public x$ORM getORMByColumn(String colName) ;
	
	/** throw {@link DataTypeException} if property not found. */
	public x$ORM getORMByProperty(String propName) ;
	
	public DBGroup getDbGroup() ;
	
		
}
