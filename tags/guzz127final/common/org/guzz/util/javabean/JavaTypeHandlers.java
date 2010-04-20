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
package org.guzz.util.javabean;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.DataTypeException;
import org.guzz.util.DateUtil;

/**
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class JavaTypeHandlers {
	private static transient final Log log = LogFactory.getLog(JavaTypeHandlers.class) ;
	
	public static final Map COMMON_DATA_TYPE_HANDLERS = new HashMap() ;
	
	static{
		try {
			COMMON_DATA_TYPE_HANDLERS.put("int", IntegerHandler.class.newInstance()) ;
			COMMON_DATA_TYPE_HANDLERS.put(Integer.class.getName(), IntegerHandler.class.newInstance()) ;
			
			COMMON_DATA_TYPE_HANDLERS.put("boolean", BooleanHandler.class.newInstance()) ;
			COMMON_DATA_TYPE_HANDLERS.put(Boolean.class.getName(), BooleanHandler.class.newInstance()) ;
			
			COMMON_DATA_TYPE_HANDLERS.put("long", LongHandler.class.newInstance()) ;
			COMMON_DATA_TYPE_HANDLERS.put(Long.class.getName(), LongHandler.class.newInstance()) ;
			
			COMMON_DATA_TYPE_HANDLERS.put("float", FloatHandler.class.newInstance()) ;
			COMMON_DATA_TYPE_HANDLERS.put(Float.class.getName(), FloatHandler.class.newInstance()) ;
			
			COMMON_DATA_TYPE_HANDLERS.put("double", DoubleHandler.class.newInstance()) ;
			COMMON_DATA_TYPE_HANDLERS.put(Double.class.getName(), DoubleHandler.class.newInstance()) ;
						
			COMMON_DATA_TYPE_HANDLERS.put("java.util.Date", DateHandler.class.newInstance()) ;
			
			COMMON_DATA_TYPE_HANDLERS.put(String.class.getName(), StringHandler.class.newInstance()) ;
			
		} catch (InstantiationException e) {
			log.error("init IDataTypeHandler failed.", e) ;
		} catch (IllegalAccessException e) {
			log.error("init IDataTypeHandler failed.", e) ;
		}
	}
	
	public static IDataTypeHandler getUnsupportedDataHandler(Class fieldType){
		return new UnsupportedDataHandler(fieldType) ;
	}
	
	/**
	 * 将字符串转换为指定的数据类型。
	 * @param value 要转换的字符串
	 * @param className 要转换成的类型，如int, float等。
	 * 
	 */
	public static Object convertValueToType(String value, String className){
		IDataTypeHandler mh = (IDataTypeHandler) COMMON_DATA_TYPE_HANDLERS.get(className) ;
		
		if(mh == null){
			throw new DataTypeException("unknown data type :" + className) ;
		}
		
		return mh.getValue(value) ;
	}

}

class IntegerHandler implements IDataTypeHandler{

	public Object getValue(String fieldValue) {
		return Integer.valueOf(fieldValue) ;
	}
	
}

class StringHandler implements IDataTypeHandler{

	public Object getValue(String fieldValue) {
		return fieldValue ;
	}
	
}

class LongHandler implements IDataTypeHandler{

	public Object getValue(String fieldValue) {
		return Long.valueOf(fieldValue) ;
	}
	
}

class FloatHandler implements IDataTypeHandler{

	public Object getValue(String fieldValue) {
		return Float.valueOf(fieldValue) ;
	}
	
}

class DoubleHandler implements IDataTypeHandler{

	public Object getValue(String fieldValue) {
		return Double.valueOf(fieldValue) ;
	}
	
}

class DateHandler implements IDataTypeHandler{

	public Object getValue(String fieldValue) {
		//TODO: 根据fieldValue中传入的时间格式进行解析
		return DateUtil.stringToDate(fieldValue, "yyyy-MM-dd HH:mm:ss") ;
	}
	
}

class BooleanHandler implements IDataTypeHandler{

	public Object getValue(String fieldValue) {
		char c = fieldValue.charAt(0) ;
		if(c == '1' || c =='y' || c == 'Y' || c == 't' || c == 'T') return Boolean.TRUE ;
	
		return Boolean.FALSE ;
	}
	
}

class UnsupportedDataHandler implements IDataTypeHandler{
	
	private Class fieldType ;
	
	public UnsupportedDataHandler(Class fieldType){
		this.fieldType = fieldType ;
	}

	public Object getValue(String fieldValue) {
		throw new DataTypeException("unknown data type :" + fieldType) ;
	}
	
}

