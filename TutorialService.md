## What is Service? ##

> Service is a concept borrowed from SOA, aimed to help your team accumulated and reuse modules and codes. Service is different from helper classes(, such as apache commons), it is in a higher level to reuse a whole business module, like Log Module or User Management Module.

> If you are familiar with SOA, you can consider Guzz Service as a more opened SOA. It only defines a interface and gives you a container, all other things, like protocols, has to be done by yourself. No WSDL, No Soap, everything relies on yourself.

> With Service, projects can borrow similar modules from old ones very easily, and benefit from saving repeating developments.

## How to Write a Service? ##

> To write a service, you just have to write a class implementing the interface of org.guzz.Service. To implement it, we suggest you extend it from:

```
org.guzz.service.AbstractService
```

> If you service needs remote calls or some other asynchronous operations, you are suggested to inherit from:

```
org.guzz.service.AbstractRemoteService<ServiceReturnType>
```

> It is based on JDK5+.

> Now, Let's take a example to figure out how how to write, configure and run a service.

## Anti-Cheating Service ##

> Suppose we are developing a big SNS system. To avoid cheating, we need to design a module to prevent users flooding us. The module can be used in: Limit one user to post only 10 posts in the forum in 60 seconds; limit a IP address to vote 2 times in 10 minutes; limit the read count of a article in the blog system to update only once in 10 seconds for one IP; limit one user to post 8 comments in 10 minutes in the news system....

> These features looked different, but in Service level, they are the same. What we need is to check how many operations have been done for a MARK in a given period. Different systems can provide different marks and time periods, and operate on their own when flooding occurs.

### Design the Service: ###

> Based on the requirements, we design the interface:

```
public interface FixedLifeCountService {
	
	/**
	 * Increment the count and return.
	 * 
	 * @param key mark
	 * @param addCount the count to add.
	 * @param maxCountAllowed max count allowed.
	 * @param maxLifeInSeconds time period to alive.
	 * @return true: the total count for now is less that the max count allowed; false: bigger that max allowed.
	 */
	public boolean incCountIfLess(String key, int addCount, int maxCountAllowed, int maxLifeInSeconds) ;
}
```

### Implement the Service: ###

> We use memcached to cache the counts in the server side. Our implementation extended from AbstractService as suggested above:

```
import java.io.IOException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;

import org.guzz.service.ServiceConfig;
import org.guzz.service.AbstractService;

public class MemcachedCountServiceClientImpl extends AbstractService implements FixedLifeCountService {
	protected MemcachedClient client ;

	private String appName ;

	public boolean incCountIfLess(String key, int addCount, int maxCountAllowed, int maxLifeInSeconds) {
		String m_key = appName + key ;

		//inc to memcached, and fetch the count for now from memcached.
		int countNow = (int) client.incr(m_key, addCount, addCount , maxLifeInSeconds) ;
		
		return (maxCountAllowed >= countNow) ;
	}

	public boolean configure(ServiceConfig[] scs) {
		if(scs.length == 0){
			log.warn("FixedLifeCountService is not started. no configuration found.") ;
			return false ;
		}

		String serverList = scs[0].getProps().getProperty("serverList") ;
		this.appName = scs[0].getProps().getProperty("appName") ;

		try {
			this.client = new MemcachedClient(AddrUtil.getAddresses(serverList)) ;
				
			return true ;
		} catch (IOException e) {
			log.error(serverList, e) ;
			return false ;
		}		

		return true ;
	}

	public boolean isAvailable() {
		return super.isAvailable() && client != null ;
	}

	public void shutdown() {
		if(client != null){
			client.shutdown() ;
		}
	}

	public void startup() {
	}

}
```

> The AbstractService is a abstract class, it leaves some methods for you to finish.

> public boolean configure(ServiceConfig[.md](.md) scs): Configure method, used to inject Configurations in guzz\_app.properties by guzz. The passed parameter is a array, containing the configuration groups in the properties file. In this example, we retrieve appName and serverList items. The appName is the system's name to avoid key conflicting in memcached, and the serverList is the memcached servers' list. configure() method should return true if everything is fine.

> public boolean isAvailable() Is the service available now?

> public void shutdown() Shutdown the service, release any resources.

