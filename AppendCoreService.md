## Introduction ##

> Guzz distributes itself with some common used services built in to avoid re-coding. You can use these directly in your projects.

> 

## Debug Service ##

**purpose:** Turn on/off the output of executed sqls. Set system run level.

**Interface:** org.guzz.service.core.DebugService

**Service name:** guzzDebug

**Used inside guzz:**

  * 1. guzz will pass all sqls and their parameters to debugService. The built-in debugService will output the sqls and parameters by your configurations.
  * 2. When defining database groups in guzz.xml(dbgroup), you can miss configurations in the properties file for masterDBConfigName and slaveDBConfigName in "debug" run level. But in "production" run level, guzz will raise a exception.

**Declare Service:** Not required. Guzz will load it on startup. Your application can override the default implementation by declaring a service named guzzDebug.

**Service Parameters(configured in properties file):**

```
[guzzDebug]
#runMode=debug/production
runMode=debug

#how to handle unexpected exceptions?
#onError=halt/log/ignore
onError=halt

#print sql statements to the commons log?
printSQL=true

#print sql parameters to the commons log?
printSQLParams=true

#ignore sqls executed in guzz demon thread(Thread.isDemon() == true).
ignoreDemonThreadSQL=true

#print out how many nano-seconds a sql takes to execute.
measureTime=true

#only print out slow sqls that takes over xxx mill-seconds to execute. 0 means print out all.
onlySlowSQLInMillSeconds=0

```

The configuration group name must be guzzDebug.

## Dynamic Load SQL Service ##

**purpose:** Load sql by id dynamically. Guzz allows you to configure your sqls in guzz.xml, and use it by id as ibatis does. But, when you need to change or add a sql, you have to modify the guzz.xml, and restart the whole application to take effects. With DynamicSQLService, you can define your own sql-source, change and load the sqls by yourself in a online system, without any restarts. Dynamic SQL is a good choice for:
  * 1. Tune Performance. Replace a old sql with a new one without restarting.

  * 2. Dynamic data source. When a new feature is required, add a new sql and its mapping online, then you can query the data by a id, parameters for the sql and pagination through a common servlet(the servlet just has to pass querying to guzz with the passed id and parameters), and return the queried results in json or xml. Then, what you need to do is just to write a ajax script and build your pages.

  * 3. Secure sqls. Load sqls from a secure way, and only expose a id/name to the developers.

**Interface:** org.guzz.service.core.DynamicSQLService

**Service name:** guzzDynamicSQL

**Used inside guzz:**

  * 1. Guzz query DynamicSQLService for a sql by id when executing sqls by id.

**Declare Service:** Configure a service implements org.guzz.service.core.DynamicSQLService with the service name of guzzDynamicSQL.

**Default Implementation in guzz:** guzz distributes a default DynamicSQLService based on file system. Each xml file contains a sql and its mapping, and the sql id is the file name without the suffix of ".xml".

> How to configure?

> Declare in guzz.xml:
```
 <service name="guzzDynamicSQL" configName="guzzDynamicSQL" class="org.guzz.service.core.impl.FileDynamicSQLServiceImpl" />
```

> and add configurations in the properties file:

```
 [guzzDynamicSQL]
 #where to find the sql .xml files
 folder=/nas/conf/sqls/

 #file encoding
 encoding=UTF-8

 #When both this service and the guzz.xml have defined a sql for a same id, which one takes a priority? true: use sql from this service. false: use sql in the guzz.xml.
 overrideSqlInGuzzXML=false

 #cache the parsed sql in memory until the file changed?
 useCache=true
```

Notes: the service name must be: guzzDynamicSQL

## Count update queue service(Client-side) ##

**purpose:** Log operations for increment or decrement a count number column by primary key to a temporary table. In the meantime, a demon thread reads operations from the temporary table, combines operations for the same records, and updates back to the main table. This service is usually used in a big site to handle the count updating bottle-neck of database. This service is the client side, and handles the part of writing to a temporary table.

**Interface:** org.guzz.service.core.SlowUpdateService

**Service name:** guzzSlowUpdate

**Used inside guzz:**
  * 1. g:inc taglib uses this service to do count updating. If this service is not available, g:inc won't work!

Guzz ships with 2 versions of the client implementations. If you have many objects to update, such as message's readCount in twitter, use the first one; If you have less objects to update, but they would be updated very very frequent, like super star's visit count in twitter, use the later one.

**Declare Service For the first one:** The first one is declared in guzz by default. But you have to add a business declaration in guzz.xml:

