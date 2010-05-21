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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 
 * 为Boundary动态增加条件。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class GhostAddLimitTag extends TagSupport {
	
	//this is a must parameter, so we don't have to clean it after called.
	private Object limit ;
	
	private Object limit2 ;
	
	private Object limit3 ;
	
	private boolean test ;
	
	protected void resetToDefault(){
		this.limit3 = null ;
		this.limit2 = null ;
		this.test = true ;
	}
	
	// receives the tag's 'test' attribute
    public void setTest(boolean test) {
        this.test = test;
    }

	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);
		resetToDefault() ;
	}
	
	public int doStartTag() throws JspException {
		if(!test) return SKIP_BODY ;
		
		GhostBoundaryTag m_parent = (GhostBoundaryTag) findAncestorWithClass(this, GhostBoundaryTag.class) ;
		
		if(m_parent == null){
			throw new JspException("g:addLimit must be used between g:boundary tag!") ;
		}
		
		m_parent.addLimitCondition(limit) ;
		
		if(limit2 != null){
			m_parent.addLimitCondition(limit2) ;
		}
		
		if(limit3 != null){
			m_parent.addLimitCondition(limit3) ;
		}
		
		return SKIP_BODY;
	}

	public Object getLimit() {
		return limit;
	}

	public void setLimit(Object limit) throws JspException {
		if(limit == null){
			throw new JspException("you cann't add a null limit condition to <g:addLimit> tag.") ;
		}
		
		this.limit = limit;
	}

	public Object getLimit2() {
		return limit2;
	}

	public void setLimit2(Object limit2) throws JspException {
		if(limit == null){
			throw new JspException("you cann't add a null limit condition to <g:addLimit> tag.") ;
		}
		
		this.limit2 = limit2;
	}

	public Object getLimit3() {
		return limit3;
	}

	public void setLimit3(Object limit3) throws JspException {
		if(limit == null){
			throw new JspException("you cann't add a null limit condition to <g:addLimit> tag.") ;
		}
		
		this.limit3 = limit3;
	}

}
