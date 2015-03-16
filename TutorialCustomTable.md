## What is Custom Table? ##

> Custom Table sounds interesting, it maps a domain class to a (virtual) table with **unknown** columns! YES, unknown!

> The table may be a (virtual) table stored as many small tables like Shadow Table does, and each small table can own some unknown different columns.

> Custom Table not only sounds interesting, it is also very useful, for example for shopping site. In a typical shopping site, there are thousands of cargoes, and they share some common attributes such as name,description,price,onlineTime,storeCount,rate and so on. In the meantime, each cargo has its own special attributes, for example Book owns ISBN, author, publisher, Clothe owns color, size, brand, and TV set owns screenSize, refreshRate, networkSupport, and so on. These special attributes are very important for a shopping site, as you cann't sell clothes if your customers cann't search it with size and brand.

> In a traditional ORM solution, like hibernate, one table is mapped to one java domain class. And If you want your customers to search or list cargoes through database, every cargo must be stored in a separate table as they would own different attributes mapped to different columns. Suppose you have 1000 cargoes, then you must design 1000 tables, and 1000 domain classes in java, and 1000 hbm.xml files for mapping. 1000 tables can be distributed in many machines to keep it manageable, but what about java code? Write duplicate codes for each cargo in many separate projects(every cargo needs basic features such as rating, comment and logistics.), or write one copy for 1000 domain classes in a huge project? Anyway, the code amount would be a nightmare!

> To solve this problem, the best way is to define only one (abstract) domain class, and map it to any tables with shared columns and unknown self columns. The developer can force only on one domain class as a normal pojo, and the domain class can dynamically map itself to the right table and the right (unknown) columns.  In the domain class, we define common properties that every cargo would use, and leave a Collection property to store special properties for the cargo being used. For example:

```
public class Cargo { 
	
	private int id ;
	
	private String name ;
	
	private String description ;
	
	/**how many items left in the store. */
	private int storeCount ;
	
	private double price ;
	
	private Date onlineTime ;
	
	/*for the sake of concision, ignore other useful properties.*/
	
	/**
	 * special properties owned by this Item only.
	 */
	private Map specialProps = new HashMap() ;

        //get and set methods....
```

> Here, we define some common properties, and a specialProps field to store a actual cargo's special properties.

> To set up OR-mapping, we need to write a hbm.xml file:

```
<?xml version="1.0"?>
<!DOCTYPE guzz-mapping PUBLIC "-//GUZZ//GUZZ MAPPING DTD//EN" "http://www.guzz.org/dtd/guzz-mapping.dtd">
<guzz-mapping>
    <class name="org.guzz.test.shop.Cargo" table="tb_cargo" shadow="org.guzz.test.shop.CargoCustomTableView">
        <id name="id" type="int">
        	<generator class="native" />
        </id>
        
        <property name="name" type="string" column="name" />
        <property name="description" type="string" column="description" />
        <property name="storeCount" type="int" column="storeCount" />
        <property name="price" type="double" column="price" />
        <property name="onlineTime" type="datetime" column="onlineTime" />
    </class>
</guzz-mapping>
```

> This file is similar to a normal one. As the content of specialProps is unknown, we won't map it.

> This is what we called Custom Table, guzz's solution to do dynamic OR-mapping.

## User Case: A Shopping Site ##

> Let's continue our discussion. In the database level, we need to create a table for each cargo. Our rule for the table name is tb\_cargo\_cargoName, and we design each table on its unique features/columns. Apparently, despite of special columns, we need to split tables for Cargo first as the previous chapter told. We need a ShadowTableView, and split/shadow table with cargoName(tableCondition) as the above mapping defined: shadow="org.guzz.test.shop.CargoCustomTableView".

> A shopping site may have many type of cargoes, and new cargoes may join later, so the best way to manage cargoes's special properties is in database, so you can manage them online. We define a new business SpecialProperty for that:

```
/**
 * special properties' declarations of our cargoes.
 */
public class SpecialProperty {

	/**unqiue id for management.*/
	private int id ;
	
	/**which cargo this property belongs to.*/
	private String cargoName ;
	
	/**property name of the cargo (used in java).*/
	private String propName ;
	
	/**the column name in the table of database to store this propety.*/
	private String colName ;
	
	/**
	 * dataType. take this as the 'type' property in hbm.xml file.
	 */
	private String dataType ;

        //get and set methods...
```

> -**Define the ORM:**