```
<business dbgroup="updateDB" name="guzzSlowUpdate" file="classpath:xxx/IncUpdateBusiness.hbm.xml" />
```

> [IncUpdateBusiness.hbm.xml](MoreIncUpdateBusinessHbmXml.md) is stored under org.guzz.service.core.impl in guzz's jar file. You need to copy it to somewhere guzz can load, and create the corresponding table with the sqls provided inside [IncUpdateBusiness.hbm.xml](MoreIncUpdateBusinessHbmXml.md).

> [IncUpdateBusiness.hbm.xml](MoreIncUpdateBusinessHbmXml.md) holds the counting operations, and will be stored in the temporary table.

> And you also have to add the configuration parameters for the service in the properties file:

```
[guzzSlowUpdate]
#max size of cached queue
queueSize=20480

#batch size for updating to the temporary database.
batchSize=2048
```

**Declare Service For the later one:**

> guzz.xml：

```
<service name="guzzSlowUpdate" configName="guzzSuperSlowUpdate" class="org.guzz.service.core.impl.SuperSlowUpdateServiceImpl" />

<business dbgroup="updateDB" name="guzzSlowUpdate" file="classpath:xxx/IncUpdateBusiness.hbm.xml" />
```

> Override the default one with the same service name. (You can also write your own implementation, and override the default)

> properties file:

```
[guzzSuperSlowUpdate]
#batch size for updating to the temporary database.
batchSize=2048

#how many millseconds to wait when there is no new updates available.
updateInterval=500
```

**Java API to call the service:**

  1. Fetch guzzSlowUpdate service. The service's java interface is: org.guzz.service.core.SlowUpdateService

> 2. Call its methods.

```
public interface SlowUpdateService {
	
	/**
	 * update a count. database version.
	 * 
	 * @param dbGroup dbgroup to update
	 * @param tableName table name to update
	 * @param columnToUpdate column name to update
	 * @param pkColName primary column name of the table to be updated
	 * @param pkValue primary key
	 * @param countToInc count to inc
	 */
	public void updateCount(String dbGroup, String tableName, String columnToUpdate, String pkColName, Serializable pkValue, int countToInc) ;
	
	
	/**
	 * update a count. java object version.
	 * 
	 * @param businessName business name
	 * @param tableCondition shadow table. Pass null if shadow is not used.
	 * @param propToUpdate the java property name to be updated.
	 * @param pkValue the primary property value.
	 * @param countToInc count to inc
	 */
	public void updateCount(String businessName, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) ;
	
	
	/**
	 * update a count. java object version.
	 * 
	 * @param domainClass business domain class
	 * @param tableCondition shadow table. Pass null if shadow is not used.
	 * @param propToUpdate the java property name to be updated.
	 * @param pkValue the primary property value.
	 * @param countToInc count to inc
	 */
	public void updateCount(Class domainClass, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) ;

}

```

**Taglib API:**

> Use g:inc taglib to do the updating in the JSP file.

## Count update queue service(Server-side) ##

**purpose:** Transfer the operations in the temporary table to the main database. One instance for one temporary table, cluster is not supported until LeaderService is injected.

**Interface:** org.guzz.service.db.SlowUpdateServer

**Service name:** Anything you like. slowUpdateServer is suggested.

**Used inside guzz:** Guzz won't use it inside the framework.

**Declare Service:** Declare in guzz.xml:

```
<service name="slowUpdateServer" configName="guzzSlowUpdateServer" class="org.guzz.service.db.impl.SlowUpdateServerImpl" />
```

**Service Parameters(configured in properties file):**

```
[guzzSlowUpdateServer]
#batch size for updating the main database
batchSize=50

#page size for reading from the temporary table
pageSize=40

#how many pages to read from the temporary table for one loop updating
combinePageCount=10

#millseconds to wait for the next round of updates checking
updateInterval=500
```

**Mechanism:** slowUpdateServer reads pageSize\*combinePageCount records from the temporary table, combines operations for the same records, and updates the main database in a sql batch operation. The batch size is batchSize.

**Tip 1:** slowUpdateServer can only be started when both the declaration in guzz.xml and parameters in properties file are exist. Usually, we declare slowUpdate and slowUpdateServer services in guzz.xml, but only one machine declares slowUpdateServer's configurations, to keep the consist of the guzz.xml and only one instance of slowUpdateServer is started. This is a useful trick.

**Tip 2:** You can turn on the log to watch out the effects of operations combining. Add a line in your log4j.properties:

```
log4j.logger.org.guzz.service.db.impl=debug
```

**Callback on "affected 0 rows while updating the count":**

Sometimes, you would like or have to store the count columns in a separate table.

