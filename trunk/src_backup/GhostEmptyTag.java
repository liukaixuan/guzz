/**
 * 
 */
package org.guzz.taglib.db;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;


/**
 * 细览结果，如果结果为null则执行Tag body，否则跳过。
 * 
 * @author liu kaixuan
 * @date 2007-5-23 下午08:33:14
 */
public class GhostEmptyTag extends TagSupport {
	
	private Object data ;
	
	private boolean dataSetTag = false ;
	
	public GhostEmptyTag(){
		super() ;
		init() ;
	}
	
	protected void init(){
		this.data = null ;
		this.dataSetTag = false ;
	}
	
	public int doStartTag() throws JspException {
		
		if(!this.dataSetTag){
			SummonTag summonTag = (SummonTag) findAncestorWithClass(this, SummonTag.class) ;
			
			if(summonTag == null){
				throw new InvalidParameterException("<g:empty>标签前面必须有list, page或者load标签！否则请指定data属性。") ;
			}
			
			this.data = summonTag.getSummonedData() ;
		}
		
		if(TagSupportUtil.isLoadedDataEmpty(this.data)){
			return EVAL_BODY_INCLUDE;
			
		}else{
			return SKIP_BODY ;			
		}
	}
	
	

	@Override
	public int doEndTag() throws JspException {
		init() ;
		return super.doEndTag();
	}

	@Override
	public void release() {
		init() ;
		super.release();
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
		this.dataSetTag = true ;
	}
	
}

