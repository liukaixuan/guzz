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
package org.guzz.orm;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface BusinessInterpreter {

	/**
	 * 解释限制性查询条件。对应于标签中的limit条件。
	 * 返回null表示此条件可以忽略。
	 * 如果遇到当前用户没有权限的查询字段，抛出异常(TODO: 增加权限接口支持)。
	 * 
	 * @param limitTo 查询条件
	 */
	public Object explainCondition(Object limitTo) throws Exception  ;
	
}
