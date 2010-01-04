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

import org.guzz.orm.ObjectMapping;

/**
 * 
 * 用于动态查询（主要是代码中调用）的查询语句拼装对象。
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface SearchTerm {
	
	public String toExpression(SearchExpression se, ObjectMapping mapping, SearchParams params) ;
	
	/**获取检索表达式的参数，如果没有参数返回null*/
//	public Map getParameters() ;
	
	//TODO: 改进方法，使得SearchTerm直接产出CompliedSQL，避免NamedParameter的转换步骤。

}
