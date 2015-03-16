## What is Dynamic SQL? ##

> Dynamic SQL in guzz doesn't mean combining a sql statement dynamically. It means load and execute configured sqls dynamically. The sqls are managed by the application itself, and are unknown until needed.

> In comparison to ibatis, ibatis allows you to store sqls in ibatis.xml, and guzz allows you to store sqls in guzz.xml. But, after configure the "xxx.xml" file, the sqls are read-only when the system is online. If we need to add a new sql, or a existed sql need to be modified to improve the performance, a restart of the system is required. To solve this problem, Dynamic SQL is introduced to manage the sqls dynamically. With that, you can add/modify/tune/delete sqls and their corresponding object-relation mappings online to reduce boring restarts.

## Where to use? ##

> There are two cases born for Dynamic SQL: SQL Tuning and Dynamic Datasource.

> -**SQL Tuning:** Replace old sqls with new tuned ones online when some performance bottleneck appears. Dynamic SQL has a option to override sqls in guzz.xml with the same names(ID). With this feature, your system can be developed in the traditional way(writing sqls in guzz.xml), and override it with dynamic sql when getting online to suit the production database.

> -**Dynamic Datasource:** This is a fresh design pattern, suitable for system with many always changed pages, or system providing always changed varied data. Primary design: When a new feature/page is required, configure a new sql and it's orm to the system(eg: upload a new sql file with orm settings), pass id, parameters, pagination to a central jsp or servlet to load the sql(eg:assume the id is the sql file name, so we know which file to read.), execute it and return the result to the invoker(eg: AJAX in the user end). So, when a new feature is required, what we have to do is just to write and upload a sql file, then developing the ajax showout, no server-side development or deployment is required. The Dynamic SQL will load the unknown sql file, parse the unknown sql, execute it with passed unknown parameters, and return the result in json format in a consist way. Powerful and simple!

> Case one is a sub-collection of case two. In the following description, we will take Dynamic Datasource as a example.

## How to configure it? ##

> Dynamic SQL works as a standard guzz service, but the service name **MUST** be "guzzDynamicSQL".

> To control how to load sqls for a special system, you have to write your own "Dynamic SQL" Service implements interface "org.guzz.service.core.DynamicSQLService".

> Fortunately, guzz provides a common implementation based on File System for normal systems. The implementation class is "org.guzz.service.core.impl.FileDynamicSQLServiceImpl", and it assumes ONE sql and its orm is stored in ONE xml file. The filename wiped off suffix ".xml" is the sql id(In java code, we execute sql by id).

> Now, with this common implementation, we'd show you how to configure the Dynamic SQL.

> First, declare the service in guzz.xml:

```
 <service name="guzzDynamicSQL" configName="guzzDynamicSQL" class="org.guzz.service.core.impl.FileDynamicSQLServiceImpl" />
```

> Then, add detailed configuration items in the properties file:

```
 [guzzDynamicSQL]
 #where to find the sql .xml files
 folder=/nas/conf/sqls/

 #file encoding
 encoding=UTF-8

 #When both this service and the guzz.xml have defined a sql for a same id, which one takes a high priority? 
 #true: use sql from this service. false: use sql in the guzz.xml.
 overrideSqlInGuzzXML=false

 #cache the parsed sql in memory until the file changed?
 useCache=true
```

> "FileDynamicSQLServiceImpl" takes four items as above. We will discuss the sql file later.

## How to use it? ##

> After configuration, the configured sqls in guzz.xml or from the DynamicSQLService is transparent to the developers. Remember in case two, we need a central jsp or servlet to loop the cycle, and now a SpringMVC Action is good enough.

> -**Central Action：**

