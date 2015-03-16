## What is Shadow Table? ##

> Shadow Table means splitting a big table into many small ones, and storing domain objects(eg:comment, message, article) among the split small tables.

> To the developers, what they are operating is a domain object corresponding to one (virtual) table, but underly guzz persist data among the split smaller tables according to the user-defined route rules.

> With Shadow Table, you are able to manage split tables with minor efforts. To active Shadow Table, you just have to declare a interface org.guzz.orm.ShadowTableView, and pass a parameter indicating the condition used by the route/split rules before calling guzz's persist API.

> All guzz's persist API supports Shadow Table, including executing sqls defined in guzz.xml. For example:

```
 select sql_no_cache * from @@comment left join .... force index(idx_special)....
```

> In this sql, @@comment is used to replace the actual table name. If the comment is shadow, guzz will compute the actual table name for the current condition of the comment object, such as TB\_COMMENT\_2, and then replace it.

## Where to use? ##

  * 1. **Split a big table into small ones.** Split a big table into many small tables with the rule of createTime, userInfo, channels or so on, to improve performance.

  * 2. **Archive data.** According to something like timestamp, save data into monthly/daily-created tables, to low the cost of maintaining. For data archiving, Shadow table can be used with mulit-datasources, so you can save archived data in a backup machine, and keep the online database fit.

  * 3. **VIP customers treating.** For example, you can separate paid users and free users, and provide different service level(paid users in oracle cluster, free users in mysql).

## How to Use? ##

> For example, we have a comment system for news. To gain better performance, we store the Comment in 2 tables, TB\_COMMENT1 for user with a even userId and TB\_COMMENT2 for user with a odd userId. (In a real system, you may split tables by news channels.)

> Now, step by step, I'll show you how to use shadow table.

> The sample code is stored in the guzz project, you can find it in the test source folder.

### step 1: Design the domain class ###

> For concision, our Comment class owns a auto-increment id, a userId, a userName, content of the comment, and the createdTime as below:

```
package org.guzz.test;

public class Comment {
	
	private int id ;
	
	private int userId ;
	
	private String userName ;
	
	private String content ;
	
	private java.util.Date.Date createdTime ;
    
    //get and set methods
```

> And, we define the User class.(skipped)

### step 2: Define OR mapping ###

> Create the object-relation mapping file Comment.hbm.xml, and fill the content:

```
<?xml version="1.0"?>
<!DOCTYPE guzz-mapping PUBLIC "-//GUZZ//GUZZ MAPPING DTD//EN" "http://www.guzz.org/dtd/guzz-mapping.dtd">
<guzz-mapping>
    <class name="org.guzz.test.Comment" table="TB_COMMENT" shadow="org.guzz.test.CommentShadowView">
        <id name="id" type="int">
        	<generator class="native" />
        </id>
        <property name="userId" type="int" column="userId" />
        <property name="userName" type="string" column="userName" />
        <property name="content" type="string" column="DESCRIPTION" />
        <property name="createdTime" type="datetime" column="createdTime" />
    </class>
</guzz-mapping>
```

> This file is similar to hibernate's, but with a extra attribute:shadow="org.guzz.test.CommentShadowView".

> The "shadow" attribute indicates that the domain class of org.guzz.test.Comment should be mapped to a shadow table, the table name should be judged on the runtime condition.

> And the attribute value "org.guzz.test.CommentShadowView" is the rule of how to judge the runtime table name. The rule is defined by the application itself, in our example, it is:

```
package org.guzz.test;

import org.guzz.exception.GuzzException;
import org.guzz.orm.AbstractShadowTableView;

public class CommentShadowView extends AbstractShadowTableView {

	public String toTableName(Object tableCondition) {
		if(tableCondition == null){ //Force the developers to set the table condition, or raise a exception.
			throw new GuzzException("null table condition is not allowed.") ;
		}
		
		//The passed table condition is the User object
		User u = (User) tableCondition ;
		
		//odd userId to TB_COMMENT1, or TB_COMMENT2
		int i = u.getId() % 2 + 1 ;
		
		return super.getConfiguredTableName() + i;
	}

}
```

> The CommentShadowView is derived from AbstractShadowTableView, and implement public String toTableName(Object tableCondition). This method returns the actual table on the giving runtime condition. In this example, it returns TB\_COMMENT1 or TB\_COMMENT2. The tableCondition is the passed condition, set by developers before calling persist method, and can be anything. We'll discuss it more later.

> The super.getConfiguredTableName() returns the table name configured in Comment.hbm.xml. In this example, it returns TB\_COMMENT.

### step 3: Declare Comment in guzz ###

> Add a new line in guzz.xml:

```
<business name="comment" dbgroup="default" file="classpath:org/guzz/test/Comment.hbm.xml" />
```

> We declare the name of the Comment class to "comment", so we can use "@@comment" to replace the Comment's table name in sqls.

### step 4: Insert a comment ###

> As to persist a un-shadow object, the first step is to fetch the TransactionManager, and then open a WriteTranSession, and then execute a persist method.

```
		    TransactionManager tm = guzzContext.getTransactionManager() ;
		    WriteTranSession session = tm.openRWTran(true) ;

            Comment c = new Comment() ;			
			c.setContent("my content") ;
			Date now = new Date() ;
			c.setCreatedTime(now) ;
			c.setUserName("lucy") ;
			c.setUserId(i) ;
			
			Guzz.setTableCondition(new User(1)) ;
			
			session.insert(c) ;

```

