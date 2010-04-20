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
 * A dialect for Oracle 9i databases.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class Oracle9iDialect extends Oracle8iDialect {

	public String getLimitedString(String sql, int offset, int limit) {
		sql = sql.trim() ;
		String sql2 = sql.toLowerCase() ;
		
		boolean isForUpdate = false ;
		boolean isForUpdateNoWait = false ;
		
		if( sql2.endsWith(" for update") ){
			sql = sql.substring(0, sql.length() - 11) ;
			isForUpdate = true ;
		}else if( sql2.endsWith(" for update nowait") ){
			sql = sql.substring( 0, sql.length() - 18) ;
			isForUpdateNoWait = true ;
		}

		StringBuffer sb = new StringBuffer(sql.length() + 128) ;
		
		if(offset > 0){
			sb.append("select * from ( select row_.*, rownum rownum_ from ( ") ;
		}
		else{
			sb.append("select * from ( ") ;
		}
		
		sb.append(sql) ;
		
		if(offset > 0){
			sb.append(" ) row_ where rownum <= ").append(limit + offset).append(") where rownum_ > ").append(offset) ;
		}
		else{
			sb.append(" ) where rownum <= ").append(limit) ;
		}

		if(isForUpdate ) {
			sb.append( " for update" ) ;
		}else if(isForUpdateNoWait){
			sb.append( " for update nowait" ) ;
		}

		return sb.toString() ;
	}

}
