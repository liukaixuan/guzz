/**
 * 
 */
package org.guzz.service;

import org.guzz.Service;

/**
 * 
 * Similar to <a href="http://static.springsource.org/spring/docs/2.5.x/api/org/springframework/beans/factory/FactoryBean.html">org.springframework.beans.factory.FactoryBean</a>
 * <p/>
 * Interface to be implemented by objects used within a FactoryService which are themselves factories. If a object implements this interface, it is used as a factory for an service to expose, not directly as a service instance that will be exposed itself.
 * <p/>FactoryServices only support singletons.
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public interface FactoryService {
	
	/**
	 * create and return the service singleton. Called on service registration.
	 */
	public Service createService() ;

}
