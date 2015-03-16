## Continue introduction: ##

```
<g:addLimit limit="${consoleUser}" />
```

> The passed "limit" for business "blogVote" is the application's "current login admin user". This is a advanced usage, and must declare a interpret to tell guzz what the "current login admin user" means, as guzz won't be able to understand application's specified phases.

> This special "limit" is used to add condition(s) for business "blogVote", so we have to add a interpret for that business:

```
<business name="blogVote" interpret="guzz.test.business.BlogVoteInterpreter" file="classpath:guzz/test/business/BlogVote.hbm.xml" />
```

> The interpreter is used to transform user-defined more meaningful phases to SearchTerms. To write a interpreter, you just have to implemement interface BusinessInterpreter, and translating your application's phases to SearchTerms.

> To write a BusinessInterpreter, you can create a class derived from SEBusinessInterpreter.

> For example, the content of guzz.test.business.BlogVoteInterpreter is:

```
package guzz.test.business;

import java.util.List;

import org.guzz.GuzzContext;
import org.guzz.orm.interpreter.SEBusinessInterpreter;
import org.guzz.orm.se.InTerm;
import org.guzz.orm.sql.BindedCompiledSQL;
import org.guzz.orm.sql.CompiledSQL;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.TransactionManager;
import org.guzz.web.context.GuzzContextAware;

public class BlogVoteInterpreter extends SEBusinessInterpreter implements GuzzContextAware {

	TransactionManager tm = null ;
	
	//cached sql
	CompiledSQL cs ;
	
	//Override the base method. The logic in base method is raising a exception.
	protected Object explainOtherTypeConditon(Object limitTo) {
		//Find authorized votes based on the login AdminUser passed in.
		if(limitTo instanceof AdminUser){
			AdminUser a = (AdminUser) limitTo ;
			if(a.isSystemAdmin()){ //system admin. No limit condition is required.
				return null ;
			}else{
				
				ReadonlyTranSession session = tm.openDelayReadTran() ;
				
				try{
					//TODO: add support for many user groups
					BindedCompiledSQL bsql = cs.bind("authGroup", a.getAuthGroups()[0]) ;
					//Only read the first column, and treat it as integer.
					bsql.setRowDataLoader(FirstColumnDataLoader.newInstanceForReturnType("int")) ;
					
					List<Integer> cids = session.list(bsql, 1, 50) ;
					
					if(cids.isEmpty()){
						throw new VoteException("No authorized channels.") ;
					}
					
					return new InTerm("channelId", cids) ;
				}finally{
					session.close() ;
				}
			}
		}

	public void setGuzzContext(GuzzContext guzzContext) {
		tm = guzzContext.getTransactionManager() ;
		
		String sql = "select @id from @@" + Channel.class.getName() + " where @authGroup = :authGroup" ;
		
		cs =  tm.getCompiledSQLBuilder().buildCompiledSQL(Channel.class, sql) ;
		
		//Tell guzz that the parameter "authGroup"'s type derived from property "authGroup".
        cs.addParamPropMapping("authGroup", "authGroup") ;
	}

}
```

> In the example, interface

```
org.guzz.web.context.GuzzContextAware
```

> is declared to tell guzz to inject the GuzzContext to this Interpreter.

> A BusinessInterpreter is instanced only once on the startup for one domain class, so, it must be thread-safe!

## Keys of writing a custom Interpreter ##

> The simple way to write a custom BusinessInterpreter is to create a class derived from SEBusinessInterpreter, then override the parent's "protected Object explainWellKnownCondition(String limitTo)" and "protected Object explainOtherTypeConditon(Object limitTo)".

> explainWellKnownCondition is called to parse user-defined String conditions, like "myArticles" and so so.

> explainOtherTypeConditon is used to parse non-String conditions. Such as the condition "${consoleUser}" in the above.

> The default implementation of the both are throwing a exception, indicating a illegal condition.

> In your implementation, return null if you believe there is no need to add the current passed custom condition to the query, throw a exception if the condition is illegal such as no permission granted for a special queried column, OR return a normal SearchTerm condition which will be added in the query.

## Configure the Interpreter ##

> Custom Interpreter is configured with the business object.

> If you are mapping business object by hbm.xml and declare it with business tag in guzz.xml, set the interpreter attribute of the business tag to your implementation's full class name.

> If you map it with (JPA) annotation. Add the annotation of org.guzz.annotations.Entity in your business class, and set the annotation's interpreter attribute to your implementation.

## BusinessInterpreter Interface: ##

```
package org.guzz.orm;

public interface BusinessInterpreter {

	/**
	 * Translating the giving limit condition to a Search Condition guzz supported.
	 * 
	 * @param limitTo the condition user passed.
	 * @return supported search condition. return null if this condition can be ignored.
	 * @throw Exception Throw exception if the limit is not authorized.
	 */
	public Object explainCondition(Object limitTo) throws Exception  ;
	
}
```

> The limitTo in the interface can be "checked=true", "blogId=18", or a user-defined one like ${consoleUser}. Currently, the returned search condition can only be SearchTerm.

## Startup sequence of BusinessInterpreter: ##

  1. Parse configuration file and instance the BusinessInterpreter class;
  1. call setGuzzContext(GuzzContex guzzContext) if the GuzzContextAware interface is declared;
  1. call startup();
  1. call setExtendedBeanFactory(ExtendedBeanFactory factory) if the ExtendedBeanFactoryAware interface is declared;
  1. running....;
  1. call shutdown() when guzz exits.


