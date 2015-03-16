## ORM by package scanning ##

> To declare mappings for multiple businesses, business-scan can be used.

> For example:

```

	<business-scan resources="classpath*:/org/guzz/test/*.class" />
	<business-scan dbgroup="cargoDB" resources="classpath*:org/guzz/test/shop/*.hbm.xml" />
	
	<a-business name="article" dbgroup="appDB" class="xxx.xxx.Article" />
```

> In the above example, guzz scan all files matching classpath`*`:/org/guzz/test/`*`.class and classpath`*`:org/guzz/test/shop/`*`.hbm.xml, and load them as mapping files.

> The scanned mapping files could be a hbm.xml file, or a java annotation class; it can be existed as a single file, or be packaged in a jar.

> business-scan is declared similar to business and a-business elements in guzz.xml.

> business-scan is a new feature in guzz1.3.1, and spring-core library is required.

> You can write more than one business-scan elements, and guzz will load them in the sequence of head to tail written in the guzz.xml file. If a business is included multiple times in the business-scans, only the first time is valid, the others will be ignored.

> Businesses loading sequences in the same business-scan are undefined.

> A business declared in a business or a-business element has a higher priority over the one loaded by business-scan. As a result, you can use business-scan to load all businesses for simplicities, and override some of them as needed with business and a-business declarations.


> business-scan has two attributes:

| **attribute** | **Required** | **note** |
|:--------------|:-------------|:---------|
| resources | Required  | The files to scan. Guzz uses spring's PathMatchingResourcePatternResolver to match files, for pattern details please read:[PathMatchingResourcePatternResolver](http://static.springsource.org/spring/docs/2.5.x/api/org/springframework/core/io/support/PathMatchingResourcePatternResolver.html) |
| dbgroup | Optional | The database group to store the scanned businesses. Use dbGroup declared in the scanned file(hbm.xml or java annotation) in default. Override all businesses's dbgroups to this value if this attribute is set here. |


## Cautions and samples ##

> The business's name is MUST declared in the hbm.xml file or the java annotated class, or package-scan will ignore it as a invalid mapping file.

> Samples:

> java annotation:
```

@javax.persistence.Entity 
@org.guzz.annotations.Entity(businessName="user")
@org.guzz.annotations.Table(name="tb_user", dbGroup="mainDB")
public class User implements Serializable {

...

}

```

> hbm.xml:
```

<?xml version="1.0"?>
<!DOCTYPE guzz-mapping PUBLIC "-//GUZZ//GUZZ MAPPING DTD//EN" "http://www.guzz.org/dtd/guzz-mapping.dtd">
<hibernate-mapping>
    <class name="org.guzz.service.core.impl.IncUpdateBusiness" table="tb_guzz_su" businessName="guzzSlowUpdate" dbGroup="logDB">
        <id name="id" type="bigint" column="gu_id">
        	<generator class="native">
        		<param name="sequence">seq_iub_id</param>
        	</generator>
        </id>
        <property name="dbGroup" type="string" column="gu_db_group" />
        <property name="tableName" type="string" column="gu_tab_name" />
        <property name="columnToUpdate" type="string" column="gu_inc_col" />
        <property name="pkColunName" type="string" column="gu_tab_pk_col" />
        <property name="pkValue" type="string" column="gu_tab_pk_val" />
        <property name="countToInc" type="int" column="gu_inc_count" />
    </class>
</hibernate-mapping>

```


## Inside the implementation ##

**--Could be changed in the future**

> While guzz is starting, it loads all resources matching spring's PathMatchingResourcePatternResolver pattern.

> Guzz iterator the loaded resources. If the resource is readable, it will be treated as a java annotated class and interpreted as a JAP mapping file. If failed, it is treated as a hbm.xml mapping file then. If this is failed again, the resource is ignored; or it will be loaded.

> So be careful, package-scan only considers the contents of the matched files, the file names are never minded.

> If you have a declaration as resources="classpath`*`:org/guzz/test/shop/`*`", then abc.txt, abc.hbm.xml.bak, and NotUseAnyMore.class.bak will be loaded too if they existed in the org/guzz/test/shop/' folder and the contents are a mapping file or a annotated domain class.

