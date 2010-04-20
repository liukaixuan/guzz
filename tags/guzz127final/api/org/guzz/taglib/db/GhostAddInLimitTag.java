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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.guzz.GuzzContext;
import org.guzz.orm.Business;
import org.guzz.orm.se.InTerm;
import org.guzz.util.javabean.BeanWrapper;
import org.guzz.web.context.GuzzWebApplicationContextUtil;

/**
 * 
 * 
 *
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class GhostAddInLimitTag extends TagSupport {
	
	private String name ;
	
	private Object value ;
	
	private String retrieveValueProp ;
	
	private GuzzContext guzzContext ;
	
	protected void resetToDefault(){
		this.retrieveValueProp = null ;
	}

	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);
		resetToDefault() ;
		
		if(this.guzzContext == null){
			this.guzzContext = GuzzWebApplicationContextUtil.getGuzzContext(pageContext.getServletContext()) ;
		}
	}
	
	public int doStartTag() throws JspException {
		GhostBoundaryTag m_parent = (GhostBoundaryTag) findAncestorWithClass(this, GhostBoundaryTag.class) ;
		
		if(m_parent == null){
			throw new JspException("g:addInLimit must be used between g:boundary tag!") ;
		}
		
		if(value instanceof int[]){
			m_parent.addLimitCondition(new InTerm(this.name, (int[]) value)) ;
			
			return SKIP_BODY;
		}
		
		List mvs = null ;
		
		if(value.getClass().isArray()){//是不是数组。
			mvs = Arrays.asList((Object[]) value) ;
		}else if(value instanceof List){
			mvs = (List) value ;
		}else{
			throw new JspException("value must be an array or a List.") ;
		}
		
		if(mvs.isEmpty()){
			throw new JspException("value can not be empty.") ;
		}
		
		if(retrieveValueProp == null){
			m_parent.addLimitCondition(new InTerm(this.name, mvs)) ;
		}else{
			LinkedList newValues = new LinkedList() ;
			Iterator i = mvs.iterator() ;
			Object valueItem = mvs.get(0) ;
			
			if(valueItem instanceof Map){
				while(i.hasNext()){
					newValues.addLast(((Map) i.next()).get(this.retrieveValueProp)) ;
				}
			}else{
				Class valueClass = mvs.get(0).getClass() ;
				
				BeanWrapper bw ;
				
				Business b = this.guzzContext.getBusiness(valueClass.getName()) ;
				if(b != null){//如果属于领域对象，使用领域对象的BeanWrapper(用以支持读取CustomTableView的特殊要求)
					bw = b.getBeanWrapper() ;
				}else{
					bw = BeanWrapper.createPOJOWrapper(valueClass) ;
				}
				
				while(i.hasNext()){
					newValues.addLast(bw.getValue(i.next(), this.retrieveValueProp)) ;
				}
			}
			
			m_parent.addLimitCondition(new InTerm(this.name, newValues)) ;
		}
		
		return SKIP_BODY;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getRetrieveValueProp() {
		return retrieveValueProp;
	}

	public void setRetrieveValueProp(String retrieveValueProp) {
		this.retrieveValueProp = retrieveValueProp;
	}
	
}
