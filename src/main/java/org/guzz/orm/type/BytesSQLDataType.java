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

/**
 * 
 * Represents a byte array. For very long arrays, use BLOB. 
 * The whole binary array is kept in memory when using this data type. 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class BytesSQLDataType implements SQLDataType {
	
	private byte[] nullValue ;

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		byte[] value = rs.getBytes(colName) ;
		
		if(value == null && this.nullValue != null){
			int len = this.nullValue.length ;
			byte[] newBytes = new byte[len] ;
			System.arraycopy(this.nullValue, 0, newBytes, 0, len) ;
			
			return newBytes ;
		}
		
		return value ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		byte[] value = rs.getBytes(colIndex) ;
		
		if(value == null && this.nullValue != null){
			int len = this.nullValue.length ;
			byte[] newBytes = new byte[len] ;
			System.arraycopy(this.nullValue, 0, newBytes, 0, len) ;
			
			return newBytes ;
		}
		
		return value ;
	}

	public void setNullToValue(Object nullValue) {
		this.nullValue = (byte[]) nullValue ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value instanceof String){
			value = getFromString((String) value) ;
		}else if(value == null){
			value = this.nullValue ;
		}
		
		pstm.setBytes(parameterIndex, (byte[]) value) ;
	}
	
	public Class getDataType(){
		return byte[].class ;
	}

	public Object getFromString(String value) {
		if(value == null) return null ;
		
		return value.getBytes() ;
	}

}
