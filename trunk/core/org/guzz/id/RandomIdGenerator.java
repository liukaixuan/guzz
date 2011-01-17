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
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Properties;

import org.guzz.dialect.Dialect;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;


/**
 * <b>random</b><br>
 * <br>
 * A <tt>RandomIdGenerator</tt> that returns a string of length 32 (or the length you given in parameter:length),
 * This string will consist of only a-z and 0-9, and is unable to predicate. 
 * 
 * <br><b>Note: the length maybe a little shorter than the given length.</b>
 * <br>
 * <br>Mapping parameters supported: length.
 */

public class RandomIdGenerator implements IdentifierGenerator, Configurable {
	private POJOBasedObjectMapping mapping ;
	private String primaryKeyPropName ;
	private SecureRandom random = new SecureRandom() ;
	private int length = 32 ;

	protected String random() {
		String s = new BigInteger(length*5, random).toString(36);
		
		if(s.length() > length){
			return s.substring(0, s.length()) ;
		}
		
		return s ;
	}
	
	protected void setPrimaryKey(Object domainObject, Object value){
		mapping.getBeanWrapper().setValue(domainObject, primaryKeyPropName, value) ;
	}

	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		this.mapping = mapping ;
		primaryKeyPropName = mapping.getTable().getPKPropName() ;
		
		this.length = StringUtil.toInt(params.getProperty("length"), 32) ;
	}

	public boolean insertWithPKColumn() {
		return true ;
	}

	public Serializable postInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		return null;
	}

	public Serializable preInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		String id = random() ;		
		setPrimaryKey(domainObject, id) ;		
		return id ;
	}

	public static void main( String[] args ) throws Exception {
		RandomIdGenerator gen = new RandomIdGenerator();
		RandomIdGenerator gen2 = new RandomIdGenerator();

		for ( int i=0; i<10; i++) {
			String id = (String) gen.random();
			System.out.println(id.length() + "=" + id);
			String id2 = (String) gen2.random();
			System.out.println(id2.length() + "=" + id2);
		}
	}

}
