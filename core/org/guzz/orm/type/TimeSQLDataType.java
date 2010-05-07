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
import java.sql.Time;
import java.util.Date;

import org.guzz.exception.DataTypeException;
import org.guzz.util.DateUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TimeSQLDataType implements SQLDataType, ParameteredType {
	private static final String FMT = "HH:mm:ss" ;
	
	private Time nullTime = null ;
	
	private boolean saveAsNow = false ;
	
	private String dateFormat = FMT ;

	public void setParameter(String param) {
		if(StringUtil.notEmpty(param)){
			dateFormat = param ;
		}
	}
	
	public void setNullToValue(String nullValue){
		if(nullValue != null){
			if("now()".equalsIgnoreCase(nullValue)){
				this.saveAsNow = true ;
			}else{
				Date d = DateUtil.stringToDate(nullValue, dateFormat) ;
				if(d == null){
					nullTime = null ;
				}else{
					nullTime = new Time(d.getTime()) ;
				}
			}
		}
	}

	public Object getFromString(String value) {
		Date d = DateUtil.stringToDate(value, dateFormat) ;
		if(d == null){
			throw new DataTypeException("unknown time:" + value + ", time format should be:" + dateFormat) ;
		}
		
		return new Time(d.getTime()) ;
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		Time d = rs.getTime(colName) ;
		
		if(d == null){
			return this.nullTime ;
		}
		
		return d ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		Date d = rs.getTime(colIndex) ;
		
		if(d == null){
			return this.nullTime ;
		}
		
		return d ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			if(this.saveAsNow){
				pstm.setTime(parameterIndex, new java.sql.Time(new Date().getTime())) ;
			}else{
				if(this.nullTime == null){
					pstm.setTime(parameterIndex, null) ;
				}else{
					pstm.setTime(parameterIndex, this.nullTime) ;
				}
			}
			
			return ;
		}
		
		if(value instanceof java.util.Date){
			pstm.setTime(parameterIndex, new java.sql.Time(((Date) value).getTime())) ;
		}else if(value instanceof java.sql.Time){
			pstm.setTime(parameterIndex, (java.sql.Time) value) ;
		}else{
			throw new DataTypeException("unknown time type:" + value.getClass()) ;
		}
	}
	
	public Class getDataType(){
		return java.sql.Time.class ;
	}

}
