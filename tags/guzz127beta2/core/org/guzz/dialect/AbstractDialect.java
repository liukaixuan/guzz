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

import org.guzz.exception.DataTypeException;
import org.guzz.orm.type.BigDecimalSQLDataType;
import org.guzz.orm.type.BigIntSQLDataType;
import org.guzz.orm.type.BinarySQLDataType;
import org.guzz.orm.type.BlobSQLDataType;
import org.guzz.orm.type.BooleanSQLDataType;
import org.guzz.orm.type.ClobSQLDataType;
import org.guzz.orm.type.DateSQLDataType;
import org.guzz.orm.type.DateTimeSQLDataType;
import org.guzz.orm.type.DoubleSQLDataType;
import org.guzz.orm.type.FloatSQLDataType;
import org.guzz.orm.type.IntegerSQLDataType;
import org.guzz.orm.type.SQLDataType;
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

	protected final Map sqlTypes = new HashMap() ;
	
	public AbstractDialect(){
		regSystemTypes() ;
	}
	
	protected void regSystemTypes(){
		sqlTypes.put("int", new IntegerSQLDataType()) ;
		sqlTypes.put(Integer.class.getName(), new IntegerSQLDataType()) ;
		
		sqlTypes.put("string", new StringSQLDataType()) ;
		sqlTypes.put("varchar", new StringSQLDataType()) ;
		sqlTypes.put("nvarchar", new StringSQLDataType()) ;
		sqlTypes.put("char", new StringSQLDataType()) ;
		sqlTypes.put("nchar", new StringSQLDataType()) ;
		sqlTypes.put("text", new StringSQLDataType()) ;
		sqlTypes.put("tinytext", new StringSQLDataType()) ;
		sqlTypes.put(String.class.getName(), new StringSQLDataType()) ;
		
		sqlTypes.put("datetime", new DateTimeSQLDataType()) ;
		sqlTypes.put("timestamp", new DateTimeSQLDataType()) ;
		sqlTypes.put(java.util.Date.class.getName(), new DateTimeSQLDataType()) ;
		sqlTypes.put(java.sql.Timestamp.class.getName(), new DateTimeSQLDataType()) ;

		sqlTypes.put("date", new DateSQLDataType()) ;
		sqlTypes.put("time", new TimeSQLDataType()) ;
		sqlTypes.put(java.sql.Time.class.getName(), new TimeSQLDataType()) ;
		
		
		sqlTypes.put("bool", new BooleanSQLDataType()) ;
		sqlTypes.put("boolean", new BooleanSQLDataType()) ;	
		sqlTypes.put(Boolean.class.getName(), new BooleanSQLDataType()) ;		

		sqlTypes.put("bigint", new BigIntSQLDataType()) ;
		sqlTypes.put(Long.class.getName(), new BigIntSQLDataType()) ;
		
		sqlTypes.put("double", new DoubleSQLDataType()) ;
		sqlTypes.put(Double.class.getName(), new DoubleSQLDataType()) ;
		
		sqlTypes.put("money", new BigDecimalSQLDataType()) ;	
		sqlTypes.put("decimal", new BigDecimalSQLDataType()) ;
		sqlTypes.put(BigDecimal.class.getName(), new BigDecimalSQLDataType()) ;		

		sqlTypes.put("float", new FloatSQLDataType()) ;
		sqlTypes.put(Float.class.getName(), new FloatSQLDataType()) ;

		sqlTypes.put("short", new ShortSQLDataType()) ;
		sqlTypes.put("smallint", new ShortSQLDataType()) ;
		sqlTypes.put("tinyint", new ShortSQLDataType()) ;
		sqlTypes.put(Short.class.getName(), new ShortSQLDataType()) ;
		
		sqlTypes.put("bytes", new BinarySQLDataType()) ;
		sqlTypes.put("binary", new ShortSQLDataType()) ;
		
		//clob
		sqlTypes.put("clob", new ClobSQLDataType()) ;
		sqlTypes.put(java.sql.Clob.class.getName(), new ClobSQLDataType()) ;
		sqlTypes.put(org.guzz.pojo.lob.TranClob.class.getName(), new ClobSQLDataType()) ;
		
		//blob
		sqlTypes.put("blob", new BlobSQLDataType()) ;
		sqlTypes.put(java.sql.Blob.class.getName(), new BlobSQLDataType()) ;
		sqlTypes.put(org.guzz.pojo.lob.TranBlob.class.getName(), new BlobSQLDataType()) ;
		
	}
	
	public void registerUserDefinedTypes(String typeName, SQLDataType dataType){
		sqlTypes.put(typeName, dataType) ;
	}

	/**
	 * @param 字段类型，如varchar, bigint, int, org.guzz.xxx.POJO
	 * @return 用于处理@param数据类型的处理类，如果不支持抛出异常@link DataTypeException。
	 */
	public SQLDataType getDataType(String colType){
		if(colType.indexOf('(') > 0){ //handle varchar(255), number(10, 4)...
			colType = colType.substring(0, colType.indexOf('(')) ;
		}
		
		SQLDataType type = (SQLDataType) sqlTypes.get(colType) ;
		
		if(type != null){
			return type ;
		}else{
			Iterator i = this.sqlTypes.entrySet().iterator() ;
			while(i.hasNext()){
				Map.Entry e = (Entry) i.next() ;
				if(colType.equalsIgnoreCase((String) e.getKey())){
					return (SQLDataType) e.getKey() ;
				}
			}
		}
		
		throw new DataTypeException("column type[" + colType + "] unsupported.") ;
	}

	public String getForUpdateNoWaitString(String sql) {
		return getForUpdateString(sql) ;
	}

	public String getForUpdateString(String sql) {
		return sql + " for update" ;
	}
	
}
