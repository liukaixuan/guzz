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
package org.guzz.orm.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import org.guzz.exception.DataTypeException;
import org.guzz.util.ClassUtil;

/**
 * 
 * enum type stored as varchar() in the database.
 * <p>
 * The way to define a string enum type is set type to "enum.string|your_qualified_enum_type_class_name"
 * </p>
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class EnumStringSQLDataType implements SQLDataType, ParameteredType {

	private Object nullValue ;
	
	private Class enumClassType ;
	
	public void setParameter(String param){
		Class cls = ClassUtil.getClass(param) ;
		
		if(!cls.isEnum()){
			throw new DataTypeException("class:[" + cls.getName() + "] is not a enum.") ;
		}
		
		this.enumClassType = cls ;
	}
	
	public void setNullToValue(String nullValue){
		if(nullValue != null){
			this.nullValue = getEnumFromString(nullValue) ;
		}
	}
	
	protected Object getEnumFromString(String name){
		return Enum.valueOf(this.enumClassType, name) ;
	}
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		String value = rs.getString(colName) ;
		
		return getEnumFromString(value) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		String value = rs.getString(colIndex) ;
		
		return getEnumFromString(value) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value)  throws SQLException {
		if(value == null){
			value = this.nullValue ;
		}
		if(value instanceof String){
			value = getFromString((String) value) ;
		}
		
		if(value == null){
			pstm.setString(parameterIndex, null) ;
		}else{
			pstm.setString(parameterIndex, ((Enum) value).name()) ;
		}
	}
	
	public Class getDataType(){
		return enumClassType ;
	}

	public Object getFromString(String value) {
		return getEnumFromString(value) ;
	}

}
