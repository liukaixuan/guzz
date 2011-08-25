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
package org.guzz.dialect;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.DataTypeException;
import org.guzz.orm.type.BigDecimalSQLDataType;
import org.guzz.orm.type.BigIntObjectSQLDataType;
import org.guzz.orm.type.BigIntSQLDataType;
import org.guzz.orm.type.BlobSQLDataType;
import org.guzz.orm.type.BooleanObjectSQLDataType;
import org.guzz.orm.type.BooleanSQLDataType;
import org.guzz.orm.type.ByteSQLDataType;
import org.guzz.orm.type.BytesSQLDataType;
import org.guzz.orm.type.CalendarSQLDataType;
import org.guzz.orm.type.ClobSQLDataType;
import org.guzz.orm.type.DateSQLDataType;
import org.guzz.orm.type.DateTimeSQLDataType;
import org.guzz.orm.type.DialectAware;
import org.guzz.orm.type.DoubleObjectSQLDataType;
import org.guzz.orm.type.DoubleSQLDataType;
import org.guzz.orm.type.EnumOrdinalSQLDataType;
import org.guzz.orm.type.EnumStringSQLDataType;
import org.guzz.orm.type.FloatObjectSQLDataType;
import org.guzz.orm.type.FloatSQLDataType;
import org.guzz.orm.type.IntegerObjectSQLDataType;
import org.guzz.orm.type.IntegerSQLDataType;
import org.guzz.orm.type.JavaUtilDateSQLDataType;
import org.guzz.orm.type.ParameteredType;
import org.guzz.orm.type.SQLDataType;
import org.guzz.orm.type.ShortObjectSQLDataType;
import org.guzz.orm.type.ShortSQLDataType;
import org.guzz.orm.type.StringSQLDataType;
import org.guzz.orm.type.TimeSQLDataType;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractDialect implements Dialect {
	protected transient final Log log = LogFactory.getLog(getClass()) ;	
	
	protected final Map sqlTypes = new HashMap() ;
	
	public AbstractDialect(){
		regSystemTypes() ;
	}
	
	protected void regSystemTypes(){
		sqlTypes.put("int", IntegerSQLDataType.class) ;
		sqlTypes.put("Integer", IntegerObjectSQLDataType.class) ;
		sqlTypes.put(Integer.class.getName(), IntegerObjectSQLDataType.class) ;
		
		sqlTypes.put("string", StringSQLDataType.class) ;
		sqlTypes.put("varchar", StringSQLDataType.class) ;
		sqlTypes.put("nvarchar", StringSQLDataType.class) ;
		sqlTypes.put("char", StringSQLDataType.class) ;
		sqlTypes.put("nchar", StringSQLDataType.class) ;
		sqlTypes.put("text", StringSQLDataType.class) ;
		sqlTypes.put("tinytext", StringSQLDataType.class) ;
		sqlTypes.put(String.class.getName(), StringSQLDataType.class) ;
		
		//java.sql.Timestamp
		sqlTypes.put("datetime", DateTimeSQLDataType.class) ;
		sqlTypes.put("timestamp", DateTimeSQLDataType.class) ;
		sqlTypes.put(java.util.Date.class.getName(), JavaUtilDateSQLDataType.class) ;
		sqlTypes.put(java.sql.Timestamp.class.getName(), DateTimeSQLDataType.class) ;

		//java.sql.Date
		sqlTypes.put("date", DateSQLDataType.class) ;
		sqlTypes.put(java.sql.Date.class.getName(), DateSQLDataType.class) ;
		
		//java.util.Calendar
		sqlTypes.put(java.util.Calendar.class.getName(), CalendarSQLDataType.class) ;
		
		//java.sql.Time
		sqlTypes.put("time", TimeSQLDataType.class) ;
		sqlTypes.put(java.sql.Time.class.getName(), TimeSQLDataType.class) ;
		
		//boolean
		sqlTypes.put("bool", BooleanSQLDataType.class) ;
		sqlTypes.put("boolean", BooleanSQLDataType.class) ;
		sqlTypes.put(Boolean.class.getName(), BooleanObjectSQLDataType.class) ;		

		sqlTypes.put("bigint", BigIntSQLDataType.class) ;
		sqlTypes.put("long", BigIntSQLDataType.class) ;
		sqlTypes.put(Long.class.getName(), BigIntObjectSQLDataType.class) ;
		
		sqlTypes.put("double", DoubleSQLDataType.class) ;
		sqlTypes.put(Double.class.getName(), DoubleObjectSQLDataType.class) ;
		
		sqlTypes.put("money", BigDecimalSQLDataType.class) ;	
		sqlTypes.put("decimal", BigDecimalSQLDataType.class) ;
		sqlTypes.put(BigDecimal.class.getName(), BigDecimalSQLDataType.class) ;		

		sqlTypes.put("float", FloatSQLDataType.class) ;
		sqlTypes.put(Float.class.getName(), FloatObjectSQLDataType.class) ;

		sqlTypes.put("short", ShortSQLDataType.class) ;
		sqlTypes.put("smallint", ShortSQLDataType.class) ;
		sqlTypes.put("tinyint", ShortSQLDataType.class) ;
		sqlTypes.put(Short.class.getName(), ShortObjectSQLDataType.class) ;
		
		//bit
		sqlTypes.put("byte", ByteSQLDataType.class) ;
		sqlTypes.put("bit", ByteSQLDataType.class) ;
		sqlTypes.put(java.lang.Byte.class.getName(), ByteSQLDataType.class) ;
		
		//byte[]
		sqlTypes.put("bytes", BytesSQLDataType.class) ;
		sqlTypes.put("[B", BytesSQLDataType.class) ;//byte[]
		sqlTypes.put("[Ljava.lang.Byte;", BytesSQLDataType.class) ;//Byte[]
		sqlTypes.put("binary", BytesSQLDataType.class) ;
		sqlTypes.put("varbinary", BytesSQLDataType.class) ;
		
		//clob
		sqlTypes.put("clob", ClobSQLDataType.class) ;
		sqlTypes.put(java.sql.Clob.class.getName(), ClobSQLDataType.class) ;
		sqlTypes.put(org.guzz.pojo.lob.TranClob.class.getName(), ClobSQLDataType.class) ;
		
		//blob
		sqlTypes.put("blob", BlobSQLDataType.class) ;
		sqlTypes.put(java.sql.Blob.class.getName(), BlobSQLDataType.class) ;
		sqlTypes.put(org.guzz.pojo.lob.TranBlob.class.getName(), BlobSQLDataType.class) ;
		
		//enum
		sqlTypes.put("enum.ordinal", EnumOrdinalSQLDataType.class) ;
		sqlTypes.put("enum.string", EnumStringSQLDataType.class) ;
		
	}
	
	public void registerUserDefinedTypes(String typeName, Class dataType){
		sqlTypes.put(typeName, dataType) ;
	}

	/**
	 * @param colType 字段类型，如varchar, bigint, int, org.guzz.xxx.POJO
	 * @return 用于处理@param数据类型的处理类，如果不支持抛出异常@link DataTypeException。
	 */
	public SQLDataType getDataType(String colType){
		String param = null ;
		int pos = colType.indexOf('|') ;
		
		if(pos != -1){
			param = colType.substring(pos + 1) ;
			colType = colType.substring(0, pos) ;
		}
		
		if(colType.indexOf('(') > 0){ //handle varchar(255), number(10, 4)...
			colType = colType.substring(0, colType.indexOf('(')) ;
		}
		
		Class type = (Class) sqlTypes.get(colType) ;
		
		if(type == null){
			Iterator i = this.sqlTypes.entrySet().iterator() ;
			while(i.hasNext()){
				Map.Entry e = (Entry) i.next() ;
				if(colType.equalsIgnoreCase((String) e.getKey())){
					type = (Class) e.getValue() ;
				}
			}
		}
		
		if(type != null){
			SQLDataType typeInstance ;
			try {
				typeInstance = (SQLDataType) type.newInstance() ;
			} catch (InstantiationException e) {
				throw new DataTypeException("unable to instance type class[" + type.getName() + "] for type:[" + colType + "].") ;
			} catch (IllegalAccessException e) {
				throw new DataTypeException("unable to instance type class[" + type.getName() + "] for type:[" + colType + "].") ;
			}
			
			if(typeInstance instanceof DialectAware){
				((DialectAware) typeInstance).setDialect(this) ;
			}
			
			if(typeInstance instanceof ParameteredType){
				((ParameteredType) typeInstance).setParameter(param) ;
			}else if(param != null){
				log.warn("data type class[" + type.getName() + "] for type:[" + colType + "] doesn't support parameterization. parameter:[" + param + "] is ignored.") ;
			}
			
			return typeInstance ;
		}else{
			throw new DataTypeException("column type[" + colType + "] is not supported.") ;
		}
	}

	public String getForUpdateNoWaitString(String sql) {
		return getForUpdateString(sql) ;
	}

	public String getForUpdateString(String sql) {
		return sql + " for update" ;
	}
	
	public boolean useStreamToInsertLob(){
		return true ;
	}
	
}
