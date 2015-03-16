## What is TemplatedSQL? ##

> TemplatedSQL is used to combine sqls dynamically.

> You write a single sql statement with conditions, and the sql is parsed on the runtime parameters and executed differently.

> This is similar to IBatis, but more powerful. You can write the templated sqls both in guzz.xml and in java code.

> In the next chapter, we will talk about [TutorialDynamicSQLService](TutorialDynamicSQLService.md). That service supports TemplatedSQL too.


## Configure TemplatedSQL ##

> TemplatedSQL is implemented based on guzz service and a Velocity implementation is shipped with guzz.

> To active it, declare a service in guzz.xml:

```

<service name="guzzTemplatedSQL" class="org.guzz.service.core.impl.VelocityTemplatedSQLService" />

```

> The service name MUST be guzzTemplatedSQL.

> After declaring the guzzTemplatedSQL service, add new attributes templated="true" to mark sqls as templated ones.

> Sample 1:
```
	<select id="listCommentsByName" orm="commentMap" templated="true">
			select * from @@commentMap
			
			#notEmpty($userName)
			 	where @userName = :userName
			#end
			
			<paramsMapping>
				<map paramName="userName" propName="userName" />
			</paramsMapping>
		</select>

```


Sample 2 --Be careful of SQL injections in your production system:
```

	<sqlMap dbgroup="cargoDB">
		<select id="selectCrossSize" orm="cargo" result-class="org.guzz.orm.rdms.MyCrossStitch" templated="true">
			select name, price, gridNum, size, brand from @@cargo where id > ${id}
		</select>
	</sqlMap>

```


Sample 3 :
```

	<sqlMap dbgroup="default">
		<select id="selectTimedDeletedPosts" orm="deletedPost" templated="true"><![CDATA[
			select * from @@deletedPost where checkedTime<>'' and timediff(deletedTime,checkedTime) > :time 
				 and timediff(deletedTime,now()) < :sTime
			
			#notEmpty($title)
				and title like :title
			#end
			
			#notEmpty($userNick)
				and userNick = :userNick
			#end
			
			order by id desc
			]]> 
			<paramsMapping>
				<map paramName="time" type="string" />
				<map paramName="sTime" type="string" />
				<map paramName="title" propName="title" />
				<map paramName="userNick" propName="userNick" />
			</paramsMapping>
		</select>
		
		<select id="countTimedDeletedPosts" orm="deletedPost" templated="true"><![CDATA[
			select count(*) from @@deletedPost where checkedTime<>'' and timediff(deletedTime,checkedTime) > :time
				 and timediff(deletedTime,now()) < :sTime
			 
			#notEmpty($title)
				and title like :title
			#end
			
			#notEmpty($userNick)
				and userNick = :userNick
			#end
			]]>
			<paramsMapping>
				<map paramName="time" type="string" />
				<map paramName="sTime" type="string" />
				<map paramName="title" propName="title" />
				<map paramName="userNick" propName="userNick" />
			</paramsMapping>
		</select>
	</sqlMap>

```


## The syntax of templated sqls ##

> Just like writing a velocity template, please read this for more details: http://velocity.apache.org/engine/releases/velocity-1.7/vtl-reference-guide.html

> Or Goolge search some velocity articles.


## Build templated sqls in java code ##

> Call CompiledSQLBuilder.buildTemplatedCompiledSQL, and pass the templated sqls.

> Then use the built CompiledSQL as usual, binding parameters and executing, or something else.

> The built CompiledSQL can be cached and reused.

## Performance ##

> The TemplatedSQL sqls run a little slower than normal sqls.

> In the shipped Velocity implementation, all sqls are loaded into memory to parse, so the extra consumption layered mainly on CPUs.