```
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
    <class name="org.guzz.test.shop.SpecialProperty" table="tb_s_property">
        <id name="id" type="int">
        	<generator class="native" />
        </id>
        
        <property name="cargoName" type="string" column="cargoName" />
        <property name="propName" type="string" column="propName">
        </property>
        <property name="colName" type="string" column="colName">
        </property>
        <property name="dataType" type="string" column="dataType" />
    </class>
</hibernate-mapping>
```

> OK, so far so good. If Custom Table can read SpecialProperty from the database, and map cargoes correctly in runtime, we are done.

## User Case: CustomTableView ##

> Like ShadowTableView, guzz needs your application to tell her how to compute the runtime OR-mapping, and this is done by a new interface: org.guzz.orm.CustomTableView. CustomTableView is derived from ShadowTableView, and shares the same attribute "shadow" to declare it in hbm.xml. You can treat CustomTableView as a special type of ShadowTableView. The interface of CustomTableView is:

```
/**
 * Interface for mapping dynamic tables with runtime-determinated columns(different tables and different tables' columns mapped to a same domain class).
 * <p>
 * Only {@link POJOBasedObjectMapping} supports this feature.
 * </p>
 */
public interface CustomTableView extends ShadowTableView{
	
	/**
	 * Set ObjectMapping configured in the hbm.xml file. This method only run one time on startup.
	 */
	public void setConfiguredObjectMapping(POJOBasedObjectMapping configuredMapping) ;
	
	/**
	 * Get the runtime real ObjectMapping for the given tableCondition.
	 * <p>The invoker won't cache the returned {@link POJOBasedObjectMapping}, so the implementor should do the cache for performance critical system.
	 * </p>
	 */
	public POJOBasedObjectMapping getRuntimeObjectMapping(Object tableCondition) ;

}
```

> The getRuntimeObjectMapping(Object tableCondition) method is used to compute and return the runtime ORM based on the passed tableCondition. The tableCondition is the same one of Shadow Table.

> The most difficult part of implementing a CustomTableView is to build the returned POJOBasedObjectMapping, as it requires you to understand the mechanism inside guzz. Fortunately, guzz provides a abstract base class org.guzz.orm.AbstractCustomTableView to ease this job. Your CustomTableView can inherit from it.

> For our cargo, we write its CustomTableView named CargoCustomTableView:

```
/**
 * Key class. Mapping different cargo to different table and different properties.
 * 
 * <p>We define cargo's name to be the table condition.</p>
 */
public class CargoCustomTableView extends AbstractCustomTableView {
	
	/**
	 * Lookup mapping every time is expensive, so we cache it.
	 * <p>
	 * In a real production system, you should replace it with a real cache, and refresh it on special properties' changing.
	 * <br>
	 * The real cache can be started and shut down in the {@link #startup()} and {@link #shutdown()} methods(don't forget to call super).
	 * <p/>
	 */
	private Map orms_cache = new HashMap() ;

	public void setCustomPropertyValue(Object beanInstance, String propName, Object value) {
		Cargo c = (Cargo) beanInstance ;
		
		//store the special property in the map.
		c.getSpecialProps().put(propName, value) ;
	}
	
	public Object getCustomPropertyValue(Object beanInstance, String propName) {
		Cargo c = (Cargo) beanInstance ;
		
		//return special property from the map.
		return c.getSpecialProps().get(propName) ;
	}

	public POJOBasedObjectMapping getRuntimeObjectMapping(Object tableCondition) {
		String cargoName = (String) tableCondition ;
		Assert.assertNotEmpty(cargoName, "tableCondition can't be null, we don't have any default table to store un-categoried cargoes.") ;
		
		POJOBasedObjectMapping map = (POJOBasedObjectMapping) this.orms_cache.get(cargoName) ;
		
		if(map == null){
			//create it
			map = super.createRuntimeObjectMapping(cargoName) ;
			
			//cache
			this.orms_cache.put(cargoName, map) ;
		}
		
		return map ;
	}

	protected void initCustomTableColumn(POJOBasedObjectMapping mapping, Object tableCondition) {
		String cargoName = (String) tableCondition ;
		
		//load special properties for this cargo from master database.
		ReadonlyTranSession session = this.guzzContext.getTransactionManager().openNoDelayReadonlyTran() ;
		
		try{
			SearchExpression se = SearchExpression.forLoadAll(SpecialProperty.class) ;
			se.and(Terms.eq("cargoName", cargoName)) ;
			
			List properties = session.list(se) ;
			
			for(int i = 0 ; i < properties.size() ; i++){
				SpecialProperty sp = (SpecialProperty) properties.get(i) ;
				
				//create the TableColumn with the super helper method.
				TableColumn tc = super.createTableColumn(mapping, sp.getPropName(), sp.getColName(), sp.getDataType(), null) ;
				
				//add it to the mapping with the super helper method too.
				super.addTableColumn(mapping, tc) ;
			}
			
		}finally{
			session.close() ;
		}
	}
	
	public String toTableName(Object tableCondition) {
		String cargoName = (String) tableCondition ;
		Assert.assertNotEmpty(cargoName, "tableCondition can't be null, we don't have any default table to store un-categoried cargoes.") ;
				
		//different tableConditions mapped to different tables.
		return "tb_cargo_" + cargoName;
	}

}
```

