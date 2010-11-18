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
package org.guzz.orm.se;

import java.util.Collection;

/**
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Terms {
	
	public static CompareTerm eq(String propName, int paramValue){
		return new CompareTerm(propName, CompareTerm.EQUALS , new Integer(paramValue)) ;
	}
	
	public static CompareTerm notEq(String propName, int paramValue){
		return new CompareTerm(propName, CompareTerm.NOT_EQUALS , new Integer(paramValue)) ;
	}
	
	public static CompareTerm notEq(String propName, Object paramValue){
		return new CompareTerm(propName, CompareTerm.NOT_EQUALS , paramValue) ;
	}
		
	public static CompareTerm eq(String propName, Object paramValue){		
		return new CompareTerm(propName, CompareTerm.EQUALS , paramValue) ;
	}
	
	public static CompareTerm smaller(String propName, int paramValue){
		return new CompareTerm(propName, CompareTerm.SMALLER , new Integer(paramValue)) ;
	}
	
	public static CompareTerm smaller(String propName, Object paramValue){
		return new CompareTerm(propName, CompareTerm.SMALLER , paramValue) ;
	}
	
	public static CompareTerm smallerOrEq(String propName, Object paramValue){
		return new CompareTerm(propName, CompareTerm.SMALLER_OR_EQUALS , paramValue) ;
	}
	
	public static CompareTerm bigger(String propName, Object paramValue){
		return new CompareTerm(propName, CompareTerm.BIGGER , paramValue) ;
	}
	
	public static CompareTerm biggerOrEq(String propName, Object paramValue){
		return new CompareTerm(propName, CompareTerm.BIGGER_OR_EQUALS , paramValue) ;
	}
	
	public static CompareTerm bigger(String propName, int paramValue){		
		return new CompareTerm(propName, CompareTerm.BIGGER , new Integer(paramValue)) ;
	}
	
	public static CompareTerm biggerOrEq(String propName, int paramValue){
		return new CompareTerm(propName, CompareTerm.BIGGER_OR_EQUALS , new Integer(paramValue)) ;
	}
	
	public static CompareTerm like(String propName, String paramValue, boolean ignoreCase){
		return new StringCompareTerm(propName, StringCompareTerm.LIKE , paramValue, ignoreCase) ;
	}
	
	public static CompareTerm stringEq(String propName, String paramValue, boolean ignoreCase){
		return new StringCompareTerm(propName, StringCompareTerm.EQUALS , paramValue, ignoreCase) ;
	}
	
	public static InTerm in(String propName, int[] values){		
		return new InTerm(propName, values) ;
	}
	
	public static InTerm in(String propName, Collection values){		
		return new InTerm(propName, values) ;
	}
	
	public static IsNullTerm isNull(String propName){		
		return new IsNullTerm(propName) ;
	}
	
	public static NotNullTerm notNull(String propName){		
		return new NotNullTerm(propName) ;
	}
	
}
