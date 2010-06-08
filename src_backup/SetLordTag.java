/**
 * 
 */
package org.guzz.taglib.db;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * 设置一个Lord对象
 * 
 * @author liu kaixuan
 * @date 2007-5-28 下午07:28:50
 */
public class SetLordTag extends TagSupport {
	
	private Object value ;
	
	@Override
	public void release() {
		super.release();
		
		this.value = null ;
	}
	
	@Override
	public int doStartTag() throws JspException {
		RestGhostTag tag = (RestGhostTag) findAncestorWithClass(this, RestGhostTag.class) ;
		
		if(tag == null){
			throw new JspException("<g:setLord> must be used in g:add, g:update or g:delete.") ;
		}
		
		tag.setLordProperty(value) ;
		
		return SKIP_BODY ;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

}