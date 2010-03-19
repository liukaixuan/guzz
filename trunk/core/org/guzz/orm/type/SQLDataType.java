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
 * 数据库中的字段类型，用于将数据库字段的值转换为java对象。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface SQLDataType {
	
	/**
	 * 设置当字段值为null时，字段使用的取值。如果不设置，采用默认值：所有java对象有null值的对象，按照null处理；没有null值的java类型，按照java类型的默认值（如数字为0）处理。
	 * @param nullValue string类型的值，每个具体的 {@link SQLDataType} 根据自身情况转换格式。
	 */
	public void setNullToValue(String nullValue) ;
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException ;
	
	/**
	 * @param rs
	 * @param colIndex the first column is 1, the second column is 2, ...
	 */
	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException ;
	
	/**
	 * set the java paramter to PreparedStatement params.
	 * 
	 * @param pstm
	 * @param parameterIndex the first column is 1, the second column is 2, ...
	 * @param value could be null.
	 */
	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value) throws SQLException ;
	
	/**
	 * get java data type.
	 */
	public Class getDataType() ;

}
