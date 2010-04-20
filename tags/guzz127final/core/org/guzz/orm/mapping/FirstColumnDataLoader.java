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

import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.exception.DataTypeException;
import org.guzz.orm.ObjectMapping;
import org.guzz.orm.ColumnORM;
import org.guzz.orm.type.SQLDataType;

/**
 * 
 * 读取查询请求的第1列。使得ORM的结果为List<ColumnType>，如：List<Integer>等。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class FirstColumnDataLoader implements RowDataLoader {
	
	private String propName ;
	
	private SQLDataType dataType ;
	
	private String typeName ;
	
	public static FirstColumnDataLoader newInstanceForProperty(String propName){
		FirstColumnDataLoader l = new FirstColumnDataLoader() ;
		l.propName = propName ;
		return l ;
	}
	
	public static FirstColumnDataLoader newInstanceForReturnType(String dataType){
		FirstColumnDataLoader l = new FirstColumnDataLoader() ;
		l.typeName = dataType ;
		return l ;
	}
	
	public static FirstColumnDataLoader newInstanceForReturnType(SQLDataType dataType){
		FirstColumnDataLoader l = new FirstColumnDataLoader() ;
		l.dataType = dataType ;
		return l ;
	}
	
	protected FirstColumnDataLoader(){}

	public Object rs2Object(ObjectMapping mapping, ResultSet rs) throws SQLException {
		if(this.propName != null){
			ColumnORM orm = mapping.getORMByProperty(propName) ;
			
			return orm.loadResult(rs, null, 1) ;
		}
		
		if(this.dataType == null){
			this.dataType = mapping.getDbGroup().getDialect().getDataType(typeName) ;
			
			if(this.dataType == null){
				throw new DataTypeException("unknown dataType:" + typeName) ;
			}
		}
		
		return this.dataType.getSQLValue(rs, 1) ;
	}

}
