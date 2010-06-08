/**
 * GhostHiddenInputTag.java created by liu kaixuan(liukaixuan@gmail.com) at 9:32:23 AM on Apr 15, 2008 
 */
package org.guzz.taglib;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.guzz.util.ArrayUtil;
import org.guzz.util.Assert;
import org.guzz.util.RequestUtil;
import org.guzz.util.StringUtil;


/**
 * 
 * 将所有请求的传入的参数按照需要转换成<input type="hidden">标签。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date Apr 15, 2008 9:32:23 AM
 */
public class GhostHiddenInputTag extends TagSupport {
		
	private String excludeParams ;
	
	private String onlyParams ;
	
	private boolean isExclude ;
	
	private boolean isOnly ;
	
	protected void init(){
		excludeParams = null ;
		onlyParams = null ;
		this.isExclude = false ;
		this.isOnly = false ;
	}	

	public int doStartTag() throws JspException {
		super.doStartTag() ;
		
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest() ;
		String[] params = null ;
		
		if(isOnly){
			Assert.assertNotNull(onlyParams, "onlyParams如果指定，则不允许为空！") ;
			params = StringUtil.splitString(onlyParams, ",") ;
		}else if(isExclude){
			Assert.assertNotNull(excludeParams, "excludeParams如果指定，则不允许为空！") ;
			params = StringUtil.splitString(excludeParams, ",") ;
		}
		
		if(params != null){
			for(int i = 0 ; i < params.length ; i++){
				params[i] = params[i].trim() ;
			}
		}
		
		StringBuffer sb = new StringBuffer(256) ;
		
		if(isOnly){
			for(int i = 0 ; i < params.length ; i++){
				String key = params[i] ;
				String value = RequestUtil.getParameter(request, key) ;
				sb.append("<input type=\"hidden\" name=\"").append(key).append ("\" value=\"").append(value).append("\" /> ") ;
			}
		}else{
			Enumeration e = request.getParameterNames() ;
			
			while(e.hasMoreElements()){
				String key = (String) e.nextElement() ;
				
				if(isExclude &&  ArrayUtil.inArray(params, key)){
					continue ;
				}
				
				String value = RequestUtil.getParameter(request, key) ;
				sb.append("<input type=\"hidden\" name=\"").append(key).append ("\" value=\"").append(value).append("\" />") ;
			}
		}

		JspWriter w = pageContext.getOut() ;
		
		try {
			w.write(sb.toString()) ;
		} catch (IOException e) {
			throw new JspException(e) ;
		}
		
		return SKIP_BODY ;
	}	

	public String getExcludeParams() {
		return excludeParams;
	}

	public void setExcludeParams(String excludeParams) {
		this.excludeParams = excludeParams;
		isExclude = true ;
	}

	public String getOnlyParams() {
		return onlyParams;
	}

	public void setOnlyParams(String onlyParams) {
		this.onlyParams = onlyParams;
		isOnly = true ;
	}
	
}
