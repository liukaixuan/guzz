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
import org.guzz.util.StringUtil;


/**
 * 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class WhereTerm implements SearchTerm {
	
	private SearchTerm condition ;

	public WhereTerm(SearchTerm condition) {
		this.condition = condition ;
	}

	public String toExpression(SearchExpression se, ObjectMapping mapping, SearchParams params) {
		if(condition == null) return "" ;
		
		if(condition instanceof WhereTerm){ //允许程序使用错误而造成多次使用WhereTerm 
			return condition.toExpression(se, mapping, params) ;
		}
		
		String m_con = condition.toExpression(se, mapping, params) ;
		
		if(StringUtil.isEmpty(m_con)){
			return "" ;
		}
		return "where " + m_con ;
	}

	public SearchTerm getCondition() {
		return condition;
	}

	public void setCondition(SearchTerm condition) {
		this.condition = condition;
	}

	public boolean isEmptyQuery() {
		if(condition == null) return false ;
		
		return condition.isEmptyQuery();
	}

}
