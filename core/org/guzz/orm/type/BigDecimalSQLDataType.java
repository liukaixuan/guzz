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

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class BigDecimalSQLDataType implements SQLDataType {

	private BigDecimal nullValue ;
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		BigDecimal bd = rs.getBigDecimal(colName) ;
		
		return bd == null ? this.nullValue : bd ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		BigDecimal bd = rs.getBigDecimal(colIndex) ;
		
		return bd == null ? this.nullValue : bd ;
	}

	public void setNullToValue(String nullValue) {
		if(nullValue != null){
			this.nullValue = new BigDecimal(nullValue) ;
		}
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		pstm.setBigDecimal(parameterIndex, value == null ? this.nullValue : (BigDecimal) value) ;
	}

}
