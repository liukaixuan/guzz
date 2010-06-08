/**
 * InvokeServiceInstanceTag.java created by liu kaixuan(liukaixuan@gmail.com) at 3:56:17 PM on Apr 9, 2008 
 */
package org.guzz.taglib.module;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.tagext.TryCatchFinally;


/**
 * 通过反射调用执行方法。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date Apr 9, 2008 3:56:17 PM
 */
public class InvokeServiceInstanceTag extends TagSupport implements TryCatchFinally, ObjectParamSupport{
//		
//	/**ordered method parameters*/
//	private LinkedList<Object> params = new LinkedList<Object>() ;
//	
//	private LinkedList<Class> paramClasses = new LinkedList<Class>() ;
//	
//	/**must parameter*/
//	private Object service ;
//	
//	/**must parameter*/
//	private String method ;
//	
//	private String var ;
//	
//	private String scope ;
//	
//	/**是否同步调用，默认为自动选择，由服务实现决定。TODO:此参数暂不提供支持！*/
//	private String sync ;
//	
//	private String comment ;
//	
//	private PageContext pageContext ;
//	
//	protected void init(){
//		this.params.clear() ;
//		this.paramClasses.clear() ;
//		
//		this.service = null ;
//		this.method = null ;
//		this.var = null ;
//		this.scope = null ;
//		this.sync = null ;
//	}
//	
//	@Override
//	public int doStartTag() throws JspException {
//		return EVAL_BODY_INCLUDE ;
//	}
//
//	@Override
//	public int doEndTag() throws JspException {
//		Class cls = service.getClass() ;
//		
//		try {
//			Method m = cls.getMethod(method, (Class[]) paramClasses.toArray(new Class[0])) ;
//			
//			//TODO: 增加sync参数的支持。
//			Object returnValue = m.invoke(service, (Object[]) params.toArray(new Object[0])) ;
//			
//			if(var != null){
//				pageContext.setAttribute(var, returnValue,  TagSupportUtil.getScopeInCode(this.scope)) ;
//			}
//			
//		} catch (SecurityException e) {
//			throw new JspException("invoke service[" + service + "]'s method[" + method + "] with params failed. param is:[" + params + "]", e) ;
//		} catch (NoSuchMethodException e) {
//			throw new JspException("invoke service[" + service + "]'s method[" + method + "] with params failed. param is:[" + params + "]", e) ;
//		}catch (IllegalArgumentException e) {
//			throw new JspException("invoke service[" + service + "]'s method[" + method + "] with params failed. param is:[" + params + "]", e) ;
//		} catch (IllegalAccessException e) {
//			throw new JspException("invoke service[" + service + "]'s method[" + method + "] with params failed. param is:[" + params + "]", e) ;
//		} catch (InvocationTargetException e) {
//			throw new JspException("invoke service[" + service + "]'s method[" + method + "] with params failed. param is:[" + params + "]", e) ;
//		}
//		
//		super.doEndTag();
//		
//		return SKIP_BODY ;
//	}
//	
//	@Override
//	public void setPageContext(PageContext pageContext) {
//		super.setPageContext(pageContext);
//		this.pageContext = pageContext ;
//	}
//
//	public void doCatch(Throwable t) throws Throwable {
//		throw t ;
//	}
//
//	public void doFinally() {
//		init() ;
//	}
//
//	public void addParameter(String type, Object value) {
//		if(value instanceof String){
//			if(type != null){
//				IDataTypeHandler dh = null ;
//				
//				//TODO: 读取具体的域对象处理类解析。
//				dh = AbstractGhostSpokesman.COMMON_DATA_TYPE_HANDLERS.get(type) ;
//				
//				Assert.assertNotNull(dh, "类型:[" + type + "]无法解析！") ;
//				
//				value = dh.getValue((String) value) ;
//			}
//		}
//		
//		params.addLast(value) ;
//		
//		Class basicType = TagSupportUtil.getPrimaryType(type) ;
//		basicType = basicType == null ? value.getClass() : basicType ;
//		
//		paramClasses.addLast(basicType) ;
//	}
//
//	public Object getService() {
//		return service;
//	}
//
//	public void setService(Object service) {
//		this.service = service;
//	}
//
//	public String getMethod() {
//		return method;
//	}
//
//	public void setMethod(String method) {
//		this.method = method;
//	}
//
//	public String getVar() {
//		return var;
//	}
//
//	public void setVar(String var) {
//		this.var = var;
//	}
//
//	public String getScope() {
//		return scope;
//	}
//
//	public void setScope(String scope) {
//		this.scope = scope;
//	}
//
//	public String getComment() {
//		return comment;
//	}
//
//	public void setComment(String comment) {
//		this.comment = comment;
//	}

}
