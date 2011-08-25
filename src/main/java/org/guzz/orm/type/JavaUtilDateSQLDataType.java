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

import org.guzz.exception.DataTypeException;
import org.guzz.util.DateUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * java.util.Date。某项情况下，必须返回这种类型时使用。 
 * 
 * 完成数据库的timestamp字段类型数据和@link java.util.Date 类型的对象的转换。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class JavaUtilDateSQLDataType implements SQLDataType, ParameteredType {
	private static final String FMT = "yyyy-MM-dd HH:mm:ss" ;
	
	private java.util.Date nullDate = null ;
	
	private String dateFormat = FMT ;

	public void setParameter(String param) {
		if(StringUtil.notEmpty(param)){
			dateFormat = param ;
		}
	}
	
	public void setNullToValue(Object nullValue) {
		this.nullDate = (java.util.Date) nullValue ;
	}

	public Object getFromString(String value) {
		if(value == null) return null ;
		
		java.util.Date d = DateUtil.stringToDate(value, dateFormat) ;
		if(d == null){
			throw new DataTypeException("unknown datetime:" + value + ", date format should be:" + dateFormat) ;
		}
		
		return d ;
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		java.sql.Timestamp ts =  rs.getTimestamp(colName) ;
		
		if(ts == null){
			return this.nullDate ;
		}
		
		return new java.util.Date(ts.getTime()) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		java.sql.Timestamp ts =  rs.getTimestamp(colIndex) ;
		if(ts == null){
			return this.nullDate ;
		}
		
		return new java.util.Date(ts.getTime()) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			if(this.nullDate == null){
				pstm.setNull(parameterIndex, java.sql.Types.TIMESTAMP) ;
			}else{
				pstm.setTimestamp(parameterIndex, new java.sql.Timestamp(nullDate.getTime())) ;
			}
			
			return ;
		}
		if(value instanceof String){
			value = getFromString((String) value) ;
		}
				
		if(value instanceof java.sql.Timestamp){
			pstm.setTimestamp(parameterIndex, (java.sql.Timestamp) value) ;
		}else if(value instanceof java.util.Date){
			java.sql.Timestamp ts = new java.sql.Timestamp(((java.util.Date) value).getTime()) ;
			pstm.setTimestamp(parameterIndex, ts) ;
		}else if(value instanceof java.sql.Date){
			java.sql.Timestamp ts = new java.sql.Timestamp(((java.sql.Date) value).getTime()) ;
			pstm.setTimestamp(parameterIndex, ts) ;
		}else{
			throw new DataTypeException("unknown datetime type:" + value.getClass()) ;
		}
	}
	
	public Class getDataType(){
		return java.util.Date.class ;
	}

}
