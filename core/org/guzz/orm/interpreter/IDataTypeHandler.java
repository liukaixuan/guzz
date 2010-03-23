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

/**
 * 
 * 
 * @deprecated
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface IDataTypeHandler {

	//根据字母串，按照字段在Soul对象中定义的类型，转换成相应的数据类型对象。
	public Object getValue(String fieldValue) ;
	
}
