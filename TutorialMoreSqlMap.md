## SqlMap的动态SQL是什么？ ##

SqlMap的动态SQL就是一条sql语句，根据传入的参数情况动态拼装出sql。这也是IBatis引以自豪的强大功能。

IBatis文档称“。如果你有使用JDBC或其他相似框架的经验，你就明白条件地串联SQL字符串在一起是多么的痛苦，确保不能忘了空格或在列表的最后省略逗号。动态SQL可以彻底处理这种痛苦。”。

IBatis的SqlMap很强大，但我觉得并不完美。这是我理想中的动态SQL以及其使用办法，欢迎讨论：

### 像IBatis的动态sql控制一样，要有足够的简洁的控制命令: ###

1. if

2. choose(when,otherwise)

3. where,set

IBatis还有一个foreach，我觉得没什么用，不需要支持（欢迎举例反驳我）。

if用来控制条件判断，如：

```

<select id=”findActiveBlogWithTitleLike” parameterType=”Blog” resultType=”Blog”>
	SELECT * FROM BLOG
	WHERE state = „ACTIVE‟
	
	<if test=”title != null”>
		AND title like #{title}
	</if>
</select>

```

choose用来控制if/else if/else if.../else语句。

where和set用来解决 and 逗号 where等关键词因为条件是否成立的变化引起的sql语句不完整问题。拼装过sql的人肯定能够明白。方式上IBatis已经挺好了。如：

```

....
	SELECT * FROM BLOG
	<where>
		<if test=”state != null”>
			state = #{state}
		</if>
		<if test=”title != null”>
			AND title like #{title}
		</if>
		<if test=”author != null and author.name != null”>
			AND title like #{author.name}
		</if>
	</where>
</select>

<update id="updateAuthorIfNecessary" parameterType="domain.blog.Author">
	update Author
	
	<set>
		<if test="username != null">username=#{username},</if>
		<if test="password != null">password=#{password},</if>
		<if test="email != null">email=#{email},</if>
		<if test="bio != null">bio=#{bio}</if>
	</set>
	
	where id=#{id}
</update>

```

### 与IBatis不同，我希望参数是强类型的: ###

在SQL参数进行条件判断时，我希望虽然传入的参数可能都是字符串，但在执行条件判断时，能够自动转换并按照正确的类型做条件检测。比如上面的：

```

		<if test=”state != null”>
			state = #{state}
		</if>
		
```

如果state是一个int类型，我希望写成：
```

		<if test=”state > 0 ”>
			state = #{state}
		</if>
		
```

这样准确性高，判断语句更简洁，而且不容易写错。再比如日期，则可以更容易写条件了，如：
```

		<if test=”createdTime < 2009-10-23 ”>
			readonly = true
		</if>
		
```

当然，对于参数的类型我需要自己指定，免得出错。如：
```

		<if test=”state > 0 ”>
			state = #{state}
		</if>
		<if test=”createdTime < 2009-10-23 ”>
			readonly = true
		</if>
		
		<paramsMapping>
			<map paramName="state" propName="blogState" />
			<map paramName="createdTime" dataType="datetime|yyyy-MM-dd" />
		</paramsMapping>	
			
```

对于参数state，我指定数据类型借用领域对象的blogState属性为其类型（blogState为int类型变量）；对于createdTime，我直接指定类型为datetime，如果传入的参数为字符串，按照"yyyy-MM-dd"格式解析成时间。

### 当然，无论如何动态，参数必须要按照PreparedStatement执行: ###

避免被sql注入，这一点无容置疑了。


### 相比IBatis的OGNL，我觉得支持类型自身方法更实用 : ###

举例来说，对于IBatis的例子：

```

<select id=”findActiveBlogLike” parameterType=”Blog” resultType=”Blog”>
	SELECT * FROM BLOG WHERE state = "ACTIVE"
	
	<choose>
		<when test=”title != null”>
			AND title like #{title}
		</when>
		
		<when test=”author != null and author.name != null”>
			AND title like #{author.name}
		</when>
		
		<otherwise>
			AND featured = 1
		</otherwise>
	</choose>
</select>

```

假设这里的state为String类型（，虽然从sql优化的角度不合适），我更希望写成这样：

```

<select id=”findActiveBlogLike” parameterType=”Blog” resultType=”Blog”>
	SELECT * FROM BLOG WHERE state = "ACTIVE"
	
	<choose>
		<when test=”title.trim().length() > 0”>
			AND title like #{title}
		</when>
		
		<when test=”author.getName().length() > 0”>
			AND title like #{author.name}
		</when>
		
		<otherwise>
			AND featured = 1
		</otherwise>
	</choose>
</select>

```

因为虽然这儿传入的Blog参数看起来很美，但大部分情况下我需要传入的是页面提交的参数，是一个Map。在Map中我希望兼容性更好，不仅能处理null，传入的空字符串也能处理掉。当然了，如果title为null，title.trim()这些直接返回false即可，不要报空指针异常。

这样写起来，看起来会更加整洁。而且实现起来的执行速度肯定要比OGNL快很多，struts2对OGNL的大量使用就是一个性能警示。


### 我没想好，是否需要支持复杂的判断逻辑 : ###

> 比如，支持括号，如：

```

<if test=”(state > 0 and order ~= 'desc') or (id > 10 and name == 'abc') ”>
		state = #{state}
</if>
 
```

这种复杂的逻辑，支持括号，支持or操作符，支持字符串包含匹配，大小写不敏感匹配等，是否有必要？或者哪些有必要？

也许更多的运算符比较有价值，括号之类的就算了。


### 其他的哪？ ###

欢迎讨论。

