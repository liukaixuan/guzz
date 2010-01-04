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


/**
 * 
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public abstract class AbstractConcatTerm implements SearchTerm {

	protected SearchTerm leftTerm ;
	protected SearchTerm rightTerm ;
	
//	public Map getParameters() {		
//		Map params = new HashMap() ;
//		
//		if(leftTerm != null){
//			Map m_params = leftTerm.getParameters() ;
//			if(m_params != null){
//				
//				params.putAll(leftTerm.getParameters()) ;
//			}
//		}
//		
//		if(rightTerm != null){
//			Map m_params = rightTerm.getParameters() ;
//			if(m_params != null){
//				params.putAll(rightTerm.getParameters()) ;
//			}
//		}
//		
//		if(this.parameters != null){
//			params.putAll(this.parameters) ;
//		}
//				
//		return params.isEmpty() ? null : params ;
//	}

	public SearchTerm getLeftTerm() {
		return leftTerm;
	}

	public void setLeftTerm(SearchTerm leftTerm) {
		this.leftTerm = leftTerm;
	}

	public SearchTerm getRightTerm() {
		return rightTerm;
	}

	public void setRightTerm(SearchTerm rightTerm) {
		this.rightTerm = rightTerm;
	}

	
}