```
public class AnyDataAction implements Controller {
	private GuzzContext guzzContext;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String id = request.getParameter("id") ;
		int startPos = RequestUtil.getParameterAsInt(request, "startPos", 1);
		int maxSize = RequestUtil.getParameterAsInt(request, "maxSize", 20);
		
		Map<String, String> params = RequestUtil.getAllParamsAsMap(request) ;

		List<Map> data = null ;
		
		ReadonlyTranSession session = guzzContext.getTransactionManager().openDelayReadTran() ;
		try{
			data = session.list(id, params, startPos, maxSize) ;
		}finally{
			session.close() ;
		}
		
		//output the data as json
		String json = new Gson().toJson(data);
		
		PrintWriter out = response.getWriter();
		out.println(json);
		
		return null ;
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}
}
```

> This action takes three permanent parameters: "id" to decide which sql should be executed, and "startPos" and "maxSize" to do pagination. Other parameters are packed in a Map, passed to query as parameters for the sql.

> We assume our central action is online, the url is: http://company/anyData.do

> -**Write SQL Files：**

> Create a new file named "some-uuid-no-dup-id.xml", and save it under "/nas/conf/sqls/". Fill the content:

```
<sqlMap dbgroup="bookDB">
	<select orm="book" result-class="java.util.HashMap">
	select @bookName, m_price as price from tb_book join xxxx on xxx...... 
	....very
   so long...
		very long where @bookISBN = :isbn and book_author = :author order by ...... long long sql.

		<paramsMapping>
			<map paramName="isbn" propName="bookISBN" />
			<map paramName="author" propName="author" />
		</paramsMapping>
	</select>
</sqlMap>
```

Ignore the real sql, just care of the format. In this xml, "dbgroup" indicates which database group to be executed in(Remember? A system can own many database groups in guzz); orm="book" means the queried ResultSet should be converted to object Book (according to Book's mappings defined in hbm.xml or annotation); result-class indicates that the converted class should be java.util.HashMap, the "book" just before should be overrided (The result is: the queried ResultSet will be converted to java.util.HashMap, but the key of the Map is not the column name of the table, but the mapped property names in object Book indicated by the 'orm="book"' declaration); "paramsMapping" indicates the data type of the 2 parameters in the sql by setting up their relationships with their delegated properties, so guzz can understand how to make the right type conversation for them from the passed java.lang.String parameters from the central servlet.

The format of the xml is similar to guzz.xml. [Read More](MoreFileDynamicSQLServiceFileFmt.md).

**Execute the SQL：**

> The sql file name is "some-uuid-no-dup-id.xml", so the id would be "some-uuid-no-dup-id". The sql takes two parameters, isbn and author.

> To execute it, we just have to visit: http://company/anyData.do?id=some-uuid-no-dup-id&pageNo=1&pageSize=50&isbn=xxx-aaaa&author=me .

> The sql will be loaded and executed, and return the first 30 matched data in json format to us.

> If you need another new data source, create a new xml file, save it under "/nas/conf/sqls/", visit "http://company/anyData.do" with new parameters. It is done!

## Write your own Dynamic SQL Service ##

> Sometimes, you would love to manage your sqls in other ways, maybe in database or some web services. It's fine for guzz, you just have to write a guzz Service implements "org.guzz.service.core.DynamicSQLService", and add it to your system. Remember, the service name **MUST** be "guzzDynamicSQL".

> The definition of DynamicSQLService is:

```
public interface DynamicSQLService {
	
	/**
	 * 
	 * Get sql by the id. 
	 * 
	 * <p>
	 * If the sql has been changed and should take effects, this method should return the new one.<br><br>
	 * The implementor is responsible for moniting the change of sqls, 
	 * making decisions whether it should take effects now or not, caching it for performance, and flushing cache in cluster.
	 * </p>
	 * 
	 * @param id The id used to identify sqls.
	 * @return CompiledSQL to be executed. Return null if no sql found for the given id.
	 */
	public CompiledSQL getSql(String id) ;
	
	/**
	 * When both this service and the guzz.xml have defined a sql for a same id, which one takes a high priority?
	 * 
	 * @return true: use sql from this service.<br>false: use sql in the guzz.xml.
	 */
	public boolean overrideSqlInGuzzXML() ;

}
```