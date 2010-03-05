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
package org.guzz.taglib.db;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.guzz.orm.Business;
import org.guzz.orm.se.SearchExpression;
import org.guzz.transaction.ReadonlyTranSession;

/**
 * 
 * 
 * @author liu kaixuan
 */
public class GhostCountTag extends SummonTag {
	
	private String selectPhrase ;
	
	protected void init(){
		this.selectPhrase = null ;
		super.init() ;
	}

	protected Object summonGhosts(Business business, List conditions) throws JspException, IOException {
		SearchExpression se = SearchExpression.forBusiness(business.getName()) ;
		se.setTableCondition(getTableCondition()) ;
		
		if(selectPhrase != null){
			se.setCountSelectPhrase(selectPhrase) ;
		}
		
		se.and(conditions) ;
		
		ReadonlyTranSession tran = guzzContext.getTransactionManager().openDelayReadTran() ;
		
		try{
			return new Long(tran.count(se)) ;
		}finally{
			tran.close() ;
		}
	}

	public String getSelectPhrase() {
		return selectPhrase;
	}

	public void setSelectPhrase(String selectPhrase) {
		this.selectPhrase = selectPhrase;
	}
	
}
