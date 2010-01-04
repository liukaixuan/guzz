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

/**
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class Terms {
	
	public static CompareTerm eq(String paramName, int paramValue){
		return new CompareTerm(paramName, CompareTerm.EQUALS , new Integer(paramValue)) ;
	}
	
	public static CompareTerm notEq(String paramName, int paramValue){
		return new CompareTerm(paramName, CompareTerm.NOT_EQUALS , new Integer(paramValue)) ;
	}
	
	public static CompareTerm notEq(String paramName, Object paramValue){
		return new CompareTerm(paramName, CompareTerm.NOT_EQUALS , paramValue) ;
	}
		
	public static CompareTerm eq(String paramName, Object paramValue){		
		return new CompareTerm(paramName, CompareTerm.EQUALS , paramValue) ;
	}
	
	public static CompareTerm smaller(String paramName, int paramValue){
		return new CompareTerm(paramName, CompareTerm.SMALLER , new Integer(paramValue)) ;
	}
	
	public static CompareTerm smaller(String paramName, Object paramValue){
		return new CompareTerm(paramName, CompareTerm.SMALLER , paramValue) ;
	}
	
	public static CompareTerm smallerOrEq(String paramName, Object paramValue){
		return new CompareTerm(paramName, CompareTerm.SMALLER_OR_EQUALS , paramValue) ;
	}
	
	public static CompareTerm bigger(String paramName, Object paramValue){
		return new CompareTerm(paramName, CompareTerm.BIGGER , paramValue) ;
	}
	
	public static CompareTerm biggerOrEq(String paramName, Object paramValue){
		return new CompareTerm(paramName, CompareTerm.BIGGER_OR_EQUALS , paramValue) ;
	}
	
	public static CompareTerm bigger(String paramName, int paramValue){		
		return new CompareTerm(paramName, CompareTerm.BIGGER , new Integer(paramValue)) ;
	}
	
	public static CompareTerm biggerOrEq(String paramName, int paramValue){
		return new CompareTerm(paramName, CompareTerm.BIGGER_OR_EQUALS , new Integer(paramValue)) ;
	}
	
	public static CompareTerm like(String paramName, String paramValue, boolean ignoreCase){
		return new StringCompareTerm(paramName, StringCompareTerm.LIKE , paramValue, ignoreCase) ;
	}
	
	public static CompareTerm stringEq(String paramName, String paramValue, boolean ignoreCase){
		return new StringCompareTerm(paramName, StringCompareTerm.EQUALS , paramValue, ignoreCase) ;
	}
	
}
