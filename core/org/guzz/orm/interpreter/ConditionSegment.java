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
package org.guzz.orm.interpreter;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.DaoException;
import org.guzz.util.ObjectCompareUtil;
import org.guzz.util.javabean.BeanWrapper;


/**
 * 
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class ConditionSegment /*implements Comparable*/{
	private static final Log log = LogFactory.getLog(ConditionSegment.class) ;
	
	private static String[] operates = {"!=", "~~", "=~=", "~=", "<>", "==", ">=", "<=", "=", ">", "<"} ;
	private static LogicOperation[] opValues = {
			LogicOperation.NOT_EQUAL,
			LogicOperation.LIKE_IGNORE_CASE,
			LogicOperation.EQUAL_IGNORE_CASE,
			LogicOperation.LIKE_CASE_SENSTIVE,
			LogicOperation.NOT_EQUAL,
			LogicOperation.EQUAL,
			LogicOperation.BIGGER_OR_EQUAL,
			LogicOperation.SMALLER_OR_EQUAL,
			LogicOperation.EQUAL,
			LogicOperation.BIGGER,
			LogicOperation.SMALLER
		} ;
		
	public ConditionSegment(){		
	}
	
	public static ConditionSegment getByFieldName(List conditions, String fieldName){
		for(int i = 0 ; i < conditions.size(); i++){
			ConditionSegment cs = (ConditionSegment) conditions.get(i) ;
			
			if(cs.fieldName.equals(fieldName)){
				return cs ;
			}
		}
		return null ;
	}
	
	public ConditionSegment(String fieldName, LogicOperation operator, Object value){
		this.fieldName = fieldName ;
		this.operator = operator ;
		this.value = value ;
	}

	public String fieldName ;
	
	public LogicOperation operator ;
	
	/**值为null，表示适合任何值*/
	public Object value ;
	
	public String toString(){
		StringBuffer sb = new StringBuffer() ;
		sb.append(fieldName).append(operator) ;
		if(value == null){
			sb.append("?") ;
		}else{
			sb.append(value) ;
		}
		return sb.toString() ;
	}	
			
	/**判断参数对象中的字段值是否满足本Condition条件。如词condition为：id > 3，则如果param object的字段id的值>3，则返回true.*/
	public boolean matchCondition(Object object){
		BeanWrapper bw = new BeanWrapper(object.getClass()) ;
		
		Object value = bw.getValue(object, this.fieldName) ;
		
		if(this.operator == LogicOperation.EQUAL){
			return ObjectCompareUtil.objectEquals(this.value, value) ;
		}else if(this.operator == LogicOperation.BIGGER){
			return ObjectCompareUtil.objectBigger(this.value, value) ;
		}else if(this.operator == LogicOperation.BIGGER_OR_EQUAL){
			return ObjectCompareUtil.objectBiggerOrEquals(this.value, value) ;
		}else if(this.operator == LogicOperation.SMALLER){
			return ObjectCompareUtil.objectSmaller(this.value, value) ;
		}else if(this.operator == LogicOperation.SMALLER_OR_EQUAL){
			return ObjectCompareUtil.objectSmallerOrEquals(this.value, value) ;			
		}else if(this.operator == LogicOperation.NOT_EQUAL){
			return !ObjectCompareUtil.objectEquals(this.value, value) ;
		}else if(this.operator == LogicOperation.EQUAL_IGNORE_CASE){
			return ObjectCompareUtil.objectEquals(this.value == null ? null : ((String) this.value).toLowerCase(), value == null ? null : ((String) value).toLowerCase()) ;
		}else{
			log.error(object.getClass() + " cann't unsupported operator. condition is:" + this.toString()) ;
			return false ;
		}
	}
	
//	通过形如：abc=2的语法条件，创建一个ConditionSegment。如果传入的conditoin不识别，如为wellknown条件，返回null.
	public static ConditionSegment parseFromString(String condition, Map fieldHandlers){
		condition = condition.trim() ;	
				
		for(int i = 0 ; i < operates.length ; i++){
			String m_op = operates[i] ;
			
			int pos = condition.indexOf(m_op) ;
			if(pos < 0) continue ;
			
			String fieldName = condition.substring(0, pos).trim() ;
			String fieldValue = condition.substring(pos + m_op.length()).trim() ;
			
			Object value = fieldValue ;
			
			if(fieldHandlers != null){
				IDataTypeHandler m_hander = (IDataTypeHandler) fieldHandlers.get(fieldName) ;
				
				if(m_hander == null){
					throw new DaoException("conditon:[" + condition + "] 's field:[" + fieldName + "]不存在，或者数据类型没有支持的IDataTypeHandler") ;
				}
				
				value = m_hander.getValue(fieldValue) ;
			}
			
			LogicOperation op = opValues[i] ;
						
			return new ConditionSegment(fieldName, op, value) ;
		}
		
		return null ;
	}

	public static ConditionSegment parseFromString(String fieldName, String operator, Object fieldValue) {
		for(int i = 0 ; i < operates.length ; i++){
			String m_op = operates[i] ;
			
			if(m_op.equals(operator)){
				return new ConditionSegment(fieldName.trim(), opValues[i], fieldValue) ;
			}
		}
		throw new DaoException("invalid operator:[" + operator + "]. operator must be one of the [!=, ~~, =~=, ~=, <>, ==, =, >, <, >=, <=]") ;
	}

//	public int compareTo(Object o) {
//		return this.fieldName.compareTo(((ConditionSegment)o).fieldName);
//	}
//
//	public boolean equals(Object obj) {
//		if(obj == null) return false ;
//		if(this == obj) return true ;
//		
//		if(!(obj instanceof ConditionSegment)){
//			return false ;
//		}
//		
//		return this.fieldName.equals(((ConditionSegment)obj).fieldName);
//	}
}
