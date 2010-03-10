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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import org.guzz.dao.PageFlip;
import org.guzz.orm.Business;
import org.guzz.orm.se.SearchExpression;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.util.RequestUtil;
import org.guzz.util.StringUtil;
import org.guzz.web.context.GuzzWebApplicationContextUtil;


/**
 * 
 * 
 * @author liu kaixuan
 */
public class GhostPageTag extends SummonTag {
	
	private int skipCount = 0 ;
	
	private int pageNo = 0 ;
	
	private int pageSize = 0 ;
	
	private String orderBy ;
	
	private String pageNoParamName = "pageNo" ;	
	
	private int pageSpan = 10 ;
	
	private int pageBeforeSpan = -1 ;
	
	private int pageAfterSpan = -1 ;
	
	protected void init() {
		super.init();
		
		this.skipCount = 0 ;
		this.pageNo = -1 ;
		this.pageSize = -1 ;
		this.orderBy = null ;
		this.pageNoParamName = "pageNo" ;
		this.pageSpan = 10 ;
		this.pageBeforeSpan = -1 ;
		this.pageAfterSpan = -1 ;
	}
	
	protected Object summonGhosts(Business business, List conditions) throws JspException, IOException {
		//如果没有设置pageNo，pageSize，自动从request中读取。		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest() ;		
		if(pageNo == -1){
			pageNo = RequestUtil.getParameterAsInt(request, this.pageNoParamName, 1) ;
		}
		
		if(pageSize == -1){
			pageSize = RequestUtil.getParameterAsInt(request, "pageSize", 20) ;
		}
		
		
		SearchExpression se = SearchExpression.forBusiness(business.getName(), pageNo, pageSize) ;
		se.setTableCondition(getTableCondition()) ;
		se.setSkipCount(skipCount) ;
		se.and(conditions) ;
		if(StringUtil.notEmpty(orderBy)){
			se.setOrderBy(orderBy) ;
		}
		
		ReadonlyTranSession tran = guzzContext.getTransactionManager().openDelayReadTran() ;
		
		PageFlip page ;
		try{
			page = tran.page(se) ;
		}finally{
			tran.close() ;
		}
		
		//由于Tag是在jsp中调用的，有可能Request是include或者forward的新request；我们优先选择原始的request。
		HttpServletRequest orginal = (HttpServletRequest) request.getAttribute(GuzzWebApplicationContextUtil.GUZZ_ORGINAL_HTTP_REQUEST) ;
		
		if(orginal == null){
			orginal = request ;
		}
		
		page.setFlipURL(orginal, pageNoParamName) ;
		page.setPagesShow(pageSpan) ;
		
		if(this.pageBeforeSpan > 0){
			page.setPageBeforeSpan(this.pageBeforeSpan) ;
		}
		
		if(this.pageAfterSpan > 0){
			page.setPageAfterSpan(this.pageAfterSpan) ;
		}
		
		return page ;
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

	public String getPageNoParamName() {
		return pageNoParamName;
	}

	public void setPageNoParamName(String pageNoParamName) {
		this.pageNoParamName = pageNoParamName;
	}

	public int getPageSpan() {
		return pageSpan;
	}

	public void setPageSpan(int pageSpan) {
		this.pageSpan = pageSpan;
	}

	public int getPageBeforeSpan() {
		return pageBeforeSpan;
	}

	public void setPageBeforeSpan(int pageBeforePage) {
		this.pageBeforeSpan = pageBeforePage;
	}

	public int getPageAfterSpan() {
		return pageAfterSpan;
	}

	public void setPageAfterSpan(int pageAfterPage) {
		this.pageAfterSpan = pageAfterPage;
	}

	public int getSkipCount() {
		return skipCount;
	}

	public void setSkipCount(int skipCount) {
		this.skipCount = skipCount;
	}
	
}
