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
 * java.lang.Integer
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class IntegerObjectSQLDataType implements SQLDataType {

	private Integer nullValue ;
	
	public void setNullToValue(Object nullValue) {
		this.nullValue = (Integer) nullValue ;
	}
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		int value = rs.getInt(colName) ;
		
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return Integer.valueOf(value) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		int value = rs.getInt(colIndex) ;
		
		if(rs.wasNull()){
			return this.nullValue ;
		}
		
		return Integer.valueOf(value) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value)  throws SQLException {
		if(value instanceof String){
			value = getFromString((String) value) ;
		}
		
		if(value == null){
			value = this.nullValue ;
		}
		
		if(value == null){
			pstm.setNull(parameterIndex, java.sql.Types.INTEGER) ;
		}else{
			int v = ((Number) value).intValue() ;
			
			pstm.setInt(parameterIndex, v) ;
		}
	}
	
	public Class getDataType(){
		return Integer.class ;
	}

	public Object getFromString(String value) {
		//Type java.lang.Integer allows null value. 
		if(value == null) return null ;
		
		return Integer.valueOf(value) ;
	}

}
