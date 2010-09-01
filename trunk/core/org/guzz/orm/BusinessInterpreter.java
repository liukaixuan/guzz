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
	 * Translating the giving limit condition to a Search Condition guzz supported.
	 * 
	 * @param limitTo the condition user passed.
	 * @return supported search condition. return null if this condition can be ignored.
	 * @throw Exception Throw exception if the limit is not authorized.
	 */
	public Object explainCondition(ObjectMapping mapping, Object limitTo) throws Exception  ;
	
}
