package org.guzz.asm;

import java.lang.reflect.Method;

import junit.framework.TestCase;

import org.guzz.pojo.DynamicUpdatable;
import org.guzz.test.Article;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class EnchancerTest extends TestCase{
	
	public void testMe(){
		Article a = (Article) Enhancer.create(Article.class, new Class[]{DynamicUpdatable.class}, null, new Callback[]{
			new MethodInterceptor(){
				public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
					proxy.invokeSuper(obj, args) ;
					
					System.out.println(method.getName()) ;
					
					return null;
				}
			}
		}) ;
		
		assertTrue(a instanceof DynamicUpdatable) ;
		a.getContent() ;
	}

}
