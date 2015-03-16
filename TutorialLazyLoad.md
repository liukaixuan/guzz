## Lazy Load ##

> Guzz's lazy loading follows the same principle of hibernate's. You need cglib library to enable it.

> The principle of lazy loading is: when you are reading a object, the lazied properties are not fetched from the database instancely; and when you are going to read the property(by getXxx()) not fetched before, guzz open a new slave database connection, read and return that property in a second demand. So, you can set some large properties but maybe not have to be loaded every time to a lazied one, to avoiding fetching consuming.

> -**NOTE:** Unlike hibernate, guzz's lazy loading doesn't need a opened connection be kept, so you don't need something as OpenSessionInView filter.

> -**Sample:**

```
<property name="content" type="string" lazy="true" column="NAME" />
```

**Execute flow in guzz:**

![http://guzz.org/raw-attachment/wiki/LazyLoad/guzz_load_load_lct_en.png](http://guzz.org/raw-attachment/wiki/LazyLoad/guzz_load_load_lct_en.png)

## Custom (Column) Loader ##

> -**What and Why?**

> In the real-world production system:

  * 1. A big property data may stored in file system, not database, for higher performance;

  * 2. In another system, some property data may stored in a remote third-part system. We have to read it through a special remote call protocol, and use it later.

  * 3. In the other system, some property may be performance critical(eg:readCount of a Article). We have to read it from cache, or read it from the database when the cache expires and update the cache again.

  * 4. In the other's other system, it's hard to image what we really want to do!

> Totally, for a real-world system, data is not purely stored in database. To the ORM, we need a fresh design! It should allow the developers to load property values from anywhere they need, but without any extra efforts or codes. That is what the Custom Column Loader does! It covers the complexity of where the data is, and provides the developers with a single entrance of javabean getXXX() methods to all data.

> With Custom Column Loaders for each special properties, any data sources can be wrapped to be operated by simple POJO getXXX() methods.

> -**NOTES:** Custom Column Loader only handles data read. Data write is not supported, for 2 reasons:

> -**1. Most of the time, your Custom Loader will return a interface to the data(eg:TranClob/TranBlob). The interface will own its own read/write methods, and would be a better choice.[[BR](BR.md)]**

> -**2. Most of the time, your special data is time-consuming, and may cause long-transaction problems when in custom loaders. So, we ask that operation to be executed in your own control. If you really need the transaction while the domain object is inserting, updating or deleting, declare the interface of org.guzz.dao.PersistListener will do the job.**

> Custom (Column) Loader can be mixed with Lazy Load.

> -**How to Configure?**

> Custom Column Loader is configured in the mapping file. You add a new attribute "loader" for each of your special property requiring this feature. The "loader" value is the full class name of your implementation. For example:

```
<property name="favCount" type="int" column="FAV_COUNT" loader="com.mycompany.guzz.FavCountLoader" lazy="false" />
```

> -**Execute flow in guzz:**

![http://guzz.org/raw-attachment/wiki/LazyLoad/loader_design_en.png](http://guzz.org/raw-attachment/wiki/LazyLoad/loader_design_en.png)

> -**How to write a Custom Column Loader?**

> Create a new java class, and implement interface:

```
org.guzz.orm.ColumnDataLoader
```

> You are done!

> -**The API of ColumnDataLoader is:**

```
package org.guzz.pojo;


/**
 * 
 * user-defined loader to load specified property of a domain object. eg: load a property value from the file system.
 * <p/>
 * per column mapping per instance.
 * <p/>
 * startup sequences:
 * <ol>
 * <li>loader = XXXClass.newInstance()</li>
 * <li>loader.configure(ObjectMapping mapping, Table table, String propName, String columnName)</li>
 * <li>.....</li>
 * <li>injected {@link GuzzContext} based on implementing {@link GuzzContextAware} or not</li>
 * <li>loader.startup()</li> 
 * <li>injected {@link ExtendedBeanFactory} based on implementing {@link ExtendedBeanFactoryAware} or not</li>
 * <li>....</li>
 * <li>loader.shutdown()</li>
 * </ol>
 */
public interface ColumnDataLoader {
	
	/**
	 * configure the loader.
	 * 
	 * @param mapping
	 * @param table fetch Object stored table.
	 * @param tableColumn the column to be loaded. The passed tableColumn is not fully inited, the orm and dataType will not be available.
	 */
	public void configure(ObjectMapping mapping, Table table, TableColumn tableColumn) ;
	
	/**
	 * load the data instancely during other properties reading from the database.
	 * 
	 * @param rs The current resultset. the resultset(and connection) will be closed after all properties are loaded. 
	             Your returning value cann't rely on this for future usage.
	 * @param objectFetching The object being orm. 
	             The property before this property in the hbm.xml configuration file is already set, so you can use it here. 
	             This param could be null on loading with something like org.guzz.orm.mapping.FirstColumnDataLoader.
	 * @param indexToLoad the propName index in the resultset.
	 * @return the returned object will be set to the pojo property.
	 */
	public Object loadData(ResultSet rs, Object objectFetching, int indexToLoad) throws SQLException ;
	
	/**
	 * eagerly load the lazied property for read. invoked by pojo.getXXX()
	 * <p/>
	 * guzz would never know what you have done to fetch the property, so it could <b>NOT</b> help you release any related resources acquired. 
	 * 
	 * @param fetchedObject the already loaded pojo.
	 * 
	 * @return the loaded object. the object will be set to the fetchedObject automatically.
	 */
	public Object loadLazyData(Object fetchedObject) ;

	/**
	 * eagerly load the lazied property for write inside a read-write transaction.
	 * <p/>
	 * guzz would never know what you have done to fetch the property, so it could <b>NOT</b> help you release any related resources acquired. 
	 * 
	 * @param tran the current opened read-write database transactional environment.
	 * @param fetchedObject the already loaded pojo.
	 * 
	 * @return the loaded object. the object will <b>NOT</b> be set to the fetchedObject automatically.
	 * @exception DaoException throw exception on @param fetchedObject doesn't exist in the database.
	 */
	public Object loadLazyDataForWrite(WriteTranSession tran, Object fetchedObject) throws DaoException ;
	
	public void startup() ;
	
	public void shutdown() throws Exception ;		
}
```

> Custom Loader is inited and started at guzz's startup, and will be only one instance for each configuration in the whole life-cycle of your system.

## Startup Sequences: ##

  * 1. Parse the mapping file, create the loader instance by: XXXClass.newInstance();

  * 2. call configure(ObjectMapping mapping, Table table, String propName, String columnName)

  * 3. call setGuzzContext(GuzzContex guzzContext) if you declare interface: GuzzContextAware

  * 4. call startup()

  * 5. call setExtendedBeanFactory(ExtendedBeanFactory factory) if you declare interface: ExtendedBeanFactoryAware

  * 6. providing service...

  * 7. call shutdown() when guzz is shutting down.
