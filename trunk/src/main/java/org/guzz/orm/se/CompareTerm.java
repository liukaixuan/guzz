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

import org.guzz.exception.DaoException;
import org.guzz.orm.ObjectMapping;


/**
 * 注意：必须使用参数形式，利用PrepareStatment,然后通过参数注入来使用此类。
 * 此类不支持直接的sql拼接的字符对比操作。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class CompareTerm implements SearchTerm {
	
	public static final String EQUALS = "=" ;
	public static final String BIGGER = ">" ;
	public static final String SMALLER = "<" ;
	public static final String NOT_EQUALS = "!=" ;
	public static final String BIGGER_OR_EQUALS = ">=" ;
	public static final String SMALLER_OR_EQUALS = "<=" ;
	
	final String propName ;
	final Object propValue ;
	final String operator ;

	public CompareTerm(String propName, String operator ,Object propValue) {
		this.propName = propName ;
		this.propValue = propValue ;
		this.operator = operator ;
	}

	public String toExpression(SearchExpression se, ObjectMapping mapping, SearchParams params) {
		String colName = mapping.getColNameByPropNameForSQL(propName) ;
		if(colName == null){
			throw new DaoException("unknown property [" + propName + "] in se.") ;
		}
		
		StringBuffer sb = new StringBuffer(16) ;
		sb.append(propName).append('_').append(params.getNextSeq()) ;
		
		String propMark = sb.toString() ;
		
		params.addParam(propName, propMark, propValue) ;
		
		return colName + " " + operator + " :" + propMark ;
	}

	public boolean isEmptyQuery() {
		return false;
	}

}
