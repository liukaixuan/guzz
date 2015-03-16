## General usage: ##

> guzz\_app.properties is used to store configuration details of an application. Most of the time, it contains configurations for databases and services.

> The content of guzz\_app.properties is organized just as what mysql does. A line represents a configuration item; and a line starts with # indicates a comment.

> `[groupName]` marks a start of a group. From the mark, to the next `[xxx]`, all items belong to the group "groupName". For example:

```
[masterDB]
xxx=xxx
xxx=xxx

[slaveDB]
....
```

> The groupName is also called "configuration group". Configuration group name can be repeated, as to define a configuration with many detailed groups.
> For example, we can configure three `[slaveDB]` to tell guzz that our system has three slave databases, so guzz can balance reads between them.

> The following is a typical configuration example:

```
#guzz app config file.

#master db
[masterDB]
guzz.identifer=blogMasterDB
guzz.IP=localhost
guzz.maxLoad=120
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/blog?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60

[updateMasterDB]
guzz.identifer=incUpdateDB1
guzz.IP=localhost
guzz.maxLoad=20
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/guzzSlowUpdate?useUnicode=true&amp;characterEncoding=UTF-8
user=slowupdate
password=slowupdate
acquireIncrement=10
idleConnectionTestPeriod=60

[logMasterDB]
guzz.identifer=logUpdateDB1
guzz.IP=localhost
guzz.maxLoad=20
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/guzzLogDB?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=loguser
password=loguser
acquireIncrement=10
idleConnectionTestPeriod=60

[guzzSlowUpdate]
#max size of cached queue
queueSize=20480
batchSize=2048

[guzzSlowUpdateServer]
#max size of cached queue
batchSize=50
pageSize=40
combinePageCount=10

[guzzDBLogServiceClient]
#max size of cached queue
batchSize=2048
queueSize=20480

#debug settings
[guzzDebug]
#runMode=debug/production
runMode=debug
#onError=halt/log/ignore
onError=halt
printSQL=false
printSQLParams=false
ignoreDemonThreadSQL=true

############################### fundamental services #####################
#other services' configurations go here...
```

> "guzz.identifer" and "guzz.IP" are used for management, they should work with your configuration server later.

> "guzz.maxLoad" defines the max capacity of a service. It depends on the service itself.

> The other items without the start of "guzz." means it is defined by the service itself.


## Use multiple properties files: ##

A properties file is designated in guzz.xml:
```
<config-server>
	<server class="org.guzz.config.LocalFileConfigServer">
		<param name="resource" value="guzz_app.properties" />
	</server>
</config-server>
```

In fact, up to 4 properties files and 4 optional properties files can be designated in guzz.xml which will be loaded and combined together during startup.

Optional properties file means that: If the file is exist, guzz will load it; or it will be ignored without any errors.

Here is the full configuration for configuring multiple properties files:

```
<config-server>
	<server class="org.guzz.config.LocalFileConfigServer">
		<param name="resource" value="guzz_app.properties" />
		<param name="resource1" value="guzz_app1.properties" />
		<param name="resource2" value="guzz_app2.properties" />
		<param name="resource3" value="guzz_app3.properties" />
                        
		<param name="optionalResource" value="guzz_app4.properties" />
		<param name="optionalResource1" value="guzz_app5.properties" />
		<param name="optionalResource2" value="guzz_app6.properties" />
		<param name="optionalResource3" value="guzz_app7.properties" />
	</server>
</config-server>
```

## Use the list of properties files: ##

This is a more flexible way to use multiple properties files.

A text file is used to list the names of all chose properties files. Guzz read the text file and load all properties files listed there.

Examples:

```
<config-server>
	<server class="org.guzz.config.LocalFileConfigServer">
		<param name="resourceList" value="resources_list.properties" />
	</server>
</config-server>
```

resources\_list.properties:
```

#basic config files
guzz_app.properties

#DBs
guzz_app_databases.properties

#guzz task config file.
*/root/optional_properties.properties

```

In this configuration, guzz will load and combine all configurations in guzz\_app.properties, guzz\_app\_databases.properties and /root/optional\_properties.properties.

The `*` marker before "/root/optional\_properties.properties" indicates that "/root/optional\_properties.properties" is a optional file. If "/root/optional\_properties.properties" exists, load it ; or ignore it.

## Working with Configuration Server ##

> The configuration server should be a central management system to manage application configurations for all systems, and notify them to reload when related configurations changed.

> Currently, guzz hasn't finished a default central configuration system yet, but only a local properties file reader(see the above).

> If you have a central configuration system, what you need to do to adapter guzz is to implement interface

```
 org.guzz.config.ConfigServer
```

> and configure it in guzz.xml:

```
<config-server>
	<server class="your config server's client implementation">
		<param name="param1" value="somevalue" />
		<param name="param2" value="somevalue2" />
    </server>
</config-server>
```

> In the startup, guzz will load your implementation, and set parameters in a javabean style--the "name" is the property name, and the "value" is the property value.

> In your implementation's setXXX() method, the accepted parameter type can be java.lang.String or org.guzz.io.Resource. If you use the later one, guzz will pass a Resource object(eg:File IO Stream) based on directory for convenience.

> For example, in the default "org.guzz.config.LocalFileConfigServer", we have to pass a parameter "resource", and the java code is:

```
public void setResource(org.guzz.io.Resource r){
...
}
```

> After injecting all parameters, guzz will call the ConfigServer's startup() method to perform the last notification.

> While shutting down, guzz will call the ConfigServer's shutdown() method before exits.

