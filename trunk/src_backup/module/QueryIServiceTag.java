/**
 * QueryIServiceTag.java created by liu kaixuan(liukaixuan@gmail.com) at 10:15:40 AM on Apr 9, 2008 
 */
package org.guzz.taglib.module;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TryCatchFinally;


/**
 * 
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date Apr 9, 2008 10:15:40 AM
 */
public class QueryIServiceTag extends QuerySupport  implements TryCatchFinally{
//	
//	private String moduleName ;
//	
//	/**must parameter*/
//	private String serviceName ;
//	
//	private IModule module ;
//	
//	@Override
//	public int doStartTag() throws JspException {
//		 super.doStartTag();
//		 			 
//		Assert.assertFalse(module == null && moduleName == null, "moduleName，module参数必须指定一个！") ;
//		
//		if(this.module == null){
//			this.module = this.moduleFactory.getModule(moduleName) ;
//		}
//		
//		Assert.assertNotNull(module, "module不存在！") ;
//		
//		IService service = module.getService(serviceName) ;
//		
//		if(service == null){
//			throw new NoSuchServiceException(this.onFail, serviceName) ;
//		}
//		
//		pageContext.setAttribute(this.var, service, TagSupportUtil.getScopeInCode(this.scope)) ;
//		
//		//标签中间不允许其他代码。
//		return SKIP_BODY ;
//	}
//
//	protected void init(){
//		super.init() ;
//		this.module = null ;
//		this.serviceName = null ;
//		this.module = null ;
//	}
//	
//	public String getModuleName() {
//		return moduleName;
//	}
//
//	public void setModuleName(String moduleName) {
//		this.moduleName = moduleName;
//	}
//
//	public String getServiceName() {
//		return serviceName;
//	}
//
//	public void setServiceName(String serviceName) {
//		this.serviceName = serviceName;
//	}
//
//	public IModule getModule() {
//		return module;
//	}
//
//	public void setModule(IModule module) {
//		this.module = module;
//	}
//
//	public void doCatch(Throwable arg0) throws Throwable {
//		throw arg0 ;
//	}
//
//	public void doFinally() {
//		init() ;
//	}

}
