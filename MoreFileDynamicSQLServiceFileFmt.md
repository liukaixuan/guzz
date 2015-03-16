## FileDynamicSQLServiceImpl Service ##

> FileDynamicSQLServiceImpl provides dynamic sqls stored in file system. The file should be a xml file, and the sql id is the file name without the suffix of ".xml".

> One xml file maps to a sql, containing both the sql statement and its OR-mapping. The content format is similar to guzz.xml's. For example:

```
 <sqlMap dbgroup="default">
	<select orm="userMap">
		select * from @@user
		 where 
		 	@id = :id 

		<paramsMapping>
			<map paramName="id" propName="id" />
		</paramsMapping>
	</select>
	
	<orm id="userMap" class="org.guzz.test.UserModel" table="TB_COMMENT" shadow="org.guzz.test.CommentShadowView">
	    <result property="id" column="id" type="int"/>
	    <result property="userId" column="userId" type="int"/>
	    <result property="userName" column="userName" type="string" />
	    <result property="createdTime" column="createdTime" type="datetime" />
	</orm>
 </sqlMap> 
```

> Or, you can simply map the result to a Map:

```
 <sqlMap dbgroup="default">
	<select orm="user" result-class="java.util.HashMap">
		select * from @@user
		 where 
		 	@id = :id 

		<paramsMapping>
			<map paramName="id" propName="id" />
		</paramsMapping>
	</select>
 </sqlMap> 
```

> "dbgroup": which database group this sql should be executed in?

> "orm": The OR-mapping name. It can be a business name, a global orm name defined in guzz.xml, or a orm defined in this file. The orm defined in this file will override the global one with the same name.

> "result-class": The mapped java object returned to the user. It can be any javabean class, or a implementation of java.util.Map. For javabean class, FileDynamicSQLServiceImpl stores the ResultSet by setXXX() methods; for a Map, FileDynamicSQLServiceImpl stores it by put(key, value). If the "orm" attribute is set, the key of the Map would be the mapped property name. Example: result-class="java.util.HashMap"

> "paramsMapping": Set the propNames that the parameters in the sql are binding for. This segment is optional, but important. It helps guzz to determine how to transform the passed parameters' values to their correct types JDBC required.

> "result": Map a java property with a column as in guzz.xml. The "result" here supports attributes: property, column, type, loader, null.
