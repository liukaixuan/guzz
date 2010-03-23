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
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class BooleanSQLDataType implements SQLDataType {
	
	private boolean nullValue ;
	
	public void setNullToValue(String nullValue){
		if(nullValue != null){
			this.nullValue = Boolean.getBoolean(nullValue) ;
		}
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		return new Boolean(rs.getBoolean(colName)) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		return new Boolean(rs.getBoolean(colIndex)) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			pstm.setBoolean(parameterIndex, this.nullValue) ;
			return ;
		}
		
		if(value instanceof String){
			value = Boolean.valueOf((String) value) ;
		}
		
		pstm.setBoolean(parameterIndex, ((Boolean) value).booleanValue()) ;
	}
	
	public Class getDataType(){
		return Boolean.class ;
	}

	public Object getFromString(String value) {
		char c = value.charAt(0) ;
		if(c == '1' || c =='y' || c == 'Y' || c == 't' || c == 'T') return Boolean.TRUE ;
	
		return Boolean.FALSE ;
	}

}
