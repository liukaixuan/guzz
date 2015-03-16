## Example 1: Vote System ##

This sample project is a vote system, based on spring IOC + springMVC + guzz + mysql + fundamental services.

Vote System uses 2 Mysql database groups in the data-layer. The first group is the main database, and the second group is used to store temporary data and logs. For the log, you can switch to a third database when needed.

This sample also showed you how to use (fundamental) services. There are three services involved in, IP to location, anti-cheating service, and admin user service.

Guzz's core services slowupdate/slowupdateServer, log, and debug service are also used.

**Download the project：**

```

svn checkout http://guzz.googlecode.com/svn/demos/en/GuzzVote GuzzVote

```

The downloaded project is a MyEclipse project, so import it directly. doc/vote.sql in the project is used to create the database schemas.

After creating the databases and tables, edit /WEB-INF/bigVote.properties to modify database connections' parameters(We use both c3po and DBCP connection pools in this example).

```
Warning: Don't open the bigVote.properties with the default "MyEclipse Properties Editor"! It will combine parameters and cause problems.

Choose the bigVote.properties -> Right Click -> Open With -> Properties File Editor
```

After startup, visit：/console/login.jsp

## java.sql.SQLException: No suitable driver ##

If you encounter this error:

```
06:38:33,156 WARN  [BasicResourcePool] com.mchange.v2.resourcepool.BasicResourcePool$AcquireTask@1e146ca
 -- Acquisition Attempt Failed!!! Clearing pending acquires. 
 While trying to acquire a needed new resource, we failed to succeed more than the maximum number of allowed acquisition attempts (30). 
 Last acquisition attempt exception: 
java.sql.SQLException: No suitable driver
 at java.sql.DriverManager.getDriver(DriverManager.java:243)
 at com.mchange.v2.c3p0.DriverManagerDataSource.driver(DriverManagerDataSource.java:224)
 at com.mchange.v2.c3p0.DriverManagerDataSource.getConnection(DriverManagerDataSource.java:120)
 .....
```

extract the bigVote.properties from the zip file, put it into the project again, and edit that file by a normal "Properties File Editor".

The default properties editor in MyEclipse is too advanced to achieve this job.