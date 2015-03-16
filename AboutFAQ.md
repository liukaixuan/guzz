## Why choose guzz? ##

**1. Guzz is intended for the large-scaled projects, can I use it for small-and-medium scaled projects?**

> Yes, you can.

> -**Costs**: Indeed, many features are designed for large system in guzz, but it is transparent. Against other frameworks, guzz won't increase your developing costs for small-and-medium scaled projects.

> -**Time**: If you have fundamental services(you will have some after several guzz projects), guzz will reduce your time and cost by reusing the services accumulated. If you have many "read" features, guzz's taglib will fast your speed horribly!

> -**Scalability**: Small projects can grow into big ones sometimes. In this situation, guzz will reduce the risk of project refactoring in the future.

**2. After introducing guzz into my project, how can I make others witness the effects as quickly as possible?**

> Introduce guzz's jsp taglib into the project! Configure guzz, leave original codes untouched. Let guzz taglib take over the reading and displaying of the domain objects. Then, you will find that your team is finishing most of the pages' developing without writing even a single line of java code. It will make you very fast, reducing 50%-60% time of the same job.

## On Usage: ##

**1. The sample project cann't run, a database pool error occurs. Why?**

```
06:38:33,156 WARN  [BasicResourcePool] com.mchange.v2.resourcepool.BasicResourcePool$AcquireTask@1e146ca -- Acquisition Attempt Failed!!! Clearing pending acquires.
While trying to acquire a needed new resource, we failed to succeed more than the maximum number of allowed acquisition attempts (30). 
Last acquisition attempt exception: 
java.sql.SQLException: No suitable driver
 at java.sql.DriverManager.getDriver(DriverManager.java:243)
 at com.mchange.v2.c3p0.DriverManagerDataSource.driver(DriverManagerDataSource.java:224)
 at com.mchange.v2.c3p0.DriverManagerDataSource.getConnection(DriverManagerDataSource.java:120)
```

> Reason: The problem is caused by IDE which changed the properties file automatic, but wrong! Please extract the bigVote.properties from the jar again and open/edit it with common Properties File Editor, **NOT** the default Advanced Properties editor of MyEclipse. The reason of this error is that the advanced editor of MyEclipse will automatically combine the “configuration items with same name”. Due to the grouping of our properties, the configuration items of same name can be valid item in different groups, and can’t be combined!

**2. Is JNDI supported?**

> Yes, since guzz 1.3.1.

**3. Guzz mentions that “it supports a large amount of database and master-slave reading and writing separation.” What does it mean? Can you show me some details? Besides, “master-slave” is a term of Mysql, how about other databases?**

> Master-slave separation refers to that the deployment of databases must follow the master-slave model. All writings should be done in Master, while all readings should be done in Slave, so as to reduce the load of Master and realize the paralleling of multi-databases.

> If you have only one database machine, you can ignore this term. Read [TutorialTranSession](TutorialTranSession.md) for the API.

> The principle of the Master-slave is that one database accept writes(called master) and replicate it to other databases(called slave). The master and the slave share the same data, and called a group together. The user can read data from any one. When more reads come, we can add more slave machines to increase the load capacity very easily.

**4. How did guzz know which machine is for read(slave) and which one is for write(master)?**

> In guzz.xml,
```
<dbgroup name="log" masterDBConfigName="masterDB" slaveDBConfigName="slaveDB" dialectName="mysql5dialect" />
```

> we set "masterDBConfigName" and "slaveDBConfigName", it identified the master/slave configuration groups.

**5. Can the master and slave databases be a cluster?**

> Yes, it depends on the jdbc driver.

**6. What does "shard" mean?**

> Shard means to distribute tables belongs to one system to different databases on different machines.

> For example, in a sns system, you can put the user information in database 1, short messages in database 2 and others in database 3. When a short message arrived, we have to update message count in database 1, write the messages in database 2,  and write a log in database 3. With guzz, you dont's have to care about the three databases, you just "insert"/"update" three "objects" like what you have done before(eg: in hibernate?). Guzz will judge how to do the job well in different databases, and guarantee the distributed transaction automatically.

**7. How to call a storage procedure?**

> First, you have to get a JDBCTemplate by invoke createJDBCTemplate() in ReadonlyTranSession or WriteTranSession, then get the jdbc connection through JDBCTemplate.getConnection(), and then, call your storage procedure with the jdbc method.

> The transaction of "JDBCTemplate.getConnection()" is under the scope of "WriteTranSession" it created from.

> Remember to call TranSession.close() when you are done. Dont's close "JDBCTemplate.getConnection()" you got directly.
