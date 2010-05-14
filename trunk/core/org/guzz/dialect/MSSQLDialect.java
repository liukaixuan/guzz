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

import org.guzz.orm.type.BytesSQLDataType;

/**
 * 
 * Microsoft SQL Server 2000 & 2005 dialect.
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class MSSQLDialect extends AbstractDialect {
	
	public MSSQLDialect(){
		super() ;
		
		this.registerUserDefinedTypes("image", BytesSQLDataType.class) ;
		this.registerUserDefinedTypes("varbinary", BytesSQLDataType.class) ;
	}

	static int getAfterSelectInsertPoint(String sql) {
		int selectIndex = sql.toLowerCase().indexOf( "select" );
		final int selectDistinctIndex = sql.toLowerCase().indexOf( "select distinct" );
		return selectIndex + ( selectDistinctIndex == selectIndex ? 15 : 6 );
	}

	public String getLimitedString(String querySelect, int offset, int limit) {
		if ( offset > 0 ) {
			throw new UnsupportedOperationException( "query result offset is not supported" ) ;
		}
		
		return new StringBuffer( querySelect.length() + 8 )
				.append( querySelect )
				.insert( getAfterSelectInsertPoint( querySelect ), " top " + limit )
				.toString();
	}
	
	public String getSelectInsertedAutoIdClause() {
		return "select @@IDENTITY" ;
	}

	public String getSelectSequenceClause(String sequenceName) {
		return null;
	}

	public String getSelectGUIDClause() {
		return "select newid()" ;
	}

	public boolean supportsSequence() {
		return false;
	}

	public String getNativeIDGenerator() {
		return "identity";
	}
	
	public String getForUpdateString(String sql) {
		throw new UnsupportedOperationException( "sql server row lock is not supported" );
	}
	
	public String getEscapedColunmName(String columnName) {
		return '[' + columnName + ']' ;
	}

}
