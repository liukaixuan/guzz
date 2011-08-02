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
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 设置property
 * 
 * @author liu kaixuan
 */
public class SetPropertyTag extends TagSupport {

	private String name ;
	
	private Object value ;
	
	public void release() {
		super.release();
		
		this.name = null ;
		this.value = null ;
	}
	
	public int doStartTag() throws JspException {
		RestGhostTag tag = (RestGhostTag) findAncestorWithClass(this, RestGhostTag.class) ;
		
		if(tag == null){
			throw new JspException("<g:set> must be used in g:add, g:update or g:delete.") ;
		}
		
		tag.setProperty(name, value) ;
		
		return SKIP_BODY ;
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

	
	
}
