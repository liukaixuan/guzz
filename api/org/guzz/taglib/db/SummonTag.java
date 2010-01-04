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
import java.util.LinkedList;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.guzz.GuzzContext;
import org.guzz.orm.Business;
import org.guzz.orm.BusinessInterpreter;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.taglib.util.TagSupportUtil;
import org.guzz.web.context.GuzzWebApplicationContextUtil;

/**
 * 召唤灵魂。此Tag按照条件实际的加载对象到var参数中。
 * 
 * @author liu kaixuan
 */
public abstract class SummonTag extends TagSupport {
	
	private String var ;
	
	/** 
	 * 要加载的灵魂实体名称，如blog_article, boke_media等。
	 * 也可以时类的class名称。Ghost框架先按照灵魂的({@link Business})的name进行查找，如果查找不到，按照ghost为className加载class。
	 * TODO: 以后可以考虑加入objectName的支持。
	 */
	private Object business ;
	
	private Object limit ;
	
	private String scope ;
	
	private Object summonedData ;
	
	protected GuzzContext guzzContext ;
	
	public SummonTag(){
		super() ;
		init() ;
	}
	
	public void release() {
		super.release();
		
		init() ;
	}
	
	protected void init(){
		this.var = null ;
		this.scope = null ;
		this.limit = null ;
		this.business = null ;
		this.summonedData = null ;
	}

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		
		init() ;
		
		if(guzzContext == null){
			guzzContext = GuzzWebApplicationContextUtil.getGuzzContext(pc.getServletContext()) ;
		}
	}
	
	public int doStartTag() throws JspException {
		List limits = getBoundaryLimits() ;
		
		String ghostName ;
		
		if(this.business instanceof java.lang.String){
			ghostName = (String) this.business ;
		}else{
			ghostName = business.getClass().getName() ;
		}
		
		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) guzzContext.getObjectMappingManager().getObjectMappingByName(ghostName) ;
		
		if(mapping == null){
			throw new JspException("unknown business:[" + business + "], guessed business name:[" + ghostName + "]") ;
		}				
				
		Object result = null;
		
		try {
			result = innerSummonGhosts(mapping, limits);
		} catch (IOException e) {
			throw new JspException("business:[" + business + "], guessed business name:[" + ghostName + "]", e) ;
		}
		
		//保存结果
		this.summonedData = result ;
		
		if(var != null){ //如果设置了保存变量则进行保存
			if(result == null){//标记没有读取到纪录，同时删除scope中保存的同名值。
				this.pageContext.removeAttribute(var, TagSupportUtil.getScopeInCode(scope)) ;
			}else{
				this.pageContext.setAttribute(var, result, TagSupportUtil.getScopeInCode(scope)) ;
			}
		}
		
		return EVAL_BODY_INCLUDE;
	}	
	
	public int doEndTag() throws JspException {
		init() ;
		return super.doEndTag();
	}

	/**
	 * @param conditons jsp页面上传入的所有原始条件
	 * @param ghostClass 要查询的对象
	 * @param ghostSpoesman ghost参数解析器
	 * @return 加载的对象
	 **/
	protected Object innerSummonGhosts(POJOBasedObjectMapping mapping, List conditions) throws JspException, IOException {
		
		LinkedList list = new LinkedList() ;
		
		Business business = mapping.getBusiness() ;
		BusinessInterpreter gi = business.getInterpret() ;
		
		if(conditions != null && !conditions.isEmpty()){
			for(int i = 0 ; i < conditions.size() ; i++){
				Object condition = conditions.get(i) ;
				
				try {
					if(condition != null){
						Object mc = gi.explainCondition(condition) ;
						if(mc != null){
							list.addLast(mc) ;
						}
					}
				} catch (Exception e) {
					throw new JspException("conditions:" + conditions, e) ;
				}
			}
		}
		
		//TODO：增加tag的<c:param>支持。并且将c:param的参数作为MapParameter传递出去。
		
		return summonGhosts(business, list) ;
	}
	
	protected abstract Object summonGhosts(Business business, List conditions) throws JspException, IOException ;
	
	protected List getBoundaryLimits(){
		LinkedList m_limits = new LinkedList() ;
		
		GhostBoundaryTag m_parent = (GhostBoundaryTag) findAncestorWithClass(this, GhostBoundaryTag.class) ;
		if(m_parent != null){ //有更高层的关系。
			List m_parentBoundaryLimits = m_parent.getBoundaryLimits() ;
			if(m_parentBoundaryLimits != null){
				m_limits.addAll(m_parentBoundaryLimits) ;
			}
		}
		
//		//接收来自配置或者Action, filter的强制条件。
//		List forceConditions = (List) pageContext.getRequest().getAttribute(GhostWebContext.REQUEST_GHOST_TEMPLATE_FORCE_CONDTION) ;
//		if(forceConditions != null){
//			parentLimits.addAll(forceConditions) ;
//		}
		
		if(limit != null){
			m_limits.addFirst(limit) ;
		}
		
		return m_limits ;
	}

	public Object getLimit() {
		return limit;
	}

	public void setLimit(Object limit) throws JspException {
		if(limit == null){
			throw new JspException("you cann't add a null limit condition in business tag.") ;
		}
		
		this.limit = limit;
	}
	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public Object getBusiness() {
		return business;
	}

	public void setBusiness(Object ghost) {
		this.business = ghost;
	}

	public Object getSummonedData() {
		return summonedData;
	}

}
