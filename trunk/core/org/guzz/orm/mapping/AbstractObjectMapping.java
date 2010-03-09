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

import java.util.HashMap;
import java.util.Map;

import org.guzz.dialect.Dialect;
import org.guzz.exception.DataTypeException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.type.SQLDataType;
import org.guzz.pojo.ColumnDataLoader;
import org.guzz.transaction.DBGroup;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractObjectMapping implements ObjectMapping {

	protected Map prop2ColsMapping = new HashMap() ;	
	
	protected Map col2PropsMapping = new HashMap() ;
	
	protected Dialect dialect ;	
	
	protected DBGroup dbGroup ;
	
	protected AbstractObjectMapping(DBGroup dbGroup){
		this.dbGroup = dbGroup ;
		this.dialect = dbGroup.getDialect() ;
	}
		
	protected abstract String getColDataType(String propName, String colName, String dataType) ;
		
	/**
	 * TODO: change to addPropertyMap(TableColumn column)
	 */
	public x$ORM addPropertyMap(String propName, String colName, String dataType, String nullValue, ColumnDataLoader columnDataLoader){
		String dataType2 = getColDataType(propName, colName, dataType) ;
		x$ORM o = null ;
		
		if(columnDataLoader == null){
			if(dataType2 == null){
				throw new DataTypeException("unknown datatype:[null] for column:[" + colName + "]") ;
			}
			
			if(dialect == null){
				throw new DataTypeException("no dialect found. datatype:[" + dataType2 + "] for column:[" + colName + "]") ;
			}
			
			SQLDataType type = dialect.getDataType(dataType2) ;
			type.setNullToValue(nullValue) ;
			
			o = new x$ORM(propName, colName, dataType2, type, null) ;
		}else{
			//如果设置了loader，忽略SQLDataType
			o = new x$ORM(propName, colName, dataType2, null, columnDataLoader) ;
		}
		
		prop2ColsMapping.put(propName, o) ;
		
		//数据库的column名称不区分大小写。检索时全部按照小写检索。
		col2PropsMapping.put(colName.toLowerCase(), o) ;
		
		return o ;
	}
	
	public SQLDataType getSQLDataTypeOfColumn(String colName){
		x$ORM orm = (x$ORM) col2PropsMapping.get(colName.toLowerCase()) ;
		
		if(orm == null){
			throw new DataTypeException("column[" + colName + "] has no mapping.") ;
		}
		
		return orm.sqlDataType ;
	}
	
	public SQLDataType getSQLDataTypeOfProperty(String propName){
		x$ORM orm = (x$ORM) this.prop2ColsMapping.get(propName) ;
		
		if(orm == null){
			throw new DataTypeException("propName[" + propName + "] has no mapping.") ;
		}
		
		return orm.sqlDataType ;
	}
	
	public x$ORM getORMByColumn(String colName){
		x$ORM orm = (x$ORM) col2PropsMapping.get(colName.toLowerCase()) ;
		
		if(orm == null){
			throw new DataTypeException("column[" + colName + "] has no mapping.") ;
		}
		
		return orm ;
	}
	
	public x$ORM getORMByProperty(String propName){
		x$ORM orm = (x$ORM) this.prop2ColsMapping.get(propName) ;
		
		if(orm == null){
			throw new DataTypeException("propName[" + propName + "] has no mapping.") ;
		}
		
		return orm ;
	}
	
	public String getColNameByPropName(String propName){
		x$ORM orm = (x$ORM) prop2ColsMapping.get(propName) ;
		if(orm == null){
			return null ;
		}
 		
		return orm.colName ;
	}
	
	public String getPropNameByColName(String colName){
		x$ORM orm = (x$ORM) col2PropsMapping.get(colName.toLowerCase()) ;
		if(orm == null){
			return null ;
		}
 		
		return orm.propName ;
	}
	
	protected x$ORM getORMByPropName(String propName){
		return  (x$ORM) prop2ColsMapping.get(propName) ;
	}

	public DBGroup getDbGroup() {
		return dbGroup;
	}

	public void setDbGroup(DBGroup dbGroup) {
		this.dbGroup = dbGroup;
	}
	
}
