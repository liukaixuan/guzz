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
 * java.lang.Long
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BigIntObjectSQLDataType implements SQLDataType {

	private Long nullValue ;
	
	public void setNullToValue(Object nullValue) {
		this.nullValue = (Long) nullValue ;
	}
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		long value = rs.getLong(colName) ;
		
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return new Long(value) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		long value = rs.getLong(colIndex) ;
		
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return new Long(value) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value)  throws SQLException {
		if(value instanceof String){
			value = getFromString((String) value) ;
		}
		
		if(value == null){
			value = this.nullValue ;
		}
		
		if(value == null){
			pstm.setNull(parameterIndex, java.sql.Types.BIGINT) ;
		}else{
			long v = ((Number) value).longValue() ;
			
			pstm.setLong(parameterIndex, v) ;
		}
	}
	
	public Class getDataType(){
		return Long.class ;
	}

	public Object getFromString(String value) {
		//Object type allows null value. 
		if(value == null) return null ;
		
		return Long.valueOf(value) ;
	}


}
