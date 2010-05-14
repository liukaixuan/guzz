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
import java.util.Iterator;

import org.guzz.exception.DaoException;
import org.guzz.orm.ObjectMapping;

/**
 * 构建sql的in操作
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class InTerm implements SearchTerm {
	
	private String propName ;
	private Collection values ;
	private int[] i_values ;

	/**
	 * 构造in操作短语。
	 * @param propName 进行in操作的对象属性名称
	 * @param values propName的可选值。
	 *   注意：sql将直接连接，如果参数值为string，应该注意将每个string加上''（引号）之类的，避免sql语法错误。
	 *        InTerm不会自动补上这些引号之类的东西。
	 */
	public InTerm(String propName, Collection values) {
		this.propName = propName ;
		this.values = values ;
	}
	
	public InTerm(String propName, int[] values) {
		this.propName = propName ;
		this.i_values = values ;
	}

	public String toExpression(SearchExpression se, ObjectMapping mapping, SearchParams params) {
		//don't check the params, this shouldn't be null!
		String colName = mapping.getColNameByPropNameForSQL(propName) ;
		if(colName == null){
			throw new DaoException("unknown property [" + propName + "] in se.") ;
		}
				
		StringBuffer sb = new StringBuffer(64) ;
		sb.append(colName).append(" in(") ;
		
		if(values != null){
			boolean notFirstElement = false ; 
			Iterator ii = values.iterator() ;
			
			while(ii.hasNext()){
				Object value = ii.next() ;
				
				if(notFirstElement){
					sb.append(",") ;
				}else{
					notFirstElement = true ;
				}
				
				String m_mark = propName + "_" + params.getNextSeq() ;
				params.addParam(propName, m_mark, value) ;
				
				sb.append(" :").append(m_mark) ;
			}
		}else if(i_values != null){
			for(int i = 0 ; i < i_values.length ; i++){
				if(i > 0){
					sb.append(",") ;
				}
				
				String m_mark = propName + "_" + params.getNextSeq() ;
				params.addParam(propName, m_mark, new Integer(i_values[i])) ;
				
				sb.append(" :").append(m_mark) ;
				
			}
		}
		
		sb.append(")") ;
		
		return sb.toString() ;
	}

}
