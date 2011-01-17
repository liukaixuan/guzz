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

import org.guzz.dialect.Dialect;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.StringUtil;


/**
 * <b>uuid</b><br>
 * <br>
 * A <tt>UUIDGenerator</tt> that returns a string of length 32,
 * This string will consist of only hex digits. Optionally,
 * the string may be generated with separators between each
 * component of the UUID.
 *
 * Mapping parameters supported: separator.
 *
 */

public class UUIDHexGenerator extends AbstractUUIDGenerator implements Configurable {
	private POJOBasedObjectMapping mapping ;
	private String primaryKeyPropName ;
	
	private String sep = "";

	protected String format(int intval) {
		String formatted = Integer.toHexString(intval);
		StringBuffer buf = new StringBuffer("00000000");
		buf.replace( 8-formatted.length(), 8, formatted );
		return buf.toString();
	}

	protected String format(short shortval) {
		String formatted = Integer.toHexString(shortval);
		StringBuffer buf = new StringBuffer("0000");
		buf.replace( 4-formatted.length(), 4, formatted );
		return buf.toString();
	}

	protected String generateUUID() {
		return new StringBuffer(36)
			.append( format( getIP() ) ).append(sep)
			.append( format( getJVM() ) ).append(sep)
			.append( format( getHiTime() ) ).append(sep)
			.append( format( getLoTime() ) ).append(sep)
			.append( format( getCount() ) )
			.toString();
	}
	
	protected void setPrimaryKey(Object domainObject, Object value){
		mapping.getBeanWrapper().setValue(domainObject, primaryKeyPropName, value) ;
	}

	public void configure(Dialect dialect, POJOBasedObjectMapping mapping, Properties params) {
		this.mapping = mapping ;
		primaryKeyPropName = mapping.getTable().getPKPropName() ;
		
		String sep = params.getProperty("separator") ;
		if(StringUtil.notEmpty(sep)){
			this.sep = sep ;
		}
	}

	public boolean insertWithPKColumn() {
		return true ;
	}

	public Serializable postInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		return null;
	}

	public Serializable preInsert(WriteTranSession session, Object domainObject, Object tableCondition) {
		String uuid = generateUUID() ;		
		setPrimaryKey(domainObject, uuid) ;		
		return (Serializable) uuid ;
	}

	public static void main( String[] args ) throws Exception {
		UUIDHexGenerator gen = new UUIDHexGenerator();
		UUIDHexGenerator gen2 = new UUIDHexGenerator();
		gen.sep = "/" ;

		for ( int i=0; i<10; i++) {
			String id = (String) gen.generateUUID();
			System.out.println(id);
			String id2 = (String) gen2.generateUUID();
			System.out.println(id2);
		}

	}

}
