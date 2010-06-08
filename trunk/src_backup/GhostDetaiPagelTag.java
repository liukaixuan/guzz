package org.guzz.taglib.db;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 细览结果，如果结果为null则跳过Tag body的执行。
 * 
 * @author liu kaixuan
 */
public class GhostDetaiPagelTag extends TagSupport {
	protected final Log logger = LogFactory.getLog(this.getClass()) ;
	
	private String var ;	
		
	private String scope ;
	
	private Object data ;
	
	private boolean dataSetTag = false ;
	
	private String operation ;
	
	public GhostDetaiPagelTag(){
		super() ;
		init() ;
	}
	
	protected void init(){
		this.var = null ;
		this.scope = null ;
		this.data = null ;
		this.dataSetTag = false ;
		this.operation = "detail" ;
	}
		
	public void release() {
		super.release();
		
		init() ;
	}

	@Override
	public void setPageContext(PageContext arg0) {
		super.setPageContext(arg0);
		
		if(commonPermissionChecker == null){
			commonPermissionChecker = GhostWebContext.getGhostManagerContext().getCommonPermissionChecker() ;
			objectLogger = GhostWebContext.getGhostManagerContext().getObjectLogger() ;
		}
		
	}

	public int doStartTag() throws JspException {
				
		if(!dataSetTag){
			SummonTag summonTag = (SummonTag) findAncestorWithClass(this, SummonTag.class) ;
			
			if(summonTag == null){
				throw new InvalidParameterException("<g:detail>标签前面必须有list, page或者load标签！否则请指定data属性。") ;
			}
			
			this.data = summonTag.getSummonedData() ;
		}
		
		if(TagSupportUtil.isLoadedDataEmpty(this.data)){
			return SKIP_BODY ;
		}else{
			
			//支持权限，验证权限
			if(TagSupportUtil.isYes(this.auth, false)){
				if(this.data instanceof IAuthLogObject){
					commonPermissionChecker.assertHasPermission((IAuthLogObject) this.data, this.operation, this.authFailMsg) ;
				}else{
					logger.warn("Auth opened, but ghost domain object doesn't implement IAuthLogObject. Business is:" + data) ;
				}
			}
			
			if(var != null){
				this.pageContext.setAttribute(var, this.data, TagSupportUtil.getScopeInCode(scope)) ;
			}
			
			return EVAL_BODY_INCLUDE;
		}
	}

	@Override
	public int doEndTag() throws JspException {
//		支持日志
		if(this.data != null && this.data.getClass().isAnnotationPresent(Ghost.class)){ //避免null或者是List
			if(TagSupportUtil.isYes(this.log, false)){
				if(this.data instanceof IAuthLogObject){
					objectLogger.log((IAuthLogObject) this.data, this.operation, this.logMsg) ;
				}else{
					logger.warn("Log opened, but ghost domain object doesn't implement IAuthLogObject. Business is:" + data) ;
				}
			}
		}
		init() ;
		return super.doEndTag();
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

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
		dataSetTag = true ;
	}

	public String getAuth() {
		return auth;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}

	public String getAuthFailMsg() {
		return authFailMsg;
	}

	public void setAuthFailMsg(String authFailMsg) {
		this.authFailMsg = authFailMsg;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getLogMsg() {
		return logMsg;
	}

	public void setLogMsg(String logMsg) {
		this.logMsg = logMsg;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getOp() {
		return operation;
	}

	public void setOp(String op) {
		this.operation = op;
	}
	
	

}
