/**
 * ObjectParamSupport.java created by liu kaixuan(liukaixuan@gmail.com) at 10:07:32 AM on Apr 9, 2008 
 */
package org.guzz.taglib.module;

/**
 * 
 * 类似于c:param，不过提供对value是Object的支持。c:param的value只能传入String类型。
 * 
 * @author liu kaixuan(liukaixuan@gmail.com)
 * @date Apr 9, 2008 10:07:32 AM
 */
public interface ObjectParamSupport {
	
	public void addParameter(String type, Object value) ;

}
