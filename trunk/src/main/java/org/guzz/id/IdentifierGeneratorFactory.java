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

import java.util.HashMap;

/**
 * Factory and helper methods for <tt>IdentifierGenerator</tt> framework.
 *
 */
public final class IdentifierGeneratorFactory {

	private static final HashMap GENERATORS = new HashMap();
	
	static {
		GENERATORS.put("identity", AutoIncrementIdGenerator.class.getName()) ;
		GENERATORS.put("assigned", AssignedIdGenerator.class.getName()) ;
		GENERATORS.put("sequence", SequenceIdGenerator.class.getName()) ;
		GENERATORS.put("silent", SilentIdGenerator.class.getName()) ;
		GENERATORS.put("guid", GUIDIdGenerator.class.getName()) ;
		GENERATORS.put("uuid", UUIDHexGenerator.class.getName()) ;
		GENERATORS.put("random", RandomIdGenerator.class.getName()) ;
		
		GENERATORS.put("hilo", TableHiLoGenerator.class.getName()) ;
		GENERATORS.put("seqhilo", SequenceHiLoGenerator.class.getName()) ;
		GENERATORS.put("hilo.multi", TableMultiIdGenerator.class.getName()) ;
	}
	
	public static String getGeneratorClass(String name){
		return (String) GENERATORS.get(name) ;
	}

	public static Number createNumber(long value, Class clazz) throws IdentifierGenerationException {
		if ( clazz == Long.class ) {
			return new Long( value );
		}
		else if ( clazz == Integer.class ) {
			return new Integer( ( int ) value );
		}
		else if ( clazz == Short.class ) {
			return new Short( ( short ) value );
		}
		else {
			throw new IdentifierGenerationException( "this id generator generates long, integer, short" );
		}
	}

	/**
	 * Disallow instantiation.
	 */
	private IdentifierGeneratorFactory() {
	}

}
