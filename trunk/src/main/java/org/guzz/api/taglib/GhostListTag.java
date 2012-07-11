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
package org.guzz.api.taglib;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;

import org.guzz.orm.Business;
import org.guzz.orm.se.SearchExpression;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 * @author liu kaixuan
 */
public class GhostListTag extends SummonTag {
	
	//TODO: 增加根据xml配置项目中CompiledSQL进行查询的标签
	
	private int skipCount = 0 ;
	
	private int pageNo = 1 ;
	
	private int pageSize = 20 ;
	
	private String orderBy ;	
		
	protected void init() {
		super.init();
		
		this.skipCount = 0 ;
		this.pageNo = 1 ;
		this.pageSize = 20 ;
		this.orderBy = null ;
	}
		
	protected Object summonGhosts(Business business, List conditions) throws JspException, IOException {
		SearchExpression se = SearchExpression.forBusiness(business.getName(), pageNo, pageSize) ;
		se.setTableCondition(getTableCondition()) ;
		se.setSkipCount(skipCount) ;
		se.and(conditions) ;
		if(StringUtil.notEmpty(orderBy)){
			se.setOrderBy(orderBy) ;
		}
		
		ReadonlyTranSession tran = guzzContext.getTransactionManager().openDelayReadTran() ;
		
		try{
			return tran.list(se) ;
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

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}
}
