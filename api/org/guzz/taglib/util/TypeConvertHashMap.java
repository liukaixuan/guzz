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
package org.guzz.taglib.util;

import java.util.HashMap;
import java.util.Map;

import org.guzz.util.StringUtil;
import org.guzz.util.ViewFormat;

/**
 * 
 * 可以进行值类型转换的HashMap。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class TypeConvertHashMap extends HashMap {
	
	public TypeConvertHashMap(){
		super() ;
	}
	
	public TypeConvertHashMap(Map m){
		this.putAll(m) ;
	}
	
	public int getAsInt(String key){
		Object value = get(key) ;
		if(value == null) return -1 ;
		return StringUtil.toInt(get(key).toString()) ;
	}
	
	public String getAsString(String key){
		Object value = get(key) ;
		if(value == null) return null ;
		
		return value.toString() ;
	}
	
	public String getAsNotNullString(String key){
		return StringUtil.dealNull(getAsString(key)) ;
	}
	
	public int[] getAsIntArray(String key){
		Object value = get(key) ;
		if(value == null) return new int[0] ;
		
		if(!value.getClass().isArray()){
			String[] values = ViewFormat.reassembleAndSplitKeywords(value.toString()) ;
			int[] m_values = new int[values.length] ;
			
			for(int i = 0 ; i < values.length ; i++){
				m_values[i] = StringUtil.toInt(values[i]) ;
			}
			
			return m_values ;
		}
		
		String[] vs = (String[]) value ;
		
		int[] is = new int[vs.length] ;
		
		for(int i = 0 ; i < vs.length ; i++){
			is[i] = StringUtil.toInt(vs[i]) ;
		}
		
		return is ;
	}
	
	public boolean getAsBool(String key){
		Object value = get(key) ;
		if(value == null) return false ;
		
		char c = value.toString().charAt(0) ;
		
		if(c == '1' || c == 'y' || c == 'Y' || c == 't' || c == 'T'){
			return true ;
		}
		
		return false ;
	}

}
