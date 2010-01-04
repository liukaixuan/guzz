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
import java.util.Date;

import org.guzz.exception.DataTypeException;
import org.guzz.util.DateUtil;

/**
 * 
 * datetime/timestamp
 * 
 * 完成数据库的timestamp字段类型数据和@link java.util.Date 类型的对象的转换。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class DateTimeSQLDataType implements SQLDataType {
	
	private Timestamp nullDate = null ;
	
	private boolean saveAsNow = false ;
	
	public void setNullToValue(String nullValue){
		if(nullValue != null){
			if("now()".equalsIgnoreCase(nullValue)){
				this.saveAsNow = true ;
			}else{
				Date d = DateUtil.stringToDate(nullValue, "yyyy-MM-dd HH:mm:ss") ;
				if(d != null){
					this.nullDate = new Timestamp(d.getTime()) ;
				}
			}
		}
	}

	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		Timestamp ts =  rs.getTimestamp(colName) ;
		
		if(ts == null){
			return this.nullDate ;
		}
		
		return new Date(ts.getTime()) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		Timestamp ts =  rs.getTimestamp(colIndex) ;
		if(ts == null){
			return this.nullDate ;
		}
		
		return new Date(ts.getTime()) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException {
		if(value == null){
			if(this.saveAsNow){
				pstm.setTimestamp(parameterIndex, new Timestamp(new Date().getTime())) ;
			}else{
				pstm.setTimestamp(parameterIndex, this.nullDate) ;
			}
			
			return ;
		}
		
		if(value instanceof java.sql.Timestamp){
			pstm.setTimestamp(parameterIndex, (Timestamp) value) ;
		}else if(value instanceof java.util.Date){
			Timestamp ts = new Timestamp(((Date) value).getTime()) ;
			pstm.setTimestamp(parameterIndex, ts) ;
		}else if(value instanceof java.sql.Date){
			Timestamp ts = new Timestamp(((java.sql.Date) value).getTime()) ;
			pstm.setTimestamp(parameterIndex, ts) ;
		}else{
			throw new DataTypeException("unknown datetime type:" + value.getClass()) ;
		}
	}

}
