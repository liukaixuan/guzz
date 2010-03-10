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
package org.guzz.service.log;

import org.guzz.Guzz;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface LogService {
	
	/**
	 * 记录日志。如果存在shadow表，按照{@link Guzz#getTableCondition()}分表
	 * @param logObject 日志对象
	 **/
	public void log(Object logObject) ;
	
	/**
	 * 记录日志。
	 * @param logObject 日志对象
	 * @param tableCondition shadow表分表条件。如果tableCondition为null，将会使用null作为分表条件，不在读取{@link Guzz#getTableCondition()}
	 **/
	public void log(Object logObject, Object tableCondition) ;

}