> setCustomPropertyValue and getCustomPropertyValue are used to store and load special properties of a domain object. For cargo, we store it in specialProps.

> getRuntimeObjectMapping is used to build the ORM, and we delegate it to super.createRuntimeObjectMapping().

> initCustomTableColumn is a method called back from the super.createRuntimeObjectMapping(). It is used to load and build special properties of a domain object. In our example, we load SpecialProperty(s) from database, build TableColumn by super.createTableColumn, and add it to the ORM by super.addTableColumn.

> toTableName is derived from ShadowTableView to compute the underly table name.

## User Case: Prepare to Run ##

> guzz never handles with table structures, so we have to create tables by ourself. Suppose we have two cargoes, book and crossStitch in our shopping system.

> The book has 3 special properties: ISBN, author and publisher; and the crossStitch has 4 special properties: gridNum, backColor, size and brand.

> We need three tables for this, tb\_s\_property to store the SpecialProperty, tb\_cargo\_book to store books, and tb\_cargo\_crossStitch to store crossStitches.

> Initialize the tables:

```
                //create special property table in H2 database.
                executeUpdate(H2Conn, "drop table if exists tb_s_property") ;
		executeUpdate(H2Conn, "create table tb_s_property(id int not null AUTO_INCREMENT primary key , 
                                cargoName varchar(32), propName varchar(32), colName varchar(32), dataType varchar(32))") ;
				
		//add book and cross-stitch's special properties' declarations into tb_s_property.
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('book', 'ISBN','ISBN','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('book', 'author','author','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('book', 'publisher','publisher','string')") ;
		
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'gridNum','gridNum','int')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'backColor','backColor','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'size','size','string')") ;
		executeUpdate(H2Conn, "insert into tb_s_property(cargoName, propName, colName, dataType) values('crossStitch', 'brand','brand','string')") ;
		
		
		//create table for cargo book and cargo cross-stitch.
		//we know the rule is : return "tb_cargo_" + cargoName;
		//cargo book:
		executeUpdate(H2Conn, "drop table if exists tb_cargo_book") ;
		executeUpdate(H2Conn, "create table tb_cargo_book(id int not null AUTO_INCREMENT primary key , name varchar(128), description text, 
                                       storeCount int(11), price double, onlineTime datetime" +
				", ISBN varchar(64) not null" +
				", author varchar(64)" +
				", publisher varchar(64)" +
				")") ;

		//cargo cross-stitch:
		executeUpdate(H2Conn, "drop table if exists tb_cargo_crossStitch") ;
		executeUpdate(H2Conn, "create table tb_cargo_crossStitch(id int not null AUTO_INCREMENT primary key , name varchar(128), description text, 
                                      storeCount int(11), price double, onlineTime datetime" +
				", gridNum int(11) not null" +
				", backColor varchar(64)" +
				", size varchar(64)" +
				", brand varchar(64)" +
				")") ;
```

> We created the three tables, and insert special properties into tb\_s\_property for book and cross stitch. Then, we have to declare business Cargo and SpecialProperty in guzz.xml:

```
	<business name="cargo" dbgroup="default" file="classpath:org/guzz/test/shop/Cargo.hbm.xml" />
	<business name="sp" dbgroup="default" file="classpath:org/guzz/test/shop/SpecialProperty.hbm.xml" />
```

## User Case: Insert a cargo ##

> To persist a custom object, it is similar to a shadow one discussed in the previous chapter. Set property values, set tableCondition, and call persist API.

