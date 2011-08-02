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

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SearchParams {
	
	private Map searchParams = new HashMap() ;
	private Map paramPropMapping = new HashMap() ;
	
	private int count = 0 ;
	
	public int getNextSeq(){
		return count++ ;
	}
	
	/**
	 * 
	 * @param propName 
	 * @param paramName
	 * @param paramValue
	 */
	public void addParam(String propName, String paramName, Object paramValue){
		searchParams.put(paramName, paramValue) ;
		paramPropMapping.put(paramName, propName) ;
	}
	
	public Map getSearchParams(){
		return this.searchParams ;
	}
	
	public Map getParamPropMapping(){
		return this.paramPropMapping ;
	}
	
}
