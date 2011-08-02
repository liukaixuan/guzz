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

import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.GuzzContext;
import org.guzz.dialect.Dialect;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.transaction.TransactionManager;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;

/**
 * <b>hilo</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that returns a <tt>Long</tt>, constructed using
 * a hi/lo algorithm. The hi value MUST be fetched in a seperate transaction
 * to the <tt>Session</tt> transaction so the generator must be able to obtain
 * a new connection and commit it. Hence this implementation may not
 * be used  when the user is supplying connections. In this
 * case a <tt>SequenceHiLoGenerator</tt> would be a better choice (where
 * supported).<br>
 * <br>
 * Mapping parameters supported: table, column, db_group, max_lo
 *
 * @see SequenceHiLoGenerator
 */
public class TableHiLoGenerator extends TableGenerator {
	private static final Log log = LogFactory.getLog(TableHiLoGenerator.class);
	
	/**
	 * The max_lo parameter
	 */
	public static final String MAX_LO = "max_lo";
	
	private TransactionManager tm ;

	private long hi;
	private int lo;
	private int maxLo;
	private Class returnType ;

	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		super.configure(dialect, mapping, params) ;
		
		this.maxLo = StringUtil.toInt(params.getProperty(MAX_LO), Short.MAX_VALUE) ;
		lo = maxLo + 1; // so we "clock over" on the first invocation
		this.returnType = pkColumn.getSqlDataType().getDataType() ;
	}

	public Serializable preInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		Serializable value = generate(tableCondition) ;
		
		setPrimaryKey(domainObject, value) ;
		
		return (Serializable) value ;
	}
	
	public synchronized Number generate(Object tableCondition) {
		if (maxLo < 1) {
			//open a new transaction.
			WriteTranSession session = this.tm.openRWTran(true) ;
			
			try{
				//keep the behavior consistent even for boundary usages
				Integer n = super.nextValueInTable(session, tableCondition) ;
				
				if(n.intValue() == 0){
					n = super.nextValueInTable(session, tableCondition) ;
				}
				
				return IdentifierGeneratorFactory.createNumber(n.longValue(), this.returnType) ;
			}finally{
				session.close() ;
			}
		}
		
		if (lo > maxLo){
			//open a new transaction.
			WriteTranSession session = this.tm.openRWTran(true) ;
			
			try{
				Number n = super.nextValueInTable(session, tableCondition) ;
				
				int hival = n.intValue() ;
				lo = (hival == 0) ? 1 : 0;
				hi = hival * ( maxLo+1 );
				
				if ( log.isDebugEnabled() ){
					log.debug("new hi value: " + hival);
				}
			}finally{
				session.close() ;
			}
		}
		
		return IdentifierGeneratorFactory.createNumber(hi + lo++, this.returnType) ;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.tm = guzzContext.getTransactionManager() ;
	}
	
}
