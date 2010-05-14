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
 * 标明select语句需要提取的字段。@link PropsSelectTerm 中仅存放数据库字段对应的java property名称，不包含表和域对象信息。
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class PropsSelectTerm implements SearchTerm {

	private LinkedList props = new LinkedList() ;
	
	public PropsSelectTerm(String selectedProps) {
		if(StringUtil.isEmpty(selectedProps)) return ;
		
		String[] ss = StringUtil.splitString(selectedProps, ",") ;
		for(int i = 0 ; i < ss.length ; i++){
			String propName = ss[i].trim() ;
			
			props.addLast(propName) ;
		}
	}
	
	public void addProp(String propName){
		props.addLast(propName) ;
	}

	public String toExpression(SearchExpression se, ObjectMapping mapping, SearchParams params) {
		if(props.isEmpty()) return "" ;
		
		StringBuffer sb = new StringBuffer(16) ;
		
		for(int i = 0 ; i < props.size() ; i++){
			String propName = (String) props.get(i) ;
			
			String colName = mapping.getColNameByPropNameForSQL(propName) ;
			if(colName == null){
				if("*".equalsIgnoreCase(propName)){
					colName = propName ;
				}else{
					throw new DaoException("unknown property [" + propName + "].") ;
				}
			}
			
			if(i > 0){ //从第二个order by开始，增加一个逗号。
				sb.append(',') ;
			}
			
			sb.append(' ').append(colName).append(' ') ;
		}
		
		return sb.toString() ;
	}

}
