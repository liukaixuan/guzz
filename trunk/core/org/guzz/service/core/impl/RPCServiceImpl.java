/**
 * 
 */
package org.guzz.service.core.impl;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import net.sf.cglib.core.ReflectUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.guzz.Service;
import org.guzz.exception.InvalidConfigurationException;
import org.guzz.service.AbstractRPCService;
import org.guzz.service.FactoryService;
import org.guzz.service.ServiceConfig;
import org.guzz.service.remote.RemoteRPCProxy;
import org.guzz.util.ClassUtil;
import org.guzz.util.StringUtil;

/**
 * 
 * A general purpose RPC client service implementation based on Cglib Proxy. 
 * 
 * @author liukaixuan(liukaixuan@gmail.com)
 */
public class RPCServiceImpl extends AbstractRPCService implements MethodInterceptor, FactoryService {
	
	private Object stub ;
	 
	private Service proxy ;
	
	private Map<String, Method> stubMethods = new HashMap<String, Method>() ;

	private Map<String, Method> thisMethods = new HashMap<String, Method>() ;

	public Service createService() {
		return proxy ;
	}
	
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		if(args.length == 2){
			String name = method.getName() ;
			if("invokeProxiedMethod".equals(name)){//pass the proxy
				MethodProxy mp = MethodProxy.find(obj.getClass(), ReflectUtils.getSignature((Member) args[0])) ;
				return mp.invokeSuper(obj, (Object[]) args[1]) ;
			}
		}
		
		String methodKey = this.getMethodKey(method) ;
		
		//call stub method
		Method stubMethod = this.stubMethods.get(methodKey) ;
		if(stubMethod != null){
			return stubMethod.invoke(stub, args) ;
		}
		
		//call this RPCServiceImpl's method
		Method thisMethod = this.thisMethods.get(methodKey) ;
		if(thisMethod != null){
			return thisMethod.invoke(this, args) ;
		}
		
		throw new NoSuchMethodException(methodKey) ;
	}
	
	protected String getMethodKey(Method m){
		StringBuilder sb = new StringBuilder();
		
		sb.append(m.getName()).append(' ') ;
		
	    Class[] params = m.getParameterTypes();
	    if (params.length > 0) {
			boolean first = true;
			sb.append("<");
			for(Class p: params) {
			    if (!first)
				sb.append(",");
			    sb.append(p.getName()) ;
			    first = false;
			}
			sb.append("> ");
	    }

	    Class retType = m.getReturnType();
	    sb.append(retType.getName());
	    
	    return sb.toString();
	}

    public boolean configure(ServiceConfig[] scs) {
    	if(scs.length == 0){
    		throw new InvalidConfigurationException("missing configurations.") ;
    	}
    	
    	String serviceInterface = (String) scs[0].getProps().remove(RemoteRPCProxy.RPC_PARAM_PREFIX + "serviceInterface") ;
    	if(StringUtil.isEmpty(serviceInterface)){        		
    		throw new InvalidConfigurationException("missing param: " + RemoteRPCProxy.RPC_PARAM_PREFIX + "serviceInterface") ;
    	}
    	
    	Class cls = ClassUtil.getClass(serviceInterface.trim()) ;
    	
    	Method[] ms = cls.getMethods() ;
    	
    	for(Method m : ms){
    		String key = this.getMethodKey(m) ;
    		    		
    		this.stubMethods.put(key, m) ;
    	}

    	ms = this.getClass().getMethods() ;
    	for(Method m : ms){
    		String key = this.getMethodKey(m) ;
    		
    		this.thisMethods.put(key, m) ;
    	}
    	        		
		Enhancer e = new Enhancer() ;
		
		//增加Service接口的方法，使得外部可以通过调用stub的Service方法，将操作转给当前的Service
		if(cls.isInterface()){
			if(Service.class.isAssignableFrom(cls)){
				e.setInterfaces(new Class[]{cls}) ;
			}else{
				e.setInterfaces(new Class[]{cls, Service.class}) ;
			}
		}else{
			if(Service.class.isAssignableFrom(cls)){
				e.setSuperclass(cls) ;
			}else{
				e.setSuperclass(cls) ;
				e.setInterfaces(new Class[]{Service.class}) ;
			}
		}
		
		e.setCallback(this) ;
		this.proxy = (Service) e.create() ;
		
    	if(super.configure(scs)){
            this.stub = remoteRPCProxy.getRemoteStub(cls) ;
            
            return true ;
        }else {
        	throw new InvalidConfigurationException("missing configurations.") ;
        }
    }

    public boolean isAvailable() {
    	return super.isAvailable() && stub != null ;
    }
    
//    public static void main(String[] args){
//    	Method[] ms = RPCServiceImpl.class.getMethods() ;
//    	
//    	for(Method m : ms){
//    		String key = getMethodKey(m) ;
//    		
//    		System.out.println("stub method key:" + key) ;
//    	}
//    	
//    }

}
