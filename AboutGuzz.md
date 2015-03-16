| [English](http://code.google.com/p/guzz/wiki/AboutGuzz?wl=en) | [简体中文](http://code.google.com/p/guzz/wiki/AboutGuzz?wl=zh-Hans) |
|:--------------------------------------------------------------|:------------------------------------------------------------------------|

## Overview ##

> Guzz is a data-layer framework designed for busy and large systems running in REAL world (not just demos) with many relational-databases as storages.

**For database solution, guzz provides 6 features:**

  * 1.	You have too many tables in a database, or they took too much resource for one machine. Distribute them in different database machines with dbgroup in chapter 3: [TutorialGuzzXml](TutorialGuzzXml.md)

  * 2.	Some table is too big. Spit them into small ones with ShadowTable in chapter 16: [TutorialShadowTable](TutorialShadowTable.md)

  * 3.	Your business is special, for example a shopping site; each table may/should own its special columns. Ok, split them with CustomTable in chapter 17: [TutorialCustomTable](TutorialCustomTable.md)

  * 4.	Big tables are split, but the split small tables are too many or too big in total to store together. Distribute the small tables in different database machines with VirtualDB in chapter 18: [TutorialVirtualDB](TutorialVirtualDB.md)

  * 5.	Some column is too big to store in the database, or should be treated very special. Store it in File System, in memcached, or in anything you like with CustomColumnLoader in chapter 12: [TutorialLazyLoad](TutorialLazyLoad.md)

  * 6.	The system is really very very busy, a single database is absolutely impossible to achieve your mission. Ok, deploy more READ machines with the native read-write separation support in guzz.

> Guzz can work with hibernate and ibatis, and can also work alone. If your system meets something of lacking database-scale ability, deploy more database machines and introduce guzz to upgrade database-scale related modules.

> Guzz also has some features for easing programming and maintaining. For example: Different API for trading between programming efficiency and low-lever resource controlling, Service extension, Central Configuration Extension support, Database oriented Taglib, and so on.

> Guzz is a small framework.

> If you can't find information here, join our [mailing list](https://lists.sourceforge.net/lists/listinfo/guzz-mail-users) and ask us.

## Doc & Help ##

  * [Getting Start: write a Message-Board system running on 5 database machines](Lesson120.md)

  * [Developer Guide](TutorialStart.md)

  * [Manual for Guzz JSP Taglib](TaglibBook.md)

  * [Why design taglib?](ArchitectureMMVC.md)

  * [User FAQ](AboutFAQ.md)

## Samples ##

  * [List of demos](DemosList.md)

## Feedback ##

  * [Feedback & Suggestions](AboutFeedback.md)

# Things guzz won't do: #

  * Create or update table structures.

  * Synchronize data between different databases and different machines.

  * Support relationships between business classes such as one-one, one-many, many-many, and so on. Join, Union and other complex sqls can only be executed in ibatis's way.
