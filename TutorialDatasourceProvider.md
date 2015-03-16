## Set up a connection pool ##

> guzz supports apache commons DBCP and C3P0 connection pool providers in default, and allow you to define your own one on yourself.

> In the db configuration, item "guzz.maxLoad" indicates the max connections allowed in the pool, while item "pool" indicates which connection pool provider should be used.

> Configuration item "pool" is optional, the default provider is "C3P0".

> Example 1: Use C3P0 connection pool:

```
[masterDB]
guzz.identifer=blogMasterDB
guzz.IP=localhost
guzz.maxLoad=120

pool=c3p0

driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/blog?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60
```

> Example 2: Use Apache DBCP connection pool:

```
[logSlaveDB]
guzz.identifer=logSlaveDB1
guzz.IP=localhost
guzz.maxLoad=30

pool=dbcp

driverClassName=com.mysql.jdbc.Driver
url=jdbc:mysql://localhost:3306/log?useUnicode=true&amp;rewriteBatchedStatements=true
username=log
password=log
initialSize=10
minIdle=10
maxWait=1000
logAbandoned=true
removeAbandoned=true
removeAbandonedTimeout=180
```

> In the above example:

```
guzz.identifer=logSlaveDB1
guzz.IP=localhost
guzz.maxLoad=xxx
```

> are must (configuration). "guzz.identifer" must be unique to identify the pool, "guzz.IP" should be the database's IP address, and "guzz.maxLoad" is the max connections allowed.

> Except for the three items starts with "guzz.", and the "pool" item to identify the provider, other items are properties of the provider implementation itself which will be injected in the javabean setXXX() style on startup. Guzz will do the type conversation.

## connection pool provider supported ##

| **Provider name** | **pool value** | **parameters** |
|:------------------|:---------------|:---------------|
| C3P0(default) | c3p0 | http://www.mchange.com/projects/c3p0/index.html#configuration_properties |
| Apache DBCP | dbcp | http://commons.apache.org/dbcp/configuration.html |
| JNDI | jndi | **jndiName**: jndiName <br> <b>contextFactory</b>: The value of javax.naming.Context.INITIAL_CONTEXT_FACTORY <br>
<tr><td> No pool(connect on demand, and release on close.) </td><td> nopool </td><td> <b>driverClass</b>: driver class<br><b>jdbcUrl</b>:jdbc url<br><b>user</b>:user name<br><b>password</b>:password </td></tr></tbody></table>

<h2>How to write a new provider?</h2>

<h3>step 1, write the code:</h3>

<blockquote>In guzz, connection pool is managed by the DataSourceProvider. To write a new provider, you just have to implement the DataSourceProvider interface.</blockquote>

<blockquote>DataSourceProvider:</blockquote>

<pre><code>package org.guzz.connection;<br>
<br>
import java.util.Properties;<br>
import javax.sql.DataSource;<br>
<br>
public interface DataSourceProvider {<br>
	<br>
	/**<br>
	 * init or re-config the underly datasource.<br>
	 * &lt;p/&gt;<br>
	 * this method will be invoked at the startup, and may also be called by the configServer to re-config the settings(eg: reducing maxLoad when new db servers installed.).<br>
	 * <br>
	 * @param props  the config properties from the configServer<br>
	 * @param maxLoad usually means max database connections suggested.<br>
	 */<br>
	public void configure(Properties props, int maxLoad) ;<br>
	<br>
	/**<br>
	 * fetch the datasource. This will be called on every connection requiring, so make it fast! <br>
	 */<br>
	public DataSource getDataSource() ;<br>
	<br>
	/**<br>
	 * shutdown the pool.<br>
	 */<br>
	public void shutdown() ;<br>
<br>
}<br>
</code></pre>

<blockquote>For example, the Apache DBCP connection pool provider is simply:</blockquote>

<pre><code>public class DBCPDataSourceProvider implements DataSourceProvider{<br>
	private static transient final Log log = LogFactory.getLog(DBCPDataSourceProvider.class) ;<br>
	BasicDataSource dataSource = null ;<br>
	<br>
	public void configure(Properties props, int maxLoad){<br>
		if(dataSource == null){<br>
			dataSource = new BasicDataSource() ;<br>
		}<br>
		<br>
		JavaBeanWrapper bw = BeanWrapper.createPOJOWrapper(dataSource.getClass()) ;<br>
		Enumeration e = props.keys() ;<br>
		while(e.hasMoreElements()){<br>
			String key = (String) e.nextElement() ;<br>
			String value = props.getProperty(key) ;<br>
			<br>
			try{<br>
				bw.setValueAutoConvert(dataSource, key, value) ;<br>
			}catch(Exception e1){<br>
				log.error("unknown property:[" + key + "=" + value + "]", e1) ;<br>
			}<br>
		}<br>
		<br>
		//default max connections:500<br>
		if(maxLoad &gt; 1000 || maxLoad &lt; 1){<br>
			maxLoad = 500 ;<br>
		}<br>
		<br>
                //set the max connection number to the underly Connection Pool.<br>
		dataSource.setMaxActive(maxLoad) ;<br>
		<br>
		//fetch a connection to force the datasource to build the pool<br>
		Connection c = null ;<br>
		try {<br>
			c = dataSource.getConnection() ;<br>
		} catch (SQLException e1) {<br>
			log.error(props, e1) ;<br>
		}finally{<br>
			CloseUtil.close(c) ;<br>
		}<br>
	}<br>
<br>
	public DataSource getDataSource() {<br>
		return dataSource ;<br>
	}<br>
<br>
	public void shutdown() {<br>
		if(dataSource != null){<br>
			try {<br>
				dataSource.close() ;<br>
			} catch (SQLException e) {<br>
				log.error("fail to shutdown the DBCPDataSource", e) ;<br>
			}<br>
			<br>
			dataSource = null ;<br>
		}<br>
	}<br>
}<br>
</code></pre>

<blockquote>The argument "props" in "public void configure(Properties props, int maxLoad) ;" would be the properties configured in the "guzz_app.properties" without item "pool" and items starts with "guzz.".</blockquote>

<blockquote>The implementation class must also provide a public construct method with no arguments.</blockquote>

<h3>step 2, choose it in the configuration:</h3>

<blockquote>This is simple. You just have to set the item "pool" to the full qualified class name of your implementation. For example:</blockquote>

<pre><code>pool=org.guzz.connection.DBCPDataSourceProvider<br>
</code></pre>