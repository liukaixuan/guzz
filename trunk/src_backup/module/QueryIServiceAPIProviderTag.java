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
public class QueryIServiceAPIProviderTag extends QuerySupport  implements TryCatchFinally{
//	
//	private String moduleName ;
//	
//	private String serviceName ;
//	
//	/**must parameter*/
//	private String ghost ;
//	
//	/**must parameter. 是否直接使用db service.*/
//	private String lookForDBService ;
//	
//	private IModule module ;
//	
//	private IService service ;
//	
//	@Override
//	public int doStartTag() throws JspException {
//		 super.doStartTag();
//		 
//		 Assert.assertNotNull(ghost, "ghost参数不存在！") ;
//		 
//		 IServiceAPIProvider api = null ;
//		 
//		 if(TagSupportUtil.isYes(lookForDBService, false)){
//			 api = (IServiceAPIProvider) GhostManager.getInstance().getDBServiceManager(ghost) ;
//		 }else if(service == null){
//				Assert.assertFalse(module == null && moduleName == null, "moduleName，module参数必须指定一个或者指定service参数！") ;
//				
//				if(this.module == null){
//					this.module = this.moduleFactory.getModule(moduleName) ;
//				}
//				
//				Assert.assertNotNull(module, "module不存在！") ;
//				
//				this.service = module.getService(serviceName) ;
//				
//				if(service == null){
//					throw new NoSuchServiceException(this.onFail, serviceName) ;
//				}
//		 }else{
//			 api = service.getServiceProvider(ghost) ;
//		 }
//		
//		if(api == null){
//			throw new NoSuchServiceException(this.onFail, service.getServiceName() + " with ghost:" + ghost) ;
//		}
//		
//		pageContext.setAttribute(this.var, api, TagSupportUtil.getScopeInCode(this.scope)) ;
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
//		this.service = null ;
//		this.ghost = this.lookForDBService = null ;
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
//
//	public String getGhost() {
//		return ghost;
//	}
//
//	public void setGhost(String ghostName) {
//		this.ghost = ghostName;
//	}
//
//	public IService getService() {
//		return service;
//	}
//
//	public void setService(IService service) {
//		this.service = service;
//	}
//
//	public String getLookForDBService() {
//		return lookForDBService;
//	}
//
//	public void setLookForDBService(String lookForDBService) {
//		this.lookForDBService = lookForDBService;
//	}

}
