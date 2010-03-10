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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.util.StringUtil;


/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractBusinessInterpreter implements BusinessInterpreter {
	protected transient final Log log = LogFactory.getLog(getClass()) ;	
	
	/**将字符串转化成IFieldConvert的类型对象表. 字段类型~转换器*/
	private Map registeredDataTypeHandlers = new HashMap() ;
	
	/**
	 * 域对象的属性对应的type handler。当用户传入诸如name=harry的参数时，用来确定name属性的数据类型，以定位
	 * harry应该解析为的类型，如将harry转换成string对象。
	 */
	private Map fieldHandlers = new HashMap() ;	
		
	/**
	 * init the BusinessInterpreter with the domainClass, and the properties of the domainClass defined.
	 * 
	 * @param domainClass
	 * @param props If the props is null, reflect all get-is-methods to make the props list.
	 */
	public void initUsingDomainClass(Class domainClass, List props) throws ClassNotFoundException{
		registeredDataTypeHandlers.putAll(JavaTypeHandlers.COMMON_DATA_TYPE_HANDLERS) ;		
		
		if(props == null){
			Method[] ms = domainClass.getMethods() ;
			
			//先把所有的get方法做好映射
			for(int i = 0 ; i < ms.length ; i++){
				Method m = ms[i] ;
				String methodName = m.getName() ;			
				
				if("getClass".equals(methodName)){
					continue ;
				}
				
				String guessedFieldName = null ;
				String guessedOrginalName = null ;			
				
				if(methodName.startsWith("get")){
					guessedFieldName = Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4) ;
					guessedOrginalName = methodName.substring(3) ;
				}else if(methodName.startsWith("is")){//对is boolean的支持
					guessedFieldName = Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3) ;
					guessedOrginalName = methodName.substring(2) ;
				}else{ //不是javabean read方法
					continue ;
				}
				
				Class retType = m.getReturnType() ;
				
				try{
					IDataTypeHandler dth = getConvert(retType) ;
					fieldHandlers.put(guessedFieldName, dth) ;
					fieldHandlers.put(guessedOrginalName, dth) ; //避免一些全大写的变量命名，如IP。
				}catch(Throwable e){
					throw new InvalidConfigurationException("error while init class:[" + domainClass + "]'s method:[" + m + "], guessed field is:" + guessedFieldName, e) ;
				}
			}
		}else{
			for(int i = 0 ; i < props.size() ; i++){
				String propName = (String) props.get(i) ;
				if(StringUtil.isEmpty(propName)){
					throw new InvalidConfigurationException("propName cann't be empty. props is:" + props) ;
				}
				
				String newPropName = null ;
				if(propName.length() == 1){
					newPropName = propName.toUpperCase() ;
				}else{
					newPropName = Character.toUpperCase(propName.charAt(0)) + propName.substring(1) ;
				}
				
				Method m = null ;
				try {
					m = domainClass.getMethod("get" + newPropName, null) ;
				} catch (Exception e) {
					try {
						m = domainClass.getMethod("is" + newPropName, null) ;
					} catch (Exception e1) {
						throw new InvalidConfigurationException("unknown property:[" + propName + "] of class:[" + domainClass.getName() + "]") ;
					}
				}
				
				Class retType = m.getReturnType() ;
				try{
					fieldHandlers.put(propName, getConvert(retType)) ;
				}catch(Throwable e){
					throw new InvalidConfigurationException("error while init class:[" + domainClass + "]'s method:[" + m + "], field is:" + propName, e) ;
				}
			}
			
		}
		
		initUserTypeHandler() ;
	}
	
	/**
	 * ~=符号表示相似，区分大小写，仅对字符串有效。
	 * ~~符号表示相似，不区分大小写，仅对字符串有效。
	 * @throws Exception 
	 * 
	 */
	public Object explainCondition(Object limitTo) throws Exception {
		
		if(limitTo instanceof java.lang.String){ //字段 + 操作 + 字段值。如：name=地球 ; count>34
			String limit = (String) limitTo ;
			
			ConditionSegment cs = ConditionSegment.parseFromString(limit, fieldHandlers) ;
			
			if(cs != null){
				return explainParamedCondition(cs.fieldName, cs.operator, cs.value) ;
			}			
			
			//没有条件的，可能只是一些简短用语。例如：checked, mychecked, my....
			return explainWellKnownCondition(limit) ;
		}else{
			return explainOtherTypeConditon(limitTo) ;
		}
	}
	
	protected Object explainOtherTypeConditon(Object limitTo){
		throw new RuntimeException(this.getClass().getName() + " doesn't support condition type:" + limitTo.getClass().getName()) ;
	}	
	
	/**执行两个条件的and操作*/
	protected abstract Object explainConditionsAndOperation(Object conditionA, Object conditionB) ;
	
	/**
	 * 构造数据库执行条件。
	 * 
	 * 可以在此处加入权限判断，避免非法用户使用涉及权限的字段进行检索。
	 * 
	 */
	protected abstract Object explainParamedCondition(String fieldName, LogicOperation operation, Object fieldValue) ;
	
	/**
	 * 进行一些应用定义的著名代名词条件解析。
	 */
	protected Object explainWellKnownCondition(String limitTo){
		throw new RuntimeException(this.getClass().getName() + " doesn't support condition type:" + limitTo) ;
	}
	
	public final void registerUserTypeHandler(String userTypeClassName, IDataTypeHandler cls){
		registeredDataTypeHandlers.put(userTypeClassName, cls) ;
	}
	
	/**初始化ghost自身特殊数据类型字段的处理。{@link #registerUserTypeHandler(String, Class)}*/
	protected void initUserTypeHandler(){
		
	}
	
	protected IDataTypeHandler getConvert(Class fieldType){
		String type = fieldType.getName() ;
		
		IDataTypeHandler ihandler = (IDataTypeHandler) registeredDataTypeHandlers.get(type) ;
		
		if(ihandler == null){
			if(log.isInfoEnabled()){
				log.info("no IDataTypeHandler found for class:" + fieldType + " in ghost:" + this.getClass()) ;
			}
			
			return JavaTypeHandlers.getUnsupportedDataHandler(fieldType) ;
		}
		
		return ihandler ;
	}

}
