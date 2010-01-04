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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.guzz.GuzzContext;
import org.guzz.orm.mapping.POJOBasedObjectMapping;
import org.guzz.util.javabean.BeanCreator;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.web.context.GuzzWebApplicationContextUtil;

/**
 * 对Ghost进行写操作。
 */
public abstract class RestGhostTag extends TagSupport {
	protected final transient Log log = LogFactory.getLog(this.getClass()) ;
	
	private String operation ;
		
	//business name or business object. MUST variable.
	private Object business ;
		
	private Object ghostObject ;
	
	protected GuzzContext guzzContext ;
	
	protected BeanWrapper ghostWrapper ;
		
	public RestGhostTag(){
		super() ;
	}
	
	public void release() {
		super.release();
	}
	
	protected void resetToDefault(){
		this.operation = null ;
		this.business = null ;
		this.ghostObject = null ;
		this.ghostWrapper = null ;
	}
	
	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		
		resetToDefault() ;
		
		if(guzzContext == null){
			this.guzzContext = GuzzWebApplicationContextUtil.getGuzzContext(pc.getServletContext()) ;
		}
	}
		
	/**设置Ghost Object的属性值*/
	public void setProperty(String name, Object value){
		ghostWrapper.setValue(this.ghostObject, name, value) ;
	}

	public int doStartTag() throws JspException {
		this.ghostObject = getRestedGhostObject() ;
		
		return EVAL_BODY_INCLUDE;
	}
	
	/**返回要操作的ghost对象*/
	protected Object getRestedGhostObject() throws JspException {
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
		
		this.ghostWrapper = mapping.getBeanWrapper() ;
		
		Object domainObject = BeanCreator.newBeanInstance(mapping.getBusiness().getDomainClass()) ;		
		return domainObject ;
	}
	
	/**
	 * 执行实际的操作，如添加，修改，删除操作到数据库中
	 * 
	 * <p>此时各项成员变量都是准备的好的，可以直接使用。</p>
	 * 
	 * */
	protected abstract void internalRestGhost(Object ghostObject) ;
		

	public int doEndTag() throws JspException {
		
		internalRestGhost(this.ghostObject) ;
		
		resetToDefault() ;
		
		return super.doEndTag();
	}

	public Object getBusiness() {
		return business;
	}

	public void setBusiness(Object ghost) {
		this.business = ghost;
	}

	public String getOp() {
		return operation;
	}

	public void setOp(String op) {
		this.operation = op;
	}

	public String getOperation() {
		return operation;
	}
	
}
