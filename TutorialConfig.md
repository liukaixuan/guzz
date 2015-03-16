## How to configure and start guzz? ##

> The startup of guzz is to initialize the GuzzContext, and hold its reference. The GuzzContext is the API gate to guzz's full functions.

### Standalone Application: ###

Create guzz's core configuration file--guzz.xml, and save it under the directory of the classpath.

```
import org.guzz.Configuration;
import org.guzz.GuzzContext;

GuzzContext gc = new Configuration("classpath:guzz.xml").newGuzzContext() ;
//perform you actions......
//.....
//shutting it down when you application exit.
gc.shutdown() ;
```

### Web Application: ###

Create guzz's core configuration file--guzz.xml, and save it under the directory of the "/WEB-INF/".

Add the following code to web.xml:

```
<context-param>
   <param-name>guzzConfigLocation</param-name>
   <param-value>/WEB-INF/guzz.xml</param-value>
</context-param>

<listener>
   <listener-class>
      org.guzz.web.context.ContextLoaderListener
   </listener-class>
</listener>
```

then, guzz is ready for you.

You can use guzz's database taglib in jsp pages.

> GuzzContext will be shutdown when the web-app quits.

### Web Application with Spring IOC support: ###

  * 1. Create guzz's core configuration file--guzz.xml, and save it under the directory of the "/WEB-INF/".

  * 2. Replace spring's ContextLoader with guzz's in the web.xml. For example:

Replace

```
<listener>
	<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
</listener>
```

with guzz's ContextLoader for springframework:

```
<listener>
      <listener-class>org.guzz.web.context.spring.GuzzWithSpringContextLoaderListener</listener-class>
</listener>
```

  * 3. Add GuzzContext as a spring bean(the bean name must be "guzzContext") into spring's applicationContext.xml file：

```
<bean id="guzzContext" class="org.guzz.web.context.spring.GuzzContextBeanFactory" factory-method="createGuzzContext">
    <constructor-arg><value>/WEB-INF/guzz.xml</value></constructor-arg>
</bean>
```

Most of the time, you should add another "BaseDao" bean for making up your own Dao or Managers. (Take it as hibernate's sessionFactory.getHibernateTemplate())

```
<bean id="abstractGuzzDao" class="org.guzz.dao.GuzzBaseDao" abstract="true">
     <property name="guzzContext" ref="guzzContext" />
</bean>
```

  * 4. Join Spring Declaration Transaction(guzz 1.3.0+):

> Add a new attribute locator="spring" in element tran in guzz.xml like this:

```
<tran locator="spring">
```

> If have configured Spring Transaction for Hibernate before, copy the configurations and change the bean "transactionManager"'s definition (in applicationContext.xml) to:

```
<bean id="transactionManager" class="org.guzz.web.context.spring.GuzzTransactionManager">  
	<property name="guzzContext" ref="guzzContext" />
</bean>
```


**Patch:** If declared by @Transactiona annotation, create a package called org.guzz.dao and put http://guzz.googlecode.com/svn/wiki/no-wikis/GuzzBaseDao.java [encoding:UTF-8](file.md) in it to override the same class in guzz.jar.


> It is done. Here is a example:

```
    <bean id="transactionManager" class="org.guzz.web.context.spring.GuzzTransactionManager">  
        <property name="guzzContext" ref="guzzContext" />  
    </bean>
      
    <bean id="transactionBase" class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean"  
            lazy-init="true" abstract="true">    
        <property name="transactionManager" ref="transactionManager" />    
        <property name="transactionAttributes">
            <props>
                <prop key="*">PROPAGATION_REQUIRED</prop>    
            </props>
        </property>
    </bean>
     
    <!-- Most of the time, your services layer -->
    <bean id="userDao" parent="transactionBase" >
        <property name="target">
        	<bean parent="abstractGuzzDao" class="org.guzz.dao.UserDaoImpl">
		    </bean>
        </property>
    </bean>
```

  * 5. Now, guzz will work with spring IOC. You can fetch guzzContext through the spring bean or GuzzWebApplicationContextUtil as above.


### fetch GuzzContext in Web Application: ###

In servlet or JSP:

```

import org.guzz.web.context.GuzzWebApplicationContextUtil;
import org.guzz.GuzzContext;

//session is HttpSession
//or pass ServletContext
GuzzContext gc = GuzzWebApplicationContextUtil.getGuzzContext(session.getServletContext()) ;

```


