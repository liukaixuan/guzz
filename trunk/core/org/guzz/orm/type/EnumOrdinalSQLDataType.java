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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.guzz.exception.DataTypeException;
import org.guzz.util.ClassUtil;

/**
 * 
 * enum type stored as integer in the database.
 * <p>
 * The way to define a ordinal enum type is set type to "enum.ordinal|your_qualified_enum_type_class_name"
 * </p>
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class EnumOrdinalSQLDataType implements SQLDataType, ParameteredType {

	private Object nullValue ;
	
	private Class enumClassType ;
	
	private Method valuesMethod ;
	
	public void setParameter(String param){
		Class cls = ClassUtil.getClass(param) ;
		
		if(!cls.isEnum()){
			throw new DataTypeException("class:[" + cls.getName() + "] is not a enum.") ;
		}
		
		this.enumClassType = cls ;
		
		try {
			this.valuesMethod = cls.getMethod("values", (Class[]) null) ;
		} catch (SecurityException e) {
			throw new DataTypeException("cann't instance enum type:[" + cls.getName() + "].", e) ;
		} catch (NoSuchMethodException e) {
			throw new DataTypeException("cann't instance enum type:[" + cls.getName() + "].", e) ;
		}
	}
	
	public void setNullToValue(Object nullValue) {
		this.nullValue = nullValue ;
	}
	
	protected Object getEnumFromOrdinal(int ordinal){
		Object[] enums;
		
		try {
			enums = (Object[]) valuesMethod.invoke((Object) null, (Object[]) null);
		} catch (IllegalArgumentException e) {
			throw new DataTypeException("cann't find enum value for ordinal:[" + ordinal + "] in enum class:" + enumClassType.getName() + "].", e) ;
		} catch (IllegalAccessException e) {
			throw new DataTypeException("cann't find enum value for ordinal:[" + ordinal + "] in enum class:" + enumClassType.getName() + "].", e) ;
		} catch (InvocationTargetException e) {
			throw new DataTypeException("cann't find enum value for ordinal:[" + ordinal + "] in enum class:" + enumClassType.getName() + "].", e) ;
		}
		
		if(ordinal >= enums.length){
			throw new DataTypeException("ordinal value:[" + ordinal + "] too bigger for enum class:" + enumClassType.getName() + "]. Max length is:" + enums.length) ;
		}
		
		return enums[ordinal] ;
	}
	
	public Object getSQLValue(ResultSet rs, String colName) throws SQLException {
		int value = rs.getInt(colName) ;
		
		if(rs.wasNull()){
			return nullValue ;
		}
		
		return getEnumFromOrdinal(value) ;
	}

	public Object getSQLValue(ResultSet rs, int colIndex) throws SQLException {
		int value = rs.getInt(colIndex) ;
		
		if(rs.wasNull()){
			return nullValue ;
		}
		
		return getEnumFromOrdinal(value) ;
	}

	public void setSQLValue(PreparedStatement pstm, int parameterIndex, Object value)  throws SQLException {
		if(value == null){
			value = this.nullValue ;
		}
		if(value instanceof String){
			value = getFromString((String) value) ;
		}
				
		if(value == null){
			pstm.setNull(parameterIndex, Types.INTEGER) ;
		}else{
			pstm.setInt(parameterIndex, ((Enum) value).ordinal()) ;
		}
	}
	
	public Class getDataType(){
		return enumClassType ;
	}

	public Object getFromString(String value) {
		return getEnumFromOrdinal(Integer.parseInt(value)) ;
	}

}
