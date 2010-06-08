/**
 * ObjectParamTag.java created by liu kaixuan(liukaixuan@gmail.com) at 4:33:18 PM on Apr 9, 2008 
 */
package org.guzz.taglib.module;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 设置Object参数。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date Apr 9, 2008 4:33:18 PM
 */
public class ObjectParamTag extends TagSupport {
	
	/**参数类型。*/
	private String type ;
	
	private Object value ;
	
	private String comment ;

	public int doStartTag() throws JspException {
		 super.doStartTag();
		
		ObjectParamSupport ops = (ObjectParamSupport) findAncestorWithClass(this, ObjectParamSupport.class) ;
		
		if(ops == null){
			throw new JspException("<g:param> must be used inside a ObjectParamSupport tag. eg:<g:invokeService> tags.") ;
		}
		
		ops.addParameter(type, value) ;
		
		return SKIP_BODY ;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

}
