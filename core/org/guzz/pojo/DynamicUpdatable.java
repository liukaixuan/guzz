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
package org.guzz.pojo;

/**
 * 
 * the POJO Object can tell what properties has been changed inside?
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public interface DynamicUpdatable {
	
	/**
	 * list the attributes that had been changed compared with the db. including lazied attribute.
	 * 
	 * @return attributea to update. return null to save all props, return String[0] to ignore update operation.
	 */
	public String[] getChangedProps() ;

}
