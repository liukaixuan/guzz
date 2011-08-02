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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.DataTypeException;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.orm.ObjectMapping;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractBusinessInterpreter implements BusinessInterpreter {
	protected transient final Log log = LogFactory.getLog(getClass()) ;	
	
	/**
	 * ~=符号表示相似，区分大小写，仅对字符串有效。
	 * ~~符号表示相似，不区分大小写，仅对字符串有效。
	 * @throws Exception 
	 * 
	 */
	public Object explainCondition(ObjectMapping mapping, Object limitTo) throws Exception {
		
		if(limitTo instanceof java.lang.String){ //字段 + 操作 + 字段值。如：name=地球 ; count>34
			String limit = (String) limitTo ;
			
			ConditionSegment cs = ConditionSegment.parseFromString(mapping, limit) ;
			
			if(cs != null){
				return explainParamedCondition(cs.fieldName, cs.operator, cs.value) ;
			}			
			
			//没有条件的，可能只是一些简短用语。例如：checked, mychecked, my....
			return explainWellKnownCondition(limit) ;
		}else{
			return explainOtherTypeConditon(mapping, limitTo) ;
		}
	}
	
	protected Object explainOtherTypeConditon(ObjectMapping mapping, Object limitTo){
		return explainOtherTypeConditon(limitTo) ;
	}
	
	protected Object explainOtherTypeConditon(Object limitTo){
		throw new DataTypeException(this.getClass().getName() + " doesn't support condition type:" + limitTo.getClass().getName()) ;
	}	
	
	/**执行两个条件的and操作*/
	protected abstract Object explainConditionsAndOperation(Object conditionA, Object conditionB) ;
	
	/**
	 * 构造数据库执行条件。
	 * 
	 * 可以在此处加入权限判断，避免非法用户使用涉及权限的字段进行检索。
	 * 
	 * @param propName
	 * @param operator
	 * @param propValue
	 */
	protected abstract Object explainParamedCondition(String propName, LogicOperation operator, Object propValue) ;
	
	/**
	 * 进行一些应用定义的著名代名词条件解析。
	 */
	protected Object explainWellKnownCondition(String limitTo){
		throw new DataTypeException(this.getClass().getName() + " doesn't support condition type:" + limitTo) ;
	}

	public void shutdown() throws Exception {		
	}

	public void startup() {
	}

}