slowUpdateServer provides a callback service for handling the record synchronization between the main table and the count table.

When slowUpdateServer executes a update sql, and finds out "0 rows affected" returned, the callback service would be called and you can sync the count record there.

To active this feature, you have to write a service implements org.guzz.service.db.impl.UpdateExceptionHandlerService, and inject it into slowUpdateServer.

Sample Code:
```
public class BlogUpdateExceptionHandlerServiceImpl extends AbstractService implements UpdateExceptionHandlerService {
		
	public void exceptionCaught(Exception e) throws Exception {
		//ignore any exceptions
		if(log.isDebugEnabled()){
			log.error(e, e) ;
		}
	}

	public boolean recordNotFoundInMainDB(WriteTranSession writeSession, JDBCTemplate jdbcTemplate, IncUpdateBusiness obj) {
		String tableName = obj.getTableName() ;
				
		if(tableName.startsWith("tb_user_info")){
			UserInfo user = new UserInfo() ;
			user.setUserName(obj.getPkValue()) ;
			user.setCreatedTime(new java.util.Date()) ;
			
			writeSession.insert(user) ;
		}else if(tableName.startsWith("tb_user_actions_count")){
			UserActionsCount count = new UserActionsCount() ;
			count.setUserName(obj.getPkValue()) ;
			count.setCreatedTime(new Date()) ;
			
			writeSession.insert(count) ;
		}else{
			//ignore and pass the record
			return false ;
		}
		
		//re-execute
		return true;
	}

	public boolean configure(ServiceConfig[] scs) {
		return true ;
	}

	public boolean isAvailable() {
		return true ;
	}

	public void shutdown() {
	}
	
	public void startup() {
	}

}

```

In this sample, a new record would be inserted according to its table name if it is not exist.

Inject it into slowUpdateServer:

```
<service name="blogUpdateExceptionHandlerService" class="xxx.BlogUpdateExceptionHandlerServiceImpl"/>

<service name="slowUpdateServer" dependsOn="feedUpdateExceptionHandlerService" configName="guzzSlowUpdateServer" class="org.guzz.service.db.impl.SlowUpdateServerImpl" />

```

## Insert Queue Service ##

**purpose:** Write objects into a memory queue, and another demon thread inserts the queued objects into the database.

**Interface:** org.guzz.service.db.InsertQueueService

**Service name:** Anything you like. insertQueueService is suggested.

**Used inside guzz:** Guzz won't use it inside the framework.

**Declare Service:** Declare in guzz.xml:

```
<service name="insertQueueService" configName="guzzInsertQueueService" class="org.guzz.service.db.impl.InsertQueueServiceImpl" />
```

**Service Parameters(configured in properties file):**

```
[guzzInsertQueueService]
#commit size
commitSize=2048

#max size of log queue
queueSize=20480
```

**Mechanism:** This service created an array sized of queueSize. All objects inserted are stored in this array in a cycle mode. The demon thread open a not-auto-commit connection, check the array, and insert the queued objects into the main database, and commit it. Each time, the demon thread commit at most commitSize objects.

**Tips:** You can insert different business objects into the InsertQueueService at the same time.


## ExecutorService ##

**purpose:** Packs the JDK's ExecutorService to a service to build one ThreadPool for all asynchronous operations.

**Interface:** java.util.concurrent.ExecutorService

**Service name:** Anything you like. executorService is suggested.

**Used inside guzz:** Guzz won't use it inside the framework.

**Declare Service:** Declare in guzz.xml:

```
<service name="executorService" configName="jdk5ExecutorService" class="org.guzz.service.core.impl.JDK5ExecutorServiceImpl" />	
```

**Service Parameters(configured in properties file):**

```

[jdk5ExecutorService]
#min threads
corePoolSize=5

#max threads
maxPoolSize=50

#max idle time in mill-seconds for a thread. The timeout thread will be terminated until the thread pool drops to the size of corePoolSize.
keepAliveMilSeconds=60000

#max queue size of pending tasks.
queueSize=2048

```


## LeaderService ##

**purpose:** Determine if this machine is the leader one in the cluster.

**Interface:** org.guzz.service.core.LeaderService

**Service name:** Anything you like. "leaderService" is recommended.

**Used inside guzz:** If this service is injected into a slowUpdateServer, the slowUpdateServer won't run until the injected leaderService.amILeader() returns true.

**Declare Service:** There is no default implementation in guzz. Implementation Example:http://code.google.com/p/halo-cloud/source/browse/JavaClient/src/main/java/misc/com/guzzservices/version/impl/ZKLeaderElectionServiceImpl.java