> public void startup() Start the service, called after configure(ServiceConfig[.md](.md) scs) returning true.

### Configure the Service: ###

> After writing and compiling the service, we pack it to jar file, and put the jar into the application' lib path.

> Take the forum system as a example, first we have to declare the service in guzz.xml. The service is declared by the tag `<service>`, and each `<service>` declared one service.

> For our anti-cheat service, the guzz.xml would become something like:

```
<guzz-configs>
…
<config-server>
		<server class="org.guzz.config.LocalFileConfigServer">
			<param name="resource" value="bbs.properties" />
		</server>
</config-server>
…
<service name="fixedLifeCountService" configName="fundFixedLifeCountServiceClient" class="org.guzz.service.impl.MemcachedCountServiceClientImpl " />
…
</guzz-configs>
```

The `<service>` tag has four attributes:
  1. name: service name. We use the service name to retrieve the service in the application. Must be unique.
  1. configName(Optional): The configuration group name in the configuration property file. Used to pass parameters to the Service.
  1. dependsOn(Optional): Depended services. Separate multiple depended service names with comma. Guzz will inject the depended services on startup by calling "public void setXXXXService(depended service's interface)". The method must be public, starts with "set" and ends with "Service". Injected by type.
  1. class: full qualified class name of the implementation.

> Every service can define its parameters(like appName and serverList in anti-cheat service), and the parameters are read and passed by the ConfigManager, such as org.guzz.config.LocalFileConfigServer in this example. ConfigManager is a interface, you can write it yourself. The default implementation is LocalFileConfigServer which reads a grouped properties file like bbs.properties, and shipped in guzz's core package.

> The format of bbs.properties is similar to Mysql's configuration file divided by [groupName](groupName.md). To our service, you need to add a new group to bbs.properties:

```
[fundFixedLifeCountServiceClient]
appName=bbs
serverList=localhost:11211
```

> The group name must be the configName declared in guzz.xml. Unlike Mysql, bbs.properties allow duplicated config groups. For example, **if** we need to pass 3 appName(s) to our service, we can configure it as:

```
[fundFixedLifeCountServiceClient]
appName=bbs
serverList=localhost:11211

[fundFixedLifeCountServiceClient]
appName=bbs1
serverList=localhost:11211

[fundFixedLifeCountServiceClient]
appName=bbs2
serverList=localhost:11211

```

> In this situation, public boolean configure(ServiceConfig[.md](.md) scs) will pass a array lengthen three. If you doesn't configure it at all, guzz will pass a zero-sized array.

### Fetch & Use Service ###

> To fetch a service, what you need is to get the reference of GuzzContext, and call:

```
FixedLifeCountService fixedLifeCountService = (FixedLifeCountService) this.getGuzzContext().getService("fixedLifeCountService") ;
```

> If you are using spring IOC, you can also export the service as a bean, and use the bean as the service in IOC style:

```
    <!-- guzzframework -->
    <bean id="guzzContext" class="org.guzz.web.context.spring.GuzzContextBeanFactory" factory-method="createGuzzContext">
    	<constructor-arg><value>/WEB-INF/guzz.xml</value></constructor-arg>
    </bean>
    
    <bean id="fixedLifeCountService" class="org.guzz.web.context.spring.GuzzServiceFactoryBean">
    	<property name="serviceName" value="fixedLifeCountService" />
    </bean>

```

> When you get the service, it is simply a normal java interface, call its method(s) you need:

```
If(!fixedLifeCountService.incCountIfLess(userId, 1,10, 600)){
	throw new Exception("Have a little rest, please!") ;
}
```

> ## Startup sequences of Service: ##

  * 1. Class.forName(service class name).newInstance() to instance the service;

  * 2. Inject depended services

  * 3. call setGuzzContext(GuzzContex guzzContext) if the GuzzContextAware interface is declared;

  * 4. call {@link #configure(ServiceConfig[.md](.md))} to start the initialization

  * 5. call startup();

  * 6. register the service to guzz's {@link ServiceManager} (where you can query the service by name).

  * 7. call setExtendedBeanFactory(ExtendedBeanFactory factory) if the ExtendedBeanFactoryAware interface is declared;

  * 8. running....;

  * 9. call shutdown() when guzz exits.
