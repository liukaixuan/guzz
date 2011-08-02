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
 * Represents for a byte.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class ByteSQLDataType implements SQLDataType {
	
	private Byte nullValue ;

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		byte value = rs.getByte(colName) ;
		
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return new Byte(value) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		byte value = rs.getByte(colIndex) ;
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return new Byte(value) ;
	}

	public void setNullToValue(Object nullValue) {
		this.nullValue = (Byte) nullValue ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value instanceof String){
			value = getFromString((String) value) ;
		}else if(value == null){
			value = this.nullValue ;
		}
		
		pstm.setByte(parameterIndex, ((Byte) value).byteValue()) ;
	}
	
	public Class getDataType(){
		return Byte.class ;
	}

	public Object getFromString(String value) {
		return new Byte(value) ;
	}

}
