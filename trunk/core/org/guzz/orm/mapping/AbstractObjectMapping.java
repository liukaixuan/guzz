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
package org.guzz.orm.mapping;

import org.guzz.connection.DBGroup;
import org.guzz.dialect.Dialect;
import org.guzz.exception.DataTypeException;
import org.guzz.orm.ColumnDataLoader;
import org.guzz.orm.ColumnORM;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.rdms.Table;
import org.guzz.orm.rdms.TableColumn;
import org.guzz.orm.type.SQLDataType;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractObjectMapping implements ObjectMapping {
	
	protected Dialect dialect ;	
	
	protected DBGroup dbGroup ;
	
	protected final Table table ;
	
	protected AbstractObjectMapping(DBGroup dbGroup, Table table){
		this.dbGroup = dbGroup ;
		this.dialect = dbGroup.getDialect() ;
		this.table = table ;
	}
		
	protected abstract String getColDataType(String propName, String colName, String dataType) ;
		
	public void initColumnMapping(TableColumn tc, ColumnDataLoader columnDataLoader){
		String colName = tc.getColNameForSQL() ;
		
		String dataType2 = getColDataType(tc.getPropName(), colName, tc.getType()) ;
		tc.setType(dataType2) ;
		
		ColumnORM o = null ;
		
		if(columnDataLoader == null){
			if(dataType2 == null){
				throw new DataTypeException("unknown datatype:[null] for column:[" + colName + "]") ;
			}
			
			if(dialect == null){
				throw new DataTypeException("no dialect found. datatype:[" + dataType2 + "] for column:[" + colName + "]") ;
			}
			
			SQLDataType type = dialect.getDataType(dataType2) ;
			if(StringUtil.notEmpty(tc.getNullValue())){
				Object value = type.getFromString(tc.getNullValue()) ;
				type.setNullToValue(value) ;
			}
			
			o = new ColumnORM(tc, type) ;
		}else{
			//如果设置了loader，忽略SQLDataType
			o = new ColumnORM(tc, columnDataLoader) ;
		}
		
		tc.setOrm(o) ;
	}
	
//	public SQLDataType getSQLDataTypeOfColumn(String colName){
//		return getORMByColumn(colName).sqlDataType ;
//	}
	
	public SQLDataType getSQLDataTypeOfProperty(String propName){
		return getORMByProperty(propName).sqlDataType ;
	}
	
//	public ColumnORM getORMByColumn(String colName){
//		TableColumn col = table.getColumnByColName(colName) ;
//		
//		if(col == null){
//			throw new DataTypeException("column[" + colName + "] has no mapping.") ;
//		}
//		
//		return col.getOrm() ;
//	}
	
	public ColumnORM getORMByProperty(String propName){
		TableColumn col = table.getColumnByPropName(propName) ;
		
		if(col == null){
			throw new DataTypeException("propName[" + propName + "] has no mapping.") ;
		}
		
		return col.getOrm() ;
	}
	
	public String getColNameByPropNameForSQL(String propName) {
		TableColumn col = table.getColumnByPropName(propName) ;
		
		if(col == null){
			return null ;
		}
 		
		return col.getColNameForSQL() ;
	}
	
//	public String getPropNameByColName(String colName){
//		TableColumn col = table.getColumnByColName(colName) ;
//		if(col == null){
//			return null ;
//		}
// 		
//		return col.getPropName() ;
//	}	
	
	public Table getTable(){
		return table ;
	}

	public DBGroup getDbGroup() {
		return dbGroup;
	}

	public void setDbGroup(DBGroup dbGroup) {
		this.dbGroup = dbGroup;
	}
	
}
