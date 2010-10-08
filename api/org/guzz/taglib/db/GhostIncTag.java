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

import java.io.Serializable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;

import org.guzz.GuzzContext;
import org.guzz.Service;
import org.guzz.service.core.SlowUpdateService;
import org.guzz.web.context.GuzzWebApplicationContextUtil;


/**
 * 
 * 采用jdbc方式对某一字段进行update递增操作。例如：增加文章阅读次数，blog访问次数等。
 * 
 * <p>一般情况下，计数的更新是有延迟的，只能应用在对延迟要求不高但对性能要求较高的需求。</p>
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class GhostIncTag extends TagSupport implements TryCatchFinally{
	
	private Object business ;
	
	private int count = 1 ;
	
	private String updatePropName ;
		
	private Serializable pkValue ;
	
	protected GuzzContext guzzContext ;
	
	protected SlowUpdateService slowUpdateService ;
	
	private Object tableCondition ;
	
	public int doStartTag() throws JspException {
		String ghostName ;
		
		if(this.business instanceof java.lang.String){
			ghostName = (String) this.business ;
		}else{
			ghostName = business.getClass().getName() ;
		}
		
		if(this.slowUpdateService == null){
			throw new JspException("slowUpdateService is not available.") ;
		}
		
//		POJOBasedObjectMapping mapping = (POJOBasedObjectMapping) guzzContext.getObjectMappingManager().getObjectMappingByName(ghostName) ;
//		
//		if(mapping == null){
//			throw new JspException("unknown business:[" + business + "], guessed business name:[" + ghostName + "]") ;
//		}
//		
//		String columnToUpdate = mapping.getColNameByPropName(updatePropName) ;
//		
//		if(columnToUpdate == null){
//			throw new JspException("unknown property:[" + updatePropName + "], business name:[" + ghostName + "]") ;
//		}
		
		this.slowUpdateService.updateCount(ghostName, getTableCondition(), updatePropName, pkValue, count) ;
		
//		this.slowUpdateService.updateCount(mapping.getTable(), columnToUpdate, pkValue, count) ;
		
		return SKIP_BODY;
	}

	public void doCatch(Throwable t) throws Throwable {
		throw t ;
	}

	public void doFinally() {
		this.business = null;		
		this.count = 1 ;		
		this.updatePropName = null;
		this.pkValue = null ;
		this.tableCondition = null ;
	}
	
	public String getUpdatePropName() {
		return updatePropName;
	}

	public void setUpdatePropName(String fieldName) {
		this.updatePropName = fieldName;
	}

	public Object getBusiness() {
		return business;
	}

	public void setBusiness(Object ghost) {
		this.business = ghost;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int step) {
		this.count = step;
	}

	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);
		if(this.slowUpdateService == null){
			this.guzzContext = GuzzWebApplicationContextUtil.getGuzzContext(pageContext.getServletContext()) ;
			this.slowUpdateService = (SlowUpdateService) this.guzzContext.getService(Service.FAMOUSE_SERVICE.SLOW_UPDATE) ;
		}
	}

	public Serializable getPkValue() {
		return pkValue;
	}

	public void setPkValue(String pkValue) {
		this.pkValue = pkValue;
	}

	public Object getTableCondition() {
		if(tableCondition != null){
			return tableCondition ;
		}else{
			GhostBoundaryTag m_parent = (GhostBoundaryTag) findAncestorWithClass(this, GhostBoundaryTag.class) ;
			if(m_parent != null){
				return m_parent.getTableCondition() ;
			}
		}
		
		return null;
	}

	public void setTableCondition(Object tableCondition) {
		this.tableCondition = tableCondition;
	}
	
}
