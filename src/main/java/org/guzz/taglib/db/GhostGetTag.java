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
import org.guzz.util.StringUtil;

/**
 * 加载数据。
 * 
 * @author liu kaixuan
 */
public class GhostGetTag extends SummonTag {
	
	private String orderBy ;		
		
	protected void init() {
		super.init();
		
		this.orderBy = null ;
	}
		
	protected Object summonGhosts(Business business, List conditions) throws JspException, IOException {
		SearchExpression se = SearchExpression.forBusiness(business.getName()) ;
		se.setTableCondition(getTableCondition()) ;
		se.and(conditions) ;
		
		if(StringUtil.notEmpty(orderBy)){
			se.setOrderBy(orderBy) ;
		}
		
		ReadonlyTranSession tran = guzzContext.getTransactionManager().openDelayReadTran() ;
		
		try{
			return tran.findObject(se) ;
		}finally{
			tran.close() ;
		}
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

}
