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
package org.guzz.dialect;


/**
 * 
 * A dialect for Oracle 11g databases.
 *
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class Oracle11gDialect extends Oracle9iDialect {
	//same as 10g
	
	/**
	 * 
	 * Oracle recommends you to keep the batch sizes in the general range of 50 to 100.
	 * 
	 * @return 64
	 */
	public int getDefaultBatchSize(){
		return 64 ;
	}
	
}