> To insert the shadow comment, we add a new line:Guzz.setTableConditon(new User(1)) ;. In guzz, this is how we set the runtime table condition.

> When we set this condition, guzz will pass the condition to ShadowView to compute the actual table name, and replace @@comment in sqls with it.

### step 5: Query Comments ###

> There are 4 methods to query shadow objects in guzz. For example, we have to query comments by userName.

> -**API 1:** By SearchExpression:

```
                TransactionManager tm = guzzContext.getTransactionManager() ;
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		Guzz.setTableCondition(new User(1)) ; //set table condition
		
		SearchExpression se = SearchExpression.forClass(Comment.class) ;
		se.and(Terms.eq("userName", "lily")) ; //add query condition
                
                //query
                List comments = session.list(se) ;

                //Set tableCondtion for this SearchExpression, to override the condition set by Guzz.setTableCondition.
                //If the set tableCondition is null, Guzz.getTableCondition will be used.
                se.setTableCondition(new User(2)) ; 
                comments = session.list(se) ; //In this query, the table will be checked by tableCondition User(2).
```

> -**API 2:** By CompiledSQL(built sql from plain sql in code)

```
                TransactionManager tm = gf.getTransactionManager() ;
		ReadonlyTranSession session = tm.openDelayReadTran() ;
		
		String sql = "select * from @@comment where userName = :param_userName" ;
		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(Comment.class, sql) ;
		cs.addParamPropMapping("param_userName", "userName") ; //
		
		User u2 = new User() ;
		u2.setId(2) ;
		
		Guzz.setTableCondition(new User(1)) ;
		assertEquals(session.list(cs.bind("userName", "lily"), 1, 1000).size(), 500) ;
		assertEquals(session.list(cs.bind("userName", "lucy"), 1, 1000).size(), 0) ;

		//BindedCompiledSQL.setTableCondition has a high priority over Guzz.setTableCondition.
		//But the value set can't be null, or the tableCondtion in Guzz.setTableCondition will be used.
		assertEquals(session.list(cs.bind("userName", "lily").setTableCondition(u2), 1, 1000).size(), 0) ;
		assertEquals(session.list(cs.bind("userName", "lucy").setTableCondition(u2), 1, 1000).size(), 500) ;
```

> -**API 3:** By SQL configured in guzz.xml(as ibatis)

> First, configure the sql and its OR-mapping in guzz.xml:

```
            <sqlMap dbgroup="default">
		....		
		<select id="listCommentsByName" orm="commentMap">
			select * from @@commentMap where @userName = :userName
		</select>
		
		<orm id="commentMap" class="org.guzz.test.Comment" table="TB_COMMENT" shadow="org.guzz.test.CommentShadowView">
		    <result property="id" column="id" type="int"/>
		    <result property="userId" column="userId" type="int"/>
		    <result property="userName" column="userName" type="string" />
		    <result property="createdTime" column="createdTime" type="datetime" />
		</orm>
	</sqlMap>
```

> In this sample, we declare a sql named listCommentsByName, and map it to commentMap. We use @@comment to replace the table name in sql, as we cann't determine the actual table name now.

> We declare a attribute "shadow" in the orm element to active the feature of shadow table.

> By the way, the "orm" attribute can be a business name to tell guzz that the OR-mapping/shadowing is the same as defined in the xxxx.hbm.xml. For example: `<select id="listCommentsByName" orm="comment">`.

> After configuration, we can execute it:

```
                TransactionManager tm = gf.getTransactionManager() ;
		ReadonlyTranSession session = tm.openDelayReadTran() ;

		Guzz.setTableCondition(new User(1)) ;
		
		HashMap<String, String> params = new HashMap<String, String>() ;
		params.put("userName", "lily") ;
		
		List<Comment> comments = session.list("listCommentsByName", params, 1, 10000) ;
```

> The method signature doesn't support pass a tableCondition when query by id, so the only way to pass it is by Guzz.setTableCondition.

> -**API 4:** By guzz taglib

> Guzz's list, get, page, inc and boundary tags also support "tableCondition" attributes.

> If tableCondition attribute is set, the value is passed to compute the table name of the business. If the set value is null(default value), guzz use tableCondition set by Guzz.setTableCondition instead when the business is a shadow one.

> If you set the "tableCondition" attribute for a g:boundary tag, all tags in this boundary inherits this attribute.

> For example, we have to query lily's first 20 comments, and the comments is owned by the current user:

```
<g:list var="m_comments" business="comment" limit="userName=lily" orderBy="id desc" pageSize="20" tableCondition="${currentUser}" />
```

## Join & Union Query ##

> Read the next chapter.

> ## Startup sequences of ShadowView: ##

  * 1. Parse configuration file and instance the ShadowView class;

  * 2. call setConfiguredTableName(String tableName) to set the configured table name.

  * 3. call setGuzzContext(GuzzContex guzzContext) if the GuzzContextAware interface is declared;

  * 4. call startup();

  * 5. call setExtendedBeanFactory(ExtendedBeanFactory factory) if the ExtendedBeanFactoryAware interface is declared;

  * 6. running....;

  * 7. call shutdown() when guzz exits.


