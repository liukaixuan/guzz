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

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Mysql5Dialect extends AbstractDialect {

	public String getLimitedString(String sql, int offset, int limit) {
		sql = sql.trim() ;
		String sql2 = sql.toLowerCase() ;
		
		boolean isForUpdate = false ;
		
		if( sql2.endsWith(" for update") ){
			sql = sql.substring(0, sql.length() - 11) ;
			isForUpdate = true ;
		}
		
		StringBuffer sb = new StringBuffer(sql.length() + 16) ;
		sb.append(sql) ;
		
		if(offset <= 0){
			if(limit >= Integer.MAX_VALUE){ //读取所有，不需要Limit.
				
			}else{
				sb.append(" limit ").append(limit) ;
			}
		}else{
			sb.append(" limit ").append((offset)).append(", ").append(limit) ;
		}
		
		if(isForUpdate ) {
			sb.append( " for update" ) ;
		}
		
		return sb.toString() ;
	}
	
	public String getSelectInsertedAutoIdClause() {
		return "select last_insert_id()" ;
	}

	public String getSelectSequenceClause(String sequenceName) {
		return null;
	}

	public String getSelectGUIDClause() {
		return "select uuid()" ;
	}

	public boolean supportsSequence() {
		return false;
	}

	public String getNativeIDGenerator() {
		return "identity";
	}

	public String getEscapedColunmName(String columnName) {
		return '`' + columnName + '`' ;
	}
	
	/**
	 * Tested in Mysql5 on Windows.
	 * 
	 * @return 1024
	 */
	public int getDefaultBatchSize(){
		return 1024 ;
	}
	
}