> For example, to save a book, you would write something like:

```
                        WriteTranSession session = tm.openRWTran(true) ;

			Cargo book = new Cargo() ;
			
			book.setName("book") ;
			book.setDescription("nice book ") ;
			book.setPrice(33.56) ;
			book.setStoreCount(10) ;
			
			Date now = new Date() ;
			book.setOnlineTime(now) ;
			
			//ISBN, author and publisher
			book.getSpecialProps().put("ISBN", "isbn-bbb-1") ;
			book.getSpecialProps().put("author", "not me") ;
			book.getSpecialProps().put("publisher", "wolf") ;
			
			Guzz.setTableCondition("book") ;			
			session.insert(book) ; //insert into table tb_cargo_book
```

## User Case: Search a cross-stitch ##

```
		se = SearchExpression.forClass(Cargo.class) ;
		se.and(Terms.eq("brand", "湘湘绣铺")); //set query conditions on a special property
                se.setTableCondition("crossStitch") ; //set tableCondition

                List m_css = session.list(se) ; //select table tb_cargo_crossStitch
```

> If you are listing crossStitches in a jsp file, it is more convenient to query by guzz taglib:

```
<g:list var="m_css" business="cargo" tableCondition="crossStitch" limit="brand=湘湘绣铺" />
```

## User Case: Join & Union Query ##

> In a custom/shadow table, data is distributed in many unknown small tables. This is good for performance and special features, but sometimes, we need to query over them, such as list all cargoes priced over 100.00 in our case. Join or Union Queries over more than one table can only be archived by sqls in guzz.

> To execute a sql in guzz, what you need is to obtain a ORM for the sql. In a custom table, ORM is returned by public POJOBasedObjectMapping getRuntimeObjectMapping(Object tableCondition), so this method must support returning a ORM for join & union queries first. Here, we define a special tableCondition "all", means that a ORM for multiple-table queries is required.

> Modify getRuntimeObjectMapping method to support "all" condition:

```
        .....

	public POJOBasedObjectMapping getRuntimeObjectMapping(Object tableCondition) {
		String cargoName = (String) tableCondition ;
		
		if("all".equals(cargoName)){
			return this.getConfiguredMapping() ;
		}
		
		Assert.assertNotEmpty(cargoName, "tableCondition can't be null, we don't have any default table to store un-categoried cargoes.") ;
		
		.....
```

> If the tableCondition is "all", we use the default ORM configured in hbm.xml or annotation (without any cargo's special properties).

> Query cargoes priced over 100.00 bucks:

```
		ReadonlyTranSession session = tm.openDelayReadTran() ; 
		
		Guzz.setTableCondition("all") ;
		
		//list all cargoes priced over 100.00 bucks
		String sql="select c.* from (select @id, @name, @storeCount from tb_cargo_book where @price >= :param_price"
		       + " union all select @id, @name, @storeCount from tb_cargo_crossstitch where @price >= :param_price) as c ";

		CompiledSQL cs = tm.getCompiledSQLBuilder().buildCompiledSQL(Cargo.class, sql) ; 
		cs.addParamPropMapping("param_price", "price") ;
		List cargoes = session.list(cs.bind("param_price", 100.00), 1, 1000).size() ;

                session.close();
```

> In the union sql statement, @propName is used to replace column name, but the table names must be the actual ones.

> ## Startup sequences of CustomTableView: ##

  * 1. Parse configuration file and instance the CustomTableView class;

  * 2. call setConfiguredTableName(String tableName) to set the configured table name;

  * 3. call setConfiguredObjectMapping(POJOBasedObjectMapping configuredMapping) to set the configured OR-Mapping;

  * 4. call setGuzzContext(GuzzContex guzzContext) if the GuzzContextAware interface is declared;

  * 5. call startup();

  * 6. call setExtendedBeanFactory(ExtendedBeanFactory factory) if the ExtendedBeanFactoryAware interface is declared;

  * 7. running....;

  * 8. call shutdown() when guzz exits.

## Notes: ##

  * 1. Custom Table doesn't support ORM defined in guzz.xml in ibatis's style. It means you must define your business class's ORM in hbm.xml or annotation if you want it to be a custom one.

  * 2. For sqls defined in guzz.xml, you can set its "orm" attribute to the domain class's business name to borrow the ORM in hbm.xml, and active custom table support.

  * 3. As ShadowTableView, small tables should be created on your own. Guzz won't create that for you.
