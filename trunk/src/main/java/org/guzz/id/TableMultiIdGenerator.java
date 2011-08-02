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
package org.guzz.id;

import java.util.Properties;

import org.guzz.dialect.Dialect;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.util.Assert;
import org.guzz.util.StringUtil;

/**
 * <b>hilo.multi</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that returns a <tt>Long</tt>, constructed using
 * a hi/lo algorithm, and service for many tables. 
 * For each table, a cell distinguished by the primary value parameter pk_column_value is used for id generating.
 * 
 * <br>The hi value MUST be fetched in a separate transaction
 * to the <tt>Session</tt> transaction so the generator must be able to obtain
 * a new connection and commit it. Hence this implementation may not
 * be used when the user is supplying connections.
 * <br>
 * Mapping parameters supported: table, column, db_group, max_lo, pk_column_name, pk_column_value(required, and must be a positive integer)
 *
 * @see TableHiLoGenerator
 * @see SequenceHiLoGenerator
 */
public class TableMultiIdGenerator extends TableHiLoGenerator {
	
	/**
	 * The pkColumnName parameter. The default name is pk_id
	 */
	public static final String PK_COLUMN_NAME = "pk_column_name" ;
	
	/**
	 * The pkColumnValue parameter
	 */
	public static final String PK_COLUMN_VALUE = "pk_column_value" ;
	
	protected String pkColumnName ;
	
	protected int pkColumnValue ;

	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		this.pkColumnName = params.getProperty(PK_COLUMN_NAME) ;
		this.pkColumnValue = StringUtil.toInt(params.getProperty(PK_COLUMN_VALUE), 0) ;
		
		if(StringUtil.isEmpty(this.pkColumnName)){
			this.pkColumnName = "pk_id" ;
		}
		
		Assert.assertBigger(this.pkColumnValue, 0, "The parameter pk_column_value must be specified, and must be a positive integer value.") ;
				
		super.configure(dialect, mapping, params) ;
	}
	
	protected String getSqlForQuery(){
		return "select " + columnName + " from " + tableName + " where " +  pkColumnName + " = " + pkColumnValue ;
	}
	
	protected String getSqlForUpdate(){
		return "update " + 
				tableName + 
				" set " + 
				columnName + 
				" = ? where " +
				pkColumnName + 
				" = " + pkColumnValue
				+ " and " +
				columnName + 
				" = ?" ;
	}
	
}
