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
import org.guzz.web.context.GuzzContextAware;

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
 * Mapping parameters supported: table, column, dbGroup, max_lo
 *
 * @see SequenceHiLoGenerator
 */
public class TableHiLoGenerator extends TableGenerator implements GuzzContextAware {
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
		this.returnType = pkDataType.getDataType() ;
	}

	public Serializable preInsert(WriteTranSession session, Object domainObject) {
		Serializable value ;
		
		//open a new transaction.
		WriteTranSession newSession = this.tm.openRWTran(true) ;
		
		try{
			value = generate(newSession) ;
		}finally{
			newSession.close() ;
		}
		
		setPrimaryKey(domainObject, value) ;
		
		return (Serializable) value ;
	}
	
	public synchronized Number generate(WriteTranSession session) {
		if (maxLo < 1) {
			//keep the behavior consistent even for boundary usages
			Integer n = super.nextValueInTable(session) ;
			
			if(n.intValue() == 0){
				n = super.nextValueInTable(session) ;
			}
			
			return n ;
		}
		
		if (lo > maxLo){
			Number n = super.nextValueInTable(session) ;
			
			int hival = n.intValue() ;
			lo = (hival == 0) ? 1 : 0;
			hi = hival * ( maxLo+1 );
			
			if ( log.isDebugEnabled() ){
				log.debug("new hi value: " + hival);
			}
		}
		
		return IdentifierGeneratorFactory.createNumber(hi + lo++, this.returnType) ;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.tm = guzzContext.getTransactionManager() ;
	}
	
}
