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
import org.guzz.dialect.Dialect;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;


/**
 * <b>seqhilo</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that combines a hi/lo algorithm with an underlying
 * oracle-style sequence that generates hi values. The user may specify a
 * maximum lo value to determine how often new hi values are fetched.<br>
 * <br>
 * If sequences are not available, <tt>TableHiLoGenerator</tt> might be an
 * alternative.<br>
 * <br>
 * Mapping parameters supported: sequence, max_lo, dbgroup, parameters.
 *
 * @see TableHiLoGenerator
 */
public class SequenceHiLoGenerator extends SequenceIdGenerator {
	private static final Log log = LogFactory.getLog(SequenceHiLoGenerator.class);
	
	public static final String MAX_LO = "max_lo";
	
	private int maxLo;
	private int lo;
	private long hi;
	
	private Class returnType ;
	
	public Serializable preInsert(WriteTranSession session, Object domainObject) {
		Serializable value = generate(session) ;
		
		setPrimaryKey(domainObject, value) ;
		
		return (Serializable) value ;
	}
	
	public Serializable postInsert(WriteTranSession session, Object domainObject) {
		return super.postInsert(session, domainObject) ;
	}
	
	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		super.configure(dialect, mapping, params) ;
		
		this.maxLo = StringUtil.toInt(params.getProperty(MAX_LO), 9) ;
		lo = maxLo + 1; // so we "clock over" on the first invocation
		this.returnType = pkDataType.getDataType() ;
	}

	public synchronized Number generate(WriteTranSession session) {
		if (maxLo < 1) {
			//keep the behavior consistent even for boundary usages
			Number n = super.nextSequenceValue(session) ;
			
			if(n.longValue() == 0L){
				n = super.nextSequenceValue(session) ;
			}
			
			return n ;
		}
		
		if (lo > maxLo){
			Number n = super.nextSequenceValue(session) ;
			
			long hival = n.longValue() ;
			lo = (hival == 0) ? 1 : 0;
			hi = hival * ( maxLo+1 );
			
			if ( log.isDebugEnabled() ){
				log.debug("new hi value: " + hival);
			}
		}
		
		return IdentifierGeneratorFactory.createNumber(hi + lo++, this.returnType) ;
	}

}
