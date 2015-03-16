## What is Virtual DB? ##

> In the previous two chapters, we split a big table into many small ones, that is great. But in some cases, the big table maybe too big to store in one database. We need to split it into small tables, **and** distribute the small tables into many machines. To the developers, they don't need to know how the data is distributed, and which database they are using now. We call this VirtualDB, a logic database with many unknown backends.

> With the VirtualDB, you can store your big table in different databases without changing the existing codes.

> VirtualDB is based on [Shadow Table](TutorialShadowTable.md). Before using VirtualDB, you have to configure your code to support shadowing.


## How to configure? ##

> To setup a Virtual database, first you have to create a virtualdbgroup, and point your business's dbgroup to it; then you have to define the rules of how to distribute the data.

> For example, we define two Virtual Databases "logDB" and "other" in guzz.xml:

```
	<tran>
		<virtualdbgroup name="logDB" dialectName="mysql5Dialect" shadow="class.name.of.the.distributing.rules" >
			<dbgroup name="log.db1" masterDBConfigName="logDB1" />
			<dbgroup name="log.db2" masterDBConfigName="logDB2" />
			<dbgroup name="log.db3" masterDBConfigName="logDB3" />
		</virtualdbgroup>
		
		<virtualdbgroup name="other" dialectName="h2dialect" shadow="xxxxxx" />
	</tran>
```

> Like "dbgroup", virtualdbgroup is declared under the "tran" element. You can also define "dbgroup" inside "virtualdbgroup".

> The "dbgroup"s defined inside virtualdbgroup elements are of the same with the ones defined under "tran", except for more clear management.

> virtualdbgroup has three attributes: "name" to name the (virtual) dbgroup; "dialectName" to indicate the database type; "shadow" to declare the data-distributing policy.

> The dbgroups declared inside virtualdbgbroup can omit attribute "dialectName", to inherit from the parent virtualdbgroup.

> We call "virtualdbgroup" Virtual Database Group, and "dbgroup" the Physics Database Group. A Virtual Database Group can hold Physics Database Groups only.

> In a virtual database group, all physics databases must be of the same provider. For example, all Mysql5, all Oracle 11g, but you cann't mix them.

## User Case: Shopping Site ##

> We continue the shopping site story in the previous chapter. Suppose our cross-stitch is getting very very hot, and we have to add a new group of databases to store it.

> Then, we have two database groups to store our cargoes: the cross-stitch in a new group, and others in the old group.

> In our previous design, we only have one "Cargo" business domain class to represent the cargo, and one business maps to one dbgroup. We have no time to re-design the whole project, VirtualDB is the only choice.

**First, Define a virtualdbgroup：**

```
	<tran>
		<dbgroup name="default" masterDBConfigName="masterDB" />
		<dbgroup name="mysql" masterDBConfigName="mysqlDB" dialectName="mysql5dialect" />
		<dbgroup name="oracle" masterDBConfigName="oracleDB" dialectName="oracle10gdialect" />
		
		<virtualdbgroup name="cargoDB" dialectName="default" shadow="org.guzz.test.shop.CargoVirtualDBView" >
			<dbgroup name="cargoDB.cargo2" masterDBConfigName="cargo2DB" />
		</virtualdbgroup>
		
	</tran>
	
	....
	<business name="cargo" dbgroup="cargoDB" file="classpath:org/guzz/test/shop/Cargo.hbm.xml" />
```

> In the "business" declaration, we point "cargo" to the virtual dbgroup "cargoDB".

> We also add a new dbgroup "cargoDB.cargo2" inside the virtualdbgroup to store cross-stitch.


**Second, Write the shadowing rules：**

> In the VirtualDB, shadowing is dbgroups oriented.

> You can consider the rule is: return the correct dbgroup for the given tableCondition.

> Yes, the parameter for VirtualDB is the "tableCondition" for Shadow Table. The are not only the same name, but also the same object. The tableCondition passed to shadow a table, is just the same one will be passed to shadow the VirtualDB.

> To write the rule, you can inherit from org.guzz.connection.AbstractVirtualDBView.

```
package org.guzz.test.shop;

public class CargoVirtualDBView extends AbstractVirtualDBView {
	
	public String getPhysicsDBGroupName(Object tableCondition) {
		String cargoName = (String) tableCondition ;
		Assert.assertNotEmpty(cargoName, "tableCondition can't be null, we don't have any default db to store un-categoried cargoes.") ;
	
		//store crossStitch in cargo2 db.
		if("crossStitch".equals(cargoName)){
			return "cargoDB.cargo2" ;
		}
		
		//store others in the default database.
		return "default";
	}

}

```

> In our Shopping site, the tableCondition is the cargo name, and we decide which dbgroup should be used based on it.

> After finishing this, you can move your cross-stitch cargo table to "cargo2DB", and add the connection pool parameters in the properties file, then you can run the Shopping Site again.


## Use Case: VirtualDBView ##

> The value of the "shadow" attribute in virtualdbgroup should be a full class implementing org.guzz.connection.VirtualDBView.

> The definition of org.guzz.connection.VirtualDBView is:

```
public interface VirtualDBView extends ContextLifeCycle {
	
	/**
	 * Set the configured dbgroup.
	 */
	public void setConfiguredVirtualDBGroup(VirtualDBGroup vdb) ;
	
	/**
	 * Retrieve the actually database group for the given condition.
	 * 
	 * <p>Guzz won't cache the returned result.</p>
	 * 
	 * @param tableCondition tableCondition
	 * @exception DaoException Raise a exception when no physics database group matched.
	 */
	public PhysicsDBGroup getDBGroup(Object tableCondition) throws DaoException ;

}
```

> setConfiguredVirtualDBGroup(VirtualDBGroup) is called on startup, passing the configuration of the virtual dbgroup.

> getDBGroup(Object tableCondition) returns the physics database group on the given tableCondition.

> The interface also has startup() and shutdown() methods, and you can get the GuzzContext by declaring GuzzContextAware interface. Through this, the developers will be able to create and manage the dbgroups on their own needs, without the limit of guzz.xml.

> If all the database groups you are using are already configured in guzz.xml, you can inherit from org.guzz.connection.AbstractVirtualDBView as the example above, to simplify the writing of the rule. Inherit from AbstractVirtualDBView, you only have to write one method to return the dbgroup name for the given tableCondition.


> ## Startup sequences of VirtualDBView: ##

  * 1. Parse configuration file and instance the VirtualDBView class;

  * 2. call setConfiguredVirtualDBGroup(VirtualDBGroup vdb)

  * 3. call setGuzzContext(GuzzContex guzzContext) if the GuzzContextAware interface is declared;

  * 4. call startup();

  * 5. call setExtendedBeanFactory(ExtendedBeanFactory factory) if the ExtendedBeanFactoryAware interface is declared;

  * 6. running....;

  * 7. call shutdown() when guzz exits.

