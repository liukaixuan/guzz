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
 * 构建sql的not null操作
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class NotNullTerm implements SearchTerm {
	
	private String propName ;

	/**
	 * 构造propName is not null操作短语。
	 * @param propName 进行is not null操作的对象属性名称
	 */
	public NotNullTerm(String propName) {
		this.propName = propName ;
	}

	public String toExpression(SearchExpression se, ObjectMapping mapping, SearchParams params) {
		String colName = mapping.getColNameByPropNameForSQL(propName) ;
		if(colName == null){
			throw new DaoException("unknown property [" + propName + "] in se.") ;
		}
		
		StringBuffer sb = new StringBuffer(16) ;
		sb.append(colName).append(" is not null") ;
		
		return sb.toString() ;
	}

	public boolean isEmptyQuery() {
		return false;
	}

}
