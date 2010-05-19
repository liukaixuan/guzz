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

import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.guzz.exception.DataTypeException;

/**
 * 
 * Represents a byte array. For very long arrays, use BLOB. 
 * the whole binary array is kept in memory when using this data type. 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class BytesSQLDataType implements SQLDataType {

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		return rs.getBytes(colName) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		return rs.getBytes(colIndex) ;
	}

	public void setNullToValue(String nullValue) {
		if(nullValue != null){
			throw new DataTypeException("null value unsupported. nullValue is:" + nullValue) ;
		}
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value instanceof String){
			value = getFromString((String) value) ;
		}
		
		pstm.setBytes(parameterIndex, (byte[]) value) ;
	}
	
	public Class getDataType(){
		return Array.class ;
	}

	public Object getFromString(String value) {
		throw new DataTypeException("unsupported operation. value is:" + value) ;
	}

}
