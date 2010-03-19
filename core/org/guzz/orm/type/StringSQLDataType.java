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
public class StringSQLDataType implements SQLDataType {
	
	private String nullValue = null ;
	
	public void setNullToValue(String nullValue){
		this.nullValue = nullValue ;
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		return rs.getString(colName) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		return rs.getString(colIndex) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			pstm.setString(parameterIndex, this.nullValue) ;
		}else{
			pstm.setString(parameterIndex, value.toString()) ;
		}
	}
	
	public Class getDataType(){
		return String.class ;
	}

}
