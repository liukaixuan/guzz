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

import java.util.LinkedList;

import org.guzz.exception.DaoException;
import org.guzz.orm.ObjectMapping;
import org.guzz.util.StringUtil;


/**
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class OrderByTerm implements SearchTerm {
	
	private LinkedList orders = new LinkedList() ;

	public OrderByTerm() {
	}
	
	/**以字符串的方式设置order by 字段。设置的字段内容必须全部为java property，不能包含db column name。
	 * @param orderBy 例如：id asc, createdTime desc
	 * */
	public OrderByTerm(String orderBy){
		if(StringUtil.isEmpty(orderBy)) return ;
		
		String[] ss = StringUtil.splitString(orderBy, ",") ;
		for(int i = 0 ; i < ss.length ; i++){
			String m_s = ss[i].trim() ;
			int startPos = m_s.indexOf(' ') ;
			if(startPos < 1){
				throw new DaoException("order by:[" + orderBy + "] not supported.") ;
			}
			
			_OrderBy o = new _OrderBy() ;
			o.propName = m_s.substring(0, startPos) ;
			
			String m_order = m_s.substring(startPos + 1).trim().toLowerCase() ;
			if("desc".equals(m_order)){
				o.isASC = false ;
			}else if("asc".equalsIgnoreCase(m_order)){
				o.isASC = true ;
			}else{
				throw new DaoException("order by:[" + orderBy + "] not supported.") ;
			}
			
			orders.addLast(o) ;
		}
	}
	
	/**
	 * 在现有order by基础上，增加一个order。
	 * @param propName 域对象的property name
	 * @param isAsc true按照升序排，false按照降序排。
	 */
	public void addOrder(String propName, boolean isAsc){
		_OrderBy o = new _OrderBy() ;
		o.propName = propName ;
		o.isASC = isAsc ;
		orders.addLast(o) ;
	}

	public String toExpression(SearchExpression se, ObjectMapping mapping, SearchParams params) {
		if(orders.isEmpty()) return "" ;
		
		StringBuffer sb = new StringBuffer(16) ;
		sb.append("order by") ;
		
		for(int i = 0 ; i < orders.size() ; i++){
			_OrderBy o = (_OrderBy) orders.get(i) ;
			
			String colName = mapping.getColNameByPropName(o.propName) ;
			if(colName == null){
				throw new DaoException("unknown property [" + o.propName + "] in order by.") ;
			}
			
			if(i > 0){ //从第二个order by开始，增加一个逗号。
				sb.append(',') ;
			}
			
			sb.append(' ').append(colName).append(' ') ;
			
			if(o.isASC){
				sb.append("asc") ;
			}else{
				sb.append("desc") ;
			}	
		}
		
		return sb.toString() ;
	}
	
	static class _OrderBy{		
		public String propName ;
		public boolean isASC ;
	}
	
}
