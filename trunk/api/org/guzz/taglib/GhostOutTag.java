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
package org.guzz.taglib;

import java.io.IOException;
import java.io.Reader;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.guzz.taglib.util.TagSupportUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 */
public class GhostOutTag extends BodyTagSupport {
	
	protected Object value; // tag attribute
	protected String def; // tag attribute
	
	protected boolean escapeXml; // tag attribute
	protected boolean escapeScriptCode = true ; //去除javascript代码
	protected boolean markFilterWord ; //是否标红过滤词
	protected String filterWordGroupName ; //过滤词组，默认为所有。
		
	protected boolean escapeForScriptOutput ; //为js输出做过滤。
	
	protected boolean escapeForXslt ; //为xslt输出内容进行过滤
	
	protected boolean native2ascii ; //将字符按照/uxxx的unicode代码输出
		
	private boolean needBody; // non-space body needed?
	
	// *********************************************************************
	
	public GhostOutTag() {
		super();
		init();
	}

	// resets local state
	private void init() {
		value = def = null;
		escapeXml = true;
		needBody = false;
		
		escapeScriptCode = true ;
		markFilterWord = false ;
		filterWordGroupName = null ;
		escapeForScriptOutput = false ;
		escapeForXslt = false ;
		native2ascii = false ;
	}

	// Releases any resources we may have (or inherit)
	public void release() {
		super.release();
		init();
	}

	// *********************************************************************
	// Tag logic

	// evaluates 'value' and determines if the body should be evaluted
	public int doStartTag() throws JspException {

		needBody = false; // reset state related to 'default'
		this.bodyContent = null; // clean-up body (just in case container is pooling tag handlers)

		try {
			// print value if available; otherwise, try 'default'
			if (value != null) {
				out(pageContext, escapeXml, value);
				return SKIP_BODY;
			} else {
				// if we don't have a 'default' attribute, just go to the body
				if (def == null) {
					needBody = true;
					return EVAL_BODY_BUFFERED;
				}

				// if we do have 'default', print it
				if (def != null) {
					// good 'default'
					out(pageContext, escapeXml, def);
				}
				return SKIP_BODY;
			}
		} catch (IOException ex) {
			throw new JspException(ex.toString(), ex);
		}
	}

	// prints the body if necessary; reports errors
	public int doEndTag() throws JspException {
		try {
			if (!needBody) return EVAL_PAGE; // nothing more to do

			// trim and print out the body
			if (bodyContent != null && bodyContent.getString() != null) out(pageContext, escapeXml, bodyContent.getString().trim());
			return EVAL_PAGE;
		} catch (IOException ex) {
			throw new JspException(ex.toString(), ex);
		}
	}

	// *********************************************************************
	// Public utility methods

	/**
	 * Outputs <tt>text</tt> to <tt>pageContext</tt>'s current JspWriter. If <tt>escapeXml</tt> is true, performs
	 * the following substring replacements (to facilitate output to XML/HTML pages):
	 *  & -> &amp; < -> &lt; > -> &gt; " -> &#034; ' -> &#039;
	 * 
	 */
	public void out(PageContext pageContext, boolean escapeXml, Object obj) throws IOException {
		JspWriter w = pageContext.getOut();
		
		String content = null ;
		StringBuffer sb = new StringBuffer(64) ;
		
		if (obj instanceof Reader) {
			Reader reader = (Reader) obj;
			char[] buf = new char[4096];
			int count;
			while ((count = reader.read(buf, 0, 4096)) != -1) {
				sb.append(buf, 0, count) ;
			}
			
			sb.toString() ;
		}else{
			sb = new StringBuffer(obj.toString()) ;
		}
		
		if(escapeXml){
			content = TagSupportUtil.escapeXml(sb.toString()) ;
			
//			StringBuffer sb2 = new StringBuffer((int) (sb.length() * 1.2)) ;
//			
//			for (int i = 0; i < sb.length(); i++) {
//				char c = sb.charAt(i) ;
//				if (c <= TagSupportUtil.HIGHEST_SPECIAL) {
//					char[] escaped = TagSupportUtil.specialCharactersRepresentation[c];
//					if (escaped != null) {
//						sb2.append(escaped) ;
//					}else{
//						sb2.append(c) ;
//					}
//				}else{
//					sb2.append(c) ;
//				}
//			}
//
//			content = sb2.toString() ;
			
		}else if(escapeScriptCode){
			content = sb.toString() ;
			content = StringUtil.replaceStringIgnoreCase(content, "<script", "< script") ;
			content = StringUtil.replaceStringIgnoreCase(content, "</script", "</ script") ;
		}else{
			content = sb.toString() ;
		}
		
		if(escapeForScriptOutput){ //删除" ' script标记
			content = StringUtil.js_string(sb.toString()) ;
		}
		
		if(escapeForXslt){
			content = StringUtil.getXSLTFreeText(content) ;
		}
		
		if(native2ascii){
			content = StringUtil.native2ascii(content) ;
		}
		
		//输出。
		if(content != null){
			w.write(content) ;
		}
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		if(value instanceof String){
			if(StringUtil.isEmpty((String) value)){
				value = null ;
			}
		}
		
		this.value = value;
	}

	public String getDef() {
		return def;
	}

	public void setDef(String def) {
		this.def = def;
	}

	public boolean isEscapeXml() {
		return escapeXml;
	}

	public void setEscapeXml(boolean escapeXml) {
		this.escapeXml = escapeXml;
	}

	public boolean isEscapeScriptCode() {
		return escapeScriptCode;
	}

	public void setEscapeScriptCode(boolean escapeScriptCode) {
		this.escapeScriptCode = escapeScriptCode;
	}

	public boolean isMarkFilterWord() {
		return markFilterWord;
	}

	public void setMarkFilterWord(boolean markFilterWord) {
		this.markFilterWord = markFilterWord;
	}

	public String getFilterWordGroupName() {
		return filterWordGroupName;
	}

	public void setFilterWordGroupName(String filterWordGroupName) {
		this.filterWordGroupName = filterWordGroupName;
	}

	public boolean isEscapeForScriptOutput() {
		return escapeForScriptOutput;
	}

	public void setEscapeForScriptOutput(boolean escapeForScriptOutput) {
		this.escapeForScriptOutput = escapeForScriptOutput;
	}

	public boolean isNeedBody() {
		return needBody;
	}

	public void setNeedBody(boolean needBody) {
		this.needBody = needBody;
	}
	      
    // for tag attribute
    public void setDefault(String def) {
        this.def = def;
    }

	public boolean isEscapeForXslt() {
		return escapeForXslt;
	}

	public void setEscapeForXslt(boolean escapeForXslt) {
		this.escapeForXslt = escapeForXslt;
	}

	public boolean isNative2ascii() {
		return native2ascii;
	}

	public void setNative2ascii(boolean native2ascii) {
		this.native2ascii = native2ascii;
	}
	
}
