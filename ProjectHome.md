| [English](http://code.google.com/p/guzz/wiki/AboutGuzz?wl=en) | [简体中文](http://code.google.com/p/guzz/wiki/HomePageCn?wl=zh-Hans) |
|:--------------------------------------------------------------|:-------------------------------------------------------------------------|

Guzz [ˈɡuzi] is a java object-relational mapping framework that will let you use many databases in one system.

Most of time, it is used to split tables and distribute tables in many database machines when you meet database bottle-necks.

Mail List: https://lists.sourceforge.net/lists/listinfo/guzz-mail-users

## Why not hibernate? ##

  * Hibernate is not good at handling several databases to work together. If you need to use more than one database in hibernate, you have to build many Hibernate instances, rewrite code, and handle the transaction between different databases yourself. Guzz can achieve this in a simple configuration file without changing any source code.

  * Complicated sqls are hard to control in hibernate. IBatis is popular for that. Guzz also allow you to write sqls in configuration file the way IBatis did.

  * Hibernate Shard is not good at Shard and also too complicated with too many limits. Simplicity is the biggest advantage of guzz's sharding. To use it, you reply on your own sharding rules coded in java, declarations in a xml file, and DBAs to prepare the databases. Things are straightforward. There is little to learn that you don't already know.

  * Hibernate's batch API is hidden from the developers. If you have to insert 1000000 records, you are in trouble or have to be advanced with hibernate. Guzz wraps JDBC's batch API in both object-oriented and direct sql's way. Both are easy to control and with the full power of the JDBC API.

## Why not IBatis/MyBatis? ##

  * IBatis is not good at handling several databases to work together.

  * IBatis is not good at Database Sharding.

  * IBatis has too many configurations. To use ibatis, you have to write many xml files inefficient even just to insert a simple record. Guzz enables you to new an object and insert that object into the database as the Hibernate did. For most database operations in your project, like insert/update/delete a record or some simple queries, guzz provides you the Hibernate's way. For complicated sqls where IBatis is great for, Guzz is not bad too.

  * IBatis doesn't support physical pagination.

  * IBatis doesn't support runtime sql. You change a sql, you have to restart your application. With guzz, you can store your sqls anywhere, reload them anytime.

  * Guzz's batch API is clear and straightforward than IBatis's.

## Features: ##

With guzz, you can split and store your tables among several database machines; add physical machines to improve database performance without changing a single line of code; you can split a big table into many small ones, and each small table can own its special columns for some applications like shopping sites; you can store some columns in the File System; you can refactor your public modules to Services for better reusing; you can query databases through jsp taglib; you can manage all your applications' configurations in a central system; you can choose to persist a object in hibernate's ORM way, or ibatis’s direct sql; you can load sqls from other systems and secure them; and you will also find writing batch sqls much easier. In a word, you can do much useful stuff other frameworks haven’t mentioned.

Guzz's features:

  * Modern design, based on both ibatis and hibernate's popular features.

  * Support your system to use many database machines without extra codes, and separate read-write between those machines.

  * Support your system to store different tables in different databases, and maintain a distributed transaction automatically (Shard).

  * Support your system to split a big table into many small ones on your rules (Shadow), and even allow each table owns its special columns (Custom).

  * Support the split small tables distributed in different database groups (VirtualDB).

  * Better batch API. It is easier to use and easier to control than any other frameworks.

  * For simple situation, you can persist, mapping and query data in a object-oriented way like hibernate (80%).

  * For some complicated operations, you can store complicated sqls in a xml configuration file, and let dbas involved in, like ibatis (20%).

  * Over ibatis, you can store and load sqls from anywhere (or any system), use it on demand, and enables you to add/delete/test/tune sqls online without a restart of applications. (We call this "Dynamic SQL")

  * Support unified POJO styled usage for data with very special conjunction (over "Normal Form"), or between unstructured data (such as from other systems). For example, you are calling book.getPrice(), and the price can actually be read from Amazon.com by a Web Service call, while other properties like book name, ISBN, author are read from your own database.

  * Support Service-oriented architecture; help you to accumulate a fundamental services system. (Yes, not build a fundamental system from scratch, but accumulate one from existing and new projects.)

  * Support object-oriented database JSP Taglib for rapid development. You can build pages without writing any java codes.

  * Support for Configuration System. You can manage the configurations of all your systems in a central system online.

> Guzz can also work with hibernate and ibatis.

**Getting Start:** http://code.google.com/p/guzz/wiki/AboutGuzz?tm=6

## roadmap ##

**release note**:

1.3.1 build20120714
  1. new feature: templated sql. concate sqls on the passed parameters.
  1. new feature: business package scan
  1. new feature: new JNDI datasource support
  1. new feature: escaping special characters in sqls
  1. new feature: new attribute "version" support in hbm.xml

1.3.0 build20120222
  1. bug fix: DebugService
  1. improvement: resource listing file
  1. other bug fixes.

1.3.0 build20111123
  1. fix some bugs.
  1. new feature: supports Spring Declaration Transaction.
  1. new feature: Supports Connection Isolations and query timeouts.

1.2.9 build20110511:
  1. fix some bugs.
  1. new ExecuteService for rpc future call.
  1. use Thread.isDemon() to determine "demon" thread for sql outputs.
  1. package source code in the default guzz.jar.

1.2.9 build20110210:
  1. store shadowed small tables in different machines.
  1. record SQL execution time in nano-seconds
  1. a new attribute “package” is supported in hbm.xml
  1. the LogService is changed to InsertQueueService
  1. fix a few bugs.

1.2.9 build20101212:
  1. add a new attribute "orderBy" in g:get taglib.
  1. add a handy method in batch operations for shadow-table.
  1. New feature in slowUpdateServer service--notification of record-not-found in main db.
  1. fix a few bugs.

1.2.9 build20101021:
  1. new feature: support a service to depend on other services.
  1. add a new Id generator--random id generator(generate a secure id).
  1. fix some bugs.

1.2.8 beta2:
  1. support dynamic sql(say: Before this you define sqls in ibatis's config file; and from now, you can define sqls in anywhere(eg: database), and put the new sqls online dynamically.).
  1. support 2 new rpc protocols: hessian, burlap.
  1. support JDK5's enum.
  1. allow pass a parameter to enum and date/time related data types.
  1. support escape character `````````` for column name.

1.2.8 beta1:
  1. limited(but enough for guzz) JPA annotation support.
  1. add a new Id generator hilo-multi.
  1. add byte data type support.
  1. fix a bug in "hilo" id generator.

1.2.7 final build20100406:
  1. fix a few bugs

1.2.7 rc2:
  1. fix bug: fail to shut down the connection pool when guzz closed.
  1. new: add long data type support as bigint
  1. improvement: others

1.2.7 rc1:
  1. fix bug: guzz taglib doesn't support custom property in limit
  1. improvement: move IDataTypeHandler to common package.
  1. improvement: call setExtendedBeanFactory after spring's full starting.

1.2.7 beta4:
  1. add support for id generator: hilo, seqhilo
  1. change default sequence name from guzzSeq to guzz\_sequence
  1. add support for Service/ShadowTableView/CustomTableView/ColumnDataLoader to fetch outside reference(eg: spring ApplicationContext).
  1. change guzz with spring's integrate configurations.

1.2.7 beta3:
  1. custom table support.
  1. fix bug: ReadonlySession.refresh() should throw a exception if the record doesn't exsit, but not.

1.2.7 beta2:
  1. Shadow Table
  1. Asynchronous Service

1.2.7 beta1:
  1. draft

1.2.6：

  1. support clob/blob/decimal/byte data type
  1. new api for writing with upgrade lock
  1. supply new custom loaders for clob and blob

....