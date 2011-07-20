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

import org.guzz.orm.type.SQLDataType;

/**
 * 
 * 用于对外提供具体数据库相关的操作。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface Dialect {

	/**
	 * @param colType eg: varchar, bigint, int, org.guzz.xxx.POJO
	 * @return 用于处理@param数据类型的处理类，如果不支持抛出异常@link DataTypeException。
	 */
	public SQLDataType getDataType(String colType) ;
	
	public void registerUserDefinedTypes(String typeName, Class dataType) ;
	
	/**
	 * Transform the given @param columnName to allied name accepted by the database.
	 * 
	 * @param columnName The column name, could be a key word of the database.
	 */
	public String getEscapedColunmName(String columnName) ;
	
	/**
	 * 
	 * @param sql
	 * @param offset no offset = 0, skip one = 1, ...
	 * @param limit 
	 */
	public String getLimitedString(String sql, int offset, int limit) ;
	
	
	public String getForUpdateString(String sql) ;
	
	
	public String getForUpdateNoWaitString(String sql) ;
	
	
	public String getSelectInsertedAutoIdClause() ;
	
	
	public String getSelectGUIDClause() ;
	
	
	public boolean supportsSequence() ;
	
	
	public String getSelectSequenceClause(String sequenceName) ;
	
	/**当主键按照native配置时，实际使用哪种generator*/
	public String getNativeIDGenerator() ;
	
	/**
	 * Should LOBs (both BLOB and CLOB) be bound using stream operations (i.e.
	 * {@link java.sql.PreparedStatement#setBinaryStream}).
	 *
	 * @return True if BLOBs and CLOBs should be bound using stream operations.
	 */
	public boolean useStreamToInsertLob() ;
	
	/**
	 * Recommended batch size for this database.
	 */
	public int getDefaultBatchSize() ;
	
	
}
