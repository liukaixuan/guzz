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
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.guzz.exception.DataTypeException;
import org.guzz.util.DateUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * {@link java.util.Calendar} data type handler.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class CalendarSQLDataType implements SQLDataType, ParameteredType {
	private static final String FMT = "yyyy-MM-dd" ;
	
	private Calendar nullDate = null ;
	
	private String dateFormat = FMT ;

	public void setParameter(String param) {
		if(StringUtil.notEmpty(param)){
			dateFormat = param ;
		}
	}
	
	public void setNullToValue(Object nullValue) {
		this.nullDate = (Calendar) nullValue ;
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		Date d = rs.getDate(colName) ;
		
		if(d == null){
			return this.nullDate ;
		}		

		Calendar c = Calendar.getInstance() ;
		c.setTime(d) ;
		
		return c ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		Date d = rs.getDate(colIndex) ;
		
		if(d == null){
			return this.nullDate ;
		}
		
		Calendar c = Calendar.getInstance() ;
		c.setTime(d) ;
		
		return c ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value instanceof String){
			value = getFromString((String) value) ;
		}
		
		if(value == null){
			if(this.nullDate == null){
				pstm.setNull(parameterIndex, java.sql.Types.TIMESTAMP) ;
			}else{
				pstm.setTimestamp(parameterIndex, new Timestamp(this.nullDate.getTimeInMillis())) ;
			}
			
			return ;
		}
		
		if(value instanceof java.util.Date){
			pstm.setTimestamp(parameterIndex, new Timestamp(((Date) value).getTime())) ;
		}else if(value instanceof java.sql.Date){
			pstm.setTimestamp(parameterIndex, new Timestamp(((java.sql.Date) value).getTime())) ;
		}else if(value instanceof java.util.Calendar){
			pstm.setTimestamp(parameterIndex, new Timestamp(((Calendar) value).getTimeInMillis())) ;
		}else{
			throw new DataTypeException("unknown Calendar type:" + value.getClass()) ;
		}
	}
	
	public Class getDataType(){
		return java.util.Calendar.class ;
	}

	public Object getFromString(String value) {
		if(value == null) return null ;
		
		Date d = DateUtil.stringToDate(value, dateFormat) ;
		if(d == null){
			throw new DataTypeException("unknown calendar date:" + value + ", date format shoule be:" + dateFormat) ;
		}
		
		Calendar c = Calendar.getInstance() ;
		c.setTime(d) ;
		
		return c ;
	}

}
