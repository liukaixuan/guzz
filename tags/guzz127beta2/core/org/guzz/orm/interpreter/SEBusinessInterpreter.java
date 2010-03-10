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

import org.guzz.exception.DaoException;
import org.guzz.orm.se.AndTerm;
import org.guzz.orm.se.SearchTerm;
import org.guzz.orm.se.Terms;


/**
 * 
 * 默认支持SearchExpress查询条件解析。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class SEBusinessInterpreter extends AbstractBusinessInterpreter {

	protected Object explainConditionsAndOperation(Object conditionA, Object conditionB) {
		return new AndTerm((SearchTerm) conditionA, (SearchTerm) conditionB) ;
	}

	protected Object explainParamedCondition(String fieldName, LogicOperation operation, Object fieldValue) {
		
		if(operation == LogicOperation.EQUAL){
			return Terms.eq(fieldName, fieldValue) ;
		}else if(operation == LogicOperation.BIGGER){
			return Terms.bigger(fieldName, fieldValue) ;
		}else if(operation == LogicOperation.BIGGER_OR_EQUAL){
			return Terms.biggerOrEq(fieldName, fieldValue) ;
		}else if(operation == LogicOperation.SMALLER){
			return Terms.smaller(fieldName, fieldValue) ;
		}else if(operation == LogicOperation.SMALLER_OR_EQUAL){
			return Terms.smallerOrEq(fieldName, fieldValue) ;	
		}else if(operation == LogicOperation.NOT_EQUAL){
			return Terms.notEq(fieldName, fieldValue) ;
		}else if(operation == LogicOperation.EQUAL_IGNORE_CASE){
			return Terms.stringEq(fieldName, fieldValue + "", true) ;
		}else if(operation == LogicOperation.LIKE_CASE_SENSTIVE){
			return Terms.like(fieldName, fieldValue + "", false) ;
		}else if(operation == LogicOperation.LIKE_IGNORE_CASE){
			return Terms.like(fieldName, fieldValue + "", true) ;
		}else{
			throw new DaoException("unresolved LogicOperation. operation is:" + operation) ;
		}
	
	}

	public Object explainCondition(Object limitTo) throws Exception {
		//如果传入的是已经构造好的条件，直接返回原条件。
		if(limitTo instanceof SearchTerm){
			return limitTo ;
		}
		
		return super.explainCondition(limitTo);
	}

}
