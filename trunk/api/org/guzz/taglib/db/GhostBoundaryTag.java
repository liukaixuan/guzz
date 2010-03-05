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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.taglib.util.TagSupportUtil;

/**
 * 用于定义一些公共的限制条件，规划Ghost应该处于的范围。
 * 
 * @author liu kaixuan
 */
public class GhostBoundaryTag extends TagSupport {
	protected final transient Log log = LogFactory.getLog(this.getClass()) ;
		
	/**用户可以选择把所有的条件保存一个变量里面。*/
	private String var ;
	
	private String scope ;
	
	/**是否继承parent标签的限制条件*/
	private boolean inherit = true ;
	
	private List additionConditions ;
	
	private Object tableCondition ;
	
	private GhostBoundaryTag parent ;
	
	public GhostBoundaryTag(){
		super() ;
	}
	
	public void release() {
		super.release();
	}
	
	protected void resetToDefault(){
		this.var = null ;
		this.scope = null ;
		this.inherit = true ;
		this.additionConditions = null ;
		this.tableCondition = null ;
		this.parent = null ;
	}

	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);
		resetToDefault() ;
	}
				
	public int doStartTag() throws JspException {
		if(var != null){
			if(scope != null){
				this.pageContext.setAttribute(var, getBoundaryLimits(), TagSupportUtil.getScopeInCode(scope)) ;
			}else{
				this.pageContext.setAttribute(var, getBoundaryLimits()) ;
			}
		}
		
		return EVAL_BODY_INCLUDE;
	}
	
	public List getBoundaryLimits(){
		LinkedList m_limits = new LinkedList() ;
		
		if(inherit){
			GhostBoundaryTag m_parent = getParentBoundary() ;
			if(m_parent != null){ //有更高层的关系。
				m_limits.addAll(m_parent.getBoundaryLimits()) ;
			}
		}
		
		if(additionConditions != null){
			m_limits.addAll(this.additionConditions) ;
		}
		
		return m_limits ;
	}
	
	public void addLimitCondition(Object condition) throws JspException{
		if(condition == null){
			throw new JspException("you cann't add a null limit condition to <g:boundary> tag.") ;
		}
		
		if(additionConditions == null){
			additionConditions = new LinkedList() ;
		}
		
		additionConditions.add(condition) ;
	}
	
	protected GhostBoundaryTag getParentBoundary(){
		if(parent == null){
			parent = (GhostBoundaryTag) findAncestorWithClass(this, GhostBoundaryTag.class) ;
		}
		
		return parent ;
	}

	public boolean isInherit() {
		return inherit;
	}

	public void setInherit(boolean inherit) {
		this.inherit = inherit;
	}

	public Object getLimit() {
		return null;
	}

	public void setLimit(Object limit) throws JspException {		
		this.addLimitCondition(limit) ;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public Object getTableCondition() {
		if(this.tableCondition !=  null){
			return tableCondition;
		}else{
			GhostBoundaryTag m_parent = getParentBoundary() ;
			if(m_parent != null){
				return m_parent.getTableCondition() ;
			}
		}
		
		return null ;
	}

	public void setTableCondition(Object tableCondition) {
		this.tableCondition = tableCondition;
	}

}
