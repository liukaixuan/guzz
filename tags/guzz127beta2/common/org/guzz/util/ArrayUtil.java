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
package org.guzz.util;

import java.lang.reflect.Array;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class ArrayUtil {
	
	public static Object[] addToArray(Object[] oldArray, Object newObject){
		int length = oldArray.length ;
		
		Object[] newArray = (Object[]) Array.newInstance(oldArray.getClass().getComponentType(), length + 1) ;
		System.arraycopy(oldArray, 0, newArray, 0, length) ;
		
		newArray[length] = newObject ;
		
		return newArray ;
	}
	
	public static Object[] addToArray(Object[] oldArray, Object[] newElements){
		int length = oldArray.length ;
		int newLength = newElements.length ;
		
		if(newLength == 0) return oldArray ;
		
		Object[] newArray = (Object[]) Array.newInstance(oldArray.getClass().getComponentType(), length + newLength) ;
		System.arraycopy(oldArray, 0, newArray, 0, length) ;
		
		for(int i = 0 ; i < newLength ; i++){
			newArray[length + i] = newElements[i] ;
		}
		
		return newArray ;
	}
	
	public static boolean inArray(Object[] ps, Object p){
		if(ps == null) return false ;
		
		for(int i = 0 ; i < ps.length ; i++){
			if(ps[i] == p) return true ;
			if(ps[i].equals(p)) return true ;		
		}
		
		return false ;
	}
	
	public static String arrayToString(Object[] a){
		if(a == null) return null ;
		
		StringBuffer sb = new StringBuffer(a.length * 8) ;
		
		for(int i = 0 ; i < a.length ; i++){
			if(i > 0){
				sb.append(", ") ;
			}
			
			sb.append(a[i].toString()) ;
		}
		
		return sb.toString() ;
	}

}
