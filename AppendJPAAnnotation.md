# Guzz JPA Annotation Reference #

The Java Persistence API (JPA), part of the Java Enterprise Edition 5 (Java EE 5) Enterprise JavaBeans (EJB) 3.0 specification, greatly simplifies Java persistence and provides an object-relational mapping approach that allows you to declaratively define how to map Java objects to relational database tables in a standard, portable way that works both inside a Java EE 5 application server and outside an EJB container in a Java Standard Edition (Java SE) 5 application.

When using Guzz JPA, you can configure the JPA behavior of your entities using annotations. An annotation is a simple, expressive means of decorating Java source code with metadata that is compiled into the corresponding Java class files for interpretation at runtime by Guzz JPA to manage JPA behavior.

For example, to designate a Java class as a JPA entity, use the @Entity annotation as follows:

```
@javax.persistence.Entity
public class Employee implements Serializable { 
   ...
}
```

You can selectively decorate your entity classes with annotations to override  defaults. This is known as configuration by  exception.

This reference quotes extensively from the [JSR-220  Enterprise JavaBeans v.3.0](http://jcp.org/aboutJava/communityprocess/pfd/jsr220/index.html) Java Persistence API specification to summarize annotation information by category (see Table 1-1) and explains when and how you use these annotations to customize JPA behavior to meet your application requirements.

For more information, see:
  * [Index of Annotations](#IndexOfAnnotations.md)
  * [Complete JPA annotation Javadoc ](http://java.sun.com/javaee/5/docs/api/index.html?javax/persistence/package-summary.html)

## Table 1-1 JPA Annotations by Category ##

| **Category** | **Description** | **Annotations** |
|:-------------|:----------------|:----------------|
| Entity | By default, Guzz JPA assumes that a Java class is non-persistent and not eligible for JPA services unless it is decorated with this annotation. <br><br>Use this annotation to designate a plain old Java object (POJO) class as an  entity so that you can use it with JPA services.<br><br>You must designate a class as a JPA entity (either using this annotation or  the orm.xml file) before you can use the class. <table><thead><th> <a href='#@Entity.md'>@Entity</a><br><br><a href='#@org.guzz.annotations.Entity.md'>@org.guzz.annotations.Entity</a> </th></thead><tbody>
<tr><td> Database Schema Attributes</td><td>By default, Guzz JPA assumes that an entity's name corresponds to a database  table of the same name and that an entity's data member names correspond to  database columns with the same names. <br><br>Use these annotations to override this default behavior and fine-tune the  relationship between your object model and data model.</td><td><a href='#@Table.md'>@Table</a><br><br><a href='#@Column.md'>@Column</a><br><br><a href='#@org.guzz.annotations.Table.md'>@org.guzz.annotations.Table</a><br><br><a href='#@org.guzz.annotations.Column.md'>@org.guzz.annotations.Column</a> </td></tr>
<tr><td> Identity</td><td>By default, Guzz JPA assumes that each entity must have one field or property  that serves as a primary key specifed by @Id.<br><br>You can also use these annotations to fine-tune how your database maintains  the identity of your entities.</td><td><a href='#@Id.md'>@Id</a><br><br><a href='#@org.guzz.annotations.GenericGenerator.md'>@org.guzz.annotations.GenericGenerator</a><br><br><a href='#@org.guzz.annotations.GenericGenerators.md'>@org.guzz.annotations.GenericGenerators</a><br><br><a href='#@org.guzz.annotations.Parameter.md'>@org.guzz.annotations.Parameter</a><br><br><a href='#@GeneratedValue.md'>@GeneratedValue</a><br><br><a href='#@SequenceGenerator.md'>@SequenceGenerator</a><br><br><a href='#@TableGenerator.md'>@TableGenerator</a></td></tr>
<tr><td> Direct Mappings</td><td>By default, Guzz JPA automatically configures a Basic mapping  for most Java primitive types, wrappers of the primitive types, and enums. <br><br>Use these annotations to fine-tune how your database implements these  mappings.</td><td><a href='#@Basic.md'>@Basic</a><br><br><a href='#@Enumerated.md'>@Enumerated</a><br><br><a href='#@Temporal.md'>@Temporal</a> (not supported)<br><br><a href='#@Lob.md'>@Lob</a> (not  supported)<br><br><a href='#@Transient.md'>@Transient</a></td></tr>
<tr><td> Composition </td><td> Some objects cannot exist on their own, but can only be embedded within owning entities.<br><br>Use these annotations to specify objects that are embedded and to override how they are mapped in the owning entity's table. </td><td> <a href='#@AttributeOverride.md'>@AttributeOverride</a><br><br><a href='#@AttributeOverrides.md'>@AttributeOverrides</a> </td></tr>
<tr><td> Inheritance</td><td>By default, Guzz JPA assumes that all persistent fields are defined by a  single entity class. <br><br>Use these annotations if your entity class inherits some or all persistent  fields from one or more superclasses.</td><td><a href='#@MappedSuperclass.md'>@MappedSuperclass</a> </td></tr></tbody></table>

<h3>@AttributeOverride</h3>

By default, Guzz JPA automatically assumes that a subclass inherits both  persistent properties and their basic mappings as defined in a mapped  superclass.<br>
<br>
Use the @AttributeOverride annotation to customize a basic  mapping inherited from a <a href='#@MappedSuperclass.md'>#@MappedSuperclass</a> to change the <a href='#@Column.md'>#@Column</a> associated with the field or property.<br>
<br>
If you have more than one @AttributeOverride change to make, you  must use <a href='#@AttributeOverrides.md'>#@AttributeOverrides</a>.<br>
<br>
Table 1-4 lists the attributes of this annotation. For  more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/AttributeOverride.html'>API</a>.<br>
<br>
<b>Table 1-4 @AttributeOverride Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Required</td><td> The name of the field or property defined in the embedded object or mapped  superclass. </td></tr>
<tr><td> column</td><td>Required </td><td> The <a href='#@Column.md'>#@Column</a> that is being mapped to the persistent attribute. <br><br>The mapping type will remain  the same as is defined in the embeddable class or mapped  superclass. </td></tr></tbody></table>

Example 1-5 shows how to use @AttributeOverride in the entity subclass  to override the <a href='#@Column.md'>#@Column</a> defined (by default) in the <a href='#@MappedSuperclass.md'>#@MappedSuperclass</a> Employee for the basic mapping to id.<br>
<br>
With the @AttributeOverride annotation, the PartTimeEmployee table would have the id attribute  mapped to the PTEMP_ID column. Other entity subclasses of Employee would inherit the default mapping to the ID column.<br>
<br>
<b>Example 1-4 @MappedSuperclass</b>

<pre><code> @MappedSuperclass<br>
 public class Employee {<br>
	 @Id protected Integer id;<br>
	 ... <br>
 }<br>
</code></pre>

<b>Example 1-5 @AttributeOverride</b>

<pre><code>@Entity<br>
@AttributeOverride(name="id", column=@Column(name="PTEMP_ID"))<br>
public class PartTimeEmployee extends Employee {<br>
...<br>
}<br>
</code></pre>

<h3>@AttributeOverrides</h3>

If you need to specify more than one <a href='#@AttributeOverride.md'>#@AttributeOverride</a>, you must specify all your attribute overrides using a single @AttributeOverrides annotation.<br>
<br>
Table 1-5 lists the attributes of this annotation. For  more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/AttributeOverrides.html'>API</a>.<br>
<br>
<b>Table 1-5 @AttributeOverrides Attributes</b>
<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> value </td><td> Required</td><td> To specify two or more attribute overrides, set value to an  array of @AttributeOverride instances (see <a href='#@AttributeOverride.md'>#@AttributeOverride</a>). </td></tr></tbody></table>

Example 1-6 shows how to use this annotation to specify two attribute overrides.<br>
<br>
<b>Example 1-6 @AttributeOverrides</b>

<pre><code>@Entity<br>
@AttributeOverrides({<br>
@AttributeOverride(name="id", column=@Column(name="PTEMP_ID")),<br>
@AttributeOverride(name="salary", column=@Column(name="SAL"))<br>
}<br>
public class PartTimeEmployee extends Employee {<br>
	...<br>
}<br>
</code></pre>

<h3>@Basic</h3>

By default, Guzz JPA automatically configures a @Basic mapping  for most Java primitive types, wrappers of the primitive types, and enums.<br>
<br>
Use the @Basic annotation to configure the fetch type to LAZY.<br>
<br>
Table  1-6 lists the attributes of this annotation. For  more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/Basic.html'>API</a>.<br>
<br>
<b>Table 1-6 @Basic Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> fetch </td><td> Optional</td><td> Default: FetchType.EAGER. By default, Guzz JPA uses a fetch type of EAGER: this is a requirement on Guzz JPA runtime that data must be eagerly fetched.<br><br>If this is inappropriate for your application or a particular persistent field, set fetch to FetchType.LAZY: this is a hint that data should be fetched lazily when it is first accessed (if possible). For  more information, see "Lazy Loading" in the <i>Guide</i>. </td></tr></tbody></table>

Example 1-7 shows how to use this annotation to specify a fetch type of LAZY for a basic mapping.<br>
<br>
<b>Example 1-7 @Basic</b>

<pre><code>@Entity<br>
public class Book implements Serializable {<br>
...<br>
@Basic(fetch=LAZY)<br>
protected String toc;<br>
...<br>
}<br>
</code></pre>

<h3>@Column</h3>

By default, Guzz JPA assumes that each of an entity's persistent attributes  is stored in a database table column whose name matches that of the persistent  field or property.<br>
<br>
Use the @Column annotation:<br>
<ul><li>to associate a persistent attribute with a different name if the default  column name is awkward, incompatible with a pre-existing data model, or invalid  as a column name in your database<br>
</li><li>to fine-tune the characteristics of a column in your database</li></ul>

Table 1-7 lists the attributes of this annotation. For  more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/Column.html'>API</a>.<br>
<br>
<b>Table 1-7 @Column Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Optional</td><td> Default: Guzz JPA assumes that each of an entity's  persistent attributes is stored in a database table column whose name matches  that of the persistent field or property.<br><br>To specify an alternative column name, set name to the String column name you want. </td></tr>
<tr><td> unique</td><td>Optional </td><td> Not supported. </td></tr>
<tr><td> nullable</td><td>Optional </td><td> Not supported. </td></tr>
<tr><td> insertable</td><td>Optional </td><td> Default: true. <br><br>By default, Guzz JPA assumes that all columns are always included in SQL INSERT statements. <br><br>If this column should not be included in these statements, set insertable to false. </td></tr>
<tr><td>updatable</td><td>Optional </td><td> Default: true. <br><br>By default, Guzz JPA assumes that a column is always included in SQL  UPDATE statements. <br><br>If this column should not be included in these statements, set updatable to false. </td></tr>
<tr><td> columnDefinition</td><td>Optional </td><td> Not supported. You have to create your table on your  own. </td></tr>
<tr><td> table</td><td>Optional </td><td> not supported. </td></tr>
<tr><td> length</td><td>Optional </td><td> not supported. </td></tr>
<tr><td> precision</td><td>Optional </td><td> not  supported. </td></tr>
<tr><td> scale</td><td>Optional </td><td> not  supported. </td></tr></tbody></table>

Example 1-8 shows how to use this annotation to make Guzz JPA persist salary to column SAL in secondary table EMP_SAL. By default, Guzz JPA persists salary to column salary in primary table EMPLOYEE.<br>
<br>
<b>Example 1-8 @Column</b>

<pre><code>@Entity<br>
@SecondaryTable(name="EMP_SAL")<br>
public class Employee implements Serializable {<br>
...<br>
@Column(name="SAL", table="EMP_SAL")<br>
private Long salary;<br>
...<br>
}<br>
</code></pre>

<h3>@Entity</h3>

Use the @Entity annotation to designate a plain old Java object  (POJO) class as an entity and make it eligible for JPA services. You must designate a POJO class as an entity before you can use any other JPA  annotations.<br>
<br>
Table 1-11 lists the attributes of this annotation. For more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/Entity.html'>API</a>.<br>
<br>
<b>Table 1-11 @Entity Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Optional</td><td> not supported. Use org.guzz.annotations.Entity to specify the business name instead. </td></tr></tbody></table>

Example 1-18 shows how to use this annotation.<br>
<br>
<b>Example 1-18 @Entity</b>

<pre><code>@Entity<br>
public class Employee implements Serializable {<br>
	...<br>
}<br>
</code></pre>

<h3>@Enumerated</h3>

By default, Guzz JPA persists the ordinal values of enumerated constants.<br>
<br>
Use the @Enumerated annotation to specify whether Guzz JPA should persist ordinal or String values of enumerated constants if the String value suits your application requirements or to match an existing  database schema.<br>
<br>
This annotation can be used with <a href='#@Basic@Basic.md'>#@Basic@Basic</a>.<br>
<br>
Table 1-14 lists the attributes of this annotation.  For more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/Enumerated.html'>API</a>.<br>
<br>
<b>Table 1-14 @Enumerated Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> value </td><td> Optional</td><td> Default: EnumType.ORDINAL.<br><br>By default, Guzz JPA assumes that for a property or field mapped to an enumerated constant, the ordinal value should be persisted. <br><br>In Example 1-26, the ordinal value of EmployeeStatus is written to the database when Employee is persisted.<br><br>If you want the String value of the enumerated constant persisted, set value to EnumType.STRING. </td></tr></tbody></table>

Given the enumerated constants in Example 1-25, Example 1-26 shows how to use this annotation to specify that the String value of SalaryRate should be written to the database when Employee is persisted. By default, the ordinal value of EmployeeStatus is written to the database.<br>
<br>
<b>Example 1-25 Enumerated Constants</b>

<pre><code>public enum EmployeeStatus {FULL_TIME, PART_TIME, CONTRACT}<br>
public enum SalaryRate {JUNIOR, SENIOR, MANAGER, EXECUTIVE}<br>
</code></pre>

<b>Example 1-26 @Enumerated</b>

<pre><code>@Entity<br>
public class Employee {<br>
	...<br>
<br>
	public EmployeeStatus getStatus() {<br>
    ...<br>
    }<br>
<br>
	@Enumerated(STRING)<br>
	public SalaryRate getPayScale() {<br>
	...   <br>
	}<br>
<br>
	...<br>
}<br>
</code></pre>

<h3>@GeneratedValue</h3>

By default, the application is responsible for supplying and setting entity  identifiers (see <a href='#@Id@Id.md'>#@Id@Id</a>).<br>
<br>
Use the @GeneratedValue annotation if you want Guzz JPA to  provide and manage entity identifiers.<br>
<br>
Table 1-16 lists the attributes of this annotation. For more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/GeneratedValue.html'>API</a>.<br>
<br>
<b>Table 1-16 @GeneratedValue Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> strategy </td><td> Optional</td><td> Default: GenerationType.AUTO.By default, Guzz JPA chooses the type of primary key generator that is most  appropriate for the underlying database.If you feel that another generator type is more appropriate for your database  or application, set strategy to the GeneratorType you  want:<br><br> IDENTITY - specify that Guzz JPA use a database identity  column<br><br> AUTO - specify that Guzz JPA should choose a primary key  generator that is most appropriate for the underlying database.<br><br> SEQUENCE - specify that Guzz JPA use a database sequence (see <a href='#@SequenceGenerator)<br><br>.md'>TABLE - specify that Guzz JPA assign primary keys for the entity  using an underlying database table to ensure uniqueness (see [#@TableGenerator</a>)<br><br> </td></tr>
<tr><td> generator</td><td>Optional </td><td> Default:Guzz JPA assigns a name to the primary key  generator it selects.  <br><br>If this name is awkward, a reserved word, incompatible with a pre-existing  data model, or invalid as a primary key generator name in your database, set generator to the String generator name you want to  use. <br><br>If you want to use a expanded Generator in guzz, set the generator name you  want to use, and defined a @org.guzz.annotations.GenericGenerator with that  name. </td></tr></tbody></table>

Example 1-33 shows how to use this annotation to tell Guzz JPA to use a primary key generator of type GeneratorType.SEQUENCE named CUST_SEQ.<br>
<br>
<b>Example 1-33 @GeneratedValue</b>

<pre><code>@Entity<br>
public class Employee implements Serializable {<br>
	...<br>
	@Id<br>
	@GeneratedValue(strategy=SEQUENCE, generator="CUST_SEQ")<br>
	@Column(name="CUST_ID")<br>
	public Long getId() {<br>
			return id;<br>
	}<br>
<br>
	...<br>
}<br>
</code></pre>

<h3>@Id</h3>

Use the @Id annotation to designate one or more persistent  fields or properties as the entity's primary key.<br>
<br>
This annotation has no attributes. For more  details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/Id.html'>API</a>.<br>
<br>
Example 1-34 shows how to use this annotation to designate persistent field empID as the primary key of the Employee table.<br>
<br>
<b>Example 1-34 @Id</b>

<pre><code>@Entity<br>
public class Employee implements Serializable {<br>
	@Id<br>
	private int empID;<br>
	...<br>
}<br>
</code></pre>

<h3>@Lob</h3>

@Lob is <b>not</b> supported in guzz.<br>
<br>
For a Clob, use @org.guzz.annotations.Column, and set type to "clob".<br>
<br>
For a Blob, use @org.guzz.annotations.Column, and set type to "blob".<br>
<br>
<h3>@MappedSuperclass</h3>

By default, Guzz JPA assumes that all the persistent fields of an entity are  defined in that entity.<br>
<br>
Use the @MappedSuperclass annotation to designate a superclass  from which your entity class inherits persistent fields. This is a convenient  pattern when multiple entity classes share common persistent fields or  properties.<br>
<br>
You can use the <a href='#@AttributeOverride.md'>#@AttributeOverride</a> annotation in the subclass to override the superclass's mapping  configuration.<br>
<br>
This annotation has no attributes. For more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/EmbeddableSuperclass.html'>API</a>.<br>
<br>
Example 1-51 shows how to use this annotation to specify Employee as a mapped superclass. Example 1-52 shows how to extend this superclass in an entity and how to use <a href='#@AttributeOverride.md'>#@AttributeOverride</a> in the entity class to override configuration made in the superclass.<br>
<br>
<b>Example 1-51 @MappedSuperclass</b>

<pre><code>@MappedSuperclass<br>
public class Employee {<br>
	@Id<br>
	protected Integer empId;<br>
<br>
	protected Integer version;<br>
	...<br>
}<br>
</code></pre>

<b>Example 1-52 Extending a @MappedSuperclass</b>

<pre><code>@Entity<br>
@AttributeOverride(name="address", column=@Column(name="ADDR_ID"))<br>
public class PartTimeEmployee extends Employee {<br>
<br>
	@Column(name="WAGE")<br>
	protected Float hourlyWage;<br>
	...<br>
}<br>
</code></pre>

<h3>@SequenceGenerator</h3>

If you use the <a href='#@GeneratedValue.md'>#@GeneratedValue</a> annotation to specify a primary key generator of type SEQUENCE(create the sequence in the database yourself.), then you can use the @SequenceGenerator annotation to fine tune this primary key  generator to:<br>
<ul><li>change the allocation size to match your application requirements or database performance parameters<br>
</li><li>the initial value is not supported.<br>
</li><li>use a pre-defined sequence in an existing data model</li></ul>

Table 1-42 lists the attributes of this annotation. For more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/SequenceGenerator.html'>API</a>.<br>
<br>
<b>Table 1-42 @SequenceGenerator Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Required</td><td> The name of the SequenceGenerator must match the generator name  in a GeneratedValue with its strategy set to SEQUENCE. </td></tr>
<tr><td> sequenceName</td><td>Optional </td><td> Default: guzz_sequence. <br><br>If you prefer to use an existing or pre-defined sequence or have a specific  requirement for the name of the sequence, set sequenceName to the String name you want. </td></tr>
<tr><td> initialValue</td><td>Optional </td><td> not supported. Create the sequence yourself. </td></tr>
<tr><td> allocationSize</td><td>Optional </td><td> not supported. Create the sequence yourself. </td></tr></tbody></table>

Example 1-78 shows how to use this annotation to specify the allocation size for the SEQUENCE primary key generator named CUST_SEQ.<br>
<br>
<b>Example 1-78 @SequenceGenerator</b>

<pre><code>@Entity<br>
public class Employee implements Serializable {<br>
	...<br>
	@Id<br>
	@SequenceGenerator(name="CUST_SEQ", allocationSize=25)<br>
	@GeneratedValue(strategy=SEQUENCE, generator="CUST_SEQ")<br>
	@Column(name="CUST_ID")<br>
	public Long getId() {<br>
		return id;    <br>
	}<br>
	...<br>
}<br>
</code></pre>

<h3>@Table</h3>

By default, Guzz JPA assumes that all the persistent fields of an entity are stored in a single database table whose name is unqualified name of the entity class.<br>
<br>
Use the @Table annotation to specify the primary table  associated with an entity if:<br>
<ul><li>the entity name is awkward, a reserved word, incompatible with a pre-existing  data model, or invalid as a table name in your database<br>
</li><li>you need to control what catalog or schema the table belongs to</li></ul>

Table 1-45 lists the attributes of this annotation. For more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/Table.html'>API</a>.<br>
<br>
<b>Table 1-45 @Table Attributes</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Optional</td><td> Default: Guzz JPA assumes that an entity's database table has the same name as the entity class. In Example 1-84, the default name is Employee. <br><br>If the entity class name is awkward, a reserved word, or incompatible with a pre-existing data model, set name to the appropriate database table name. In Example 1-84, Guzz persists the entity class Employee in the database table named EMP. </td></tr>
<tr><td> catalog </td><td> Optional </td><td> Default: Guzz JPA uses whatever the default catalog is for your database. <br><br>If the default catalog is inappropriate for your application, set the catalog to the String catalog name to  use. </td></tr>
<tr><td> schema </td><td> Optional </td><td> Default: Guzz JPA uses whatever the default schema is for your database. <br><br>If the default schema is inappropriate for your application, set the schema to the String schema name to use. </td></tr>
<tr><td> uniqueConstraints </td><td> Optional </td><td> Not supported. </td></tr></tbody></table>

Example 1-84 shows how to use this annotation to specify the primary table name.<br>
<br>
<b>Example 1-84 @Table</b>

<pre><code>@Entity<br>
@Table(name="EMP")<br>
public class Employee implements Serializable {<br>
	...<br>
}<br>
</code></pre>

<h3>@Temporal</h3>

Not supported.<br>
<br>
For the java.sql.Timestamp, use @org.guzz.annotations.Column, and set type to "datetime" .<br>
<br>
For the java.sql.Date, use @org.guzz.annotations.Column, and set type to  "date" .<br>
<br>
For the java.sql.Time, use @org.guzz.annotations.Column, and set type to "time" .<br>
<br>
<h3>@Transient</h3>

By default, Guzz JPA assumes that all the fields of an entity that are neither transient nor static are persistent if the data type is one of: Java primitive types, java.lang.String, wrappers of the primitive types, java.math.BigInteger, java.math.BigDecimal, java.util.Date, java.sql.Blob, java.sql.Clob, java.util.Calendar, java.sql.Date, java.sql.Time, java.sql.Timestamp, user-defined guzz types, byte<a href='.md'>.md</a>, Byte<a href='.md'>.md</a>, byte, enum, org.guzz.pojo.lob.TranClob and org.guzz.pojo.lob.TranBlob.<br>
<br>
Use the @Transient annotation to specify a field or property of an entity that is not persistent, for example, a field or property that is used at run time but that is not part of the entity's state.<br>
<br>
Guzz JPA will not persist for a property or field annotated as @Transient.<br>
<br>
This annotation can be used within classes denoted by <a href='#@Entity.md'>#@Entity</a>, <a href='#@MappedSuperclass.md'>#@MappedSuperclass</a>.<br>
<br>
This annotation has no attributes. For more details, see the <a href='http://java.sun.com/javaee/5/docs/api/javax/persistence/Transient.html'>API</a>.<br>
<br>
Example 1-87 shows how to use this annotation to specify Employee field currentSession as not persistent. Guzz JPA will not persist this field.<br>
<br>
<b>Example 1-87 @Transient</b>

<pre><code>@Entity public class Employee {<br>
	@Id int id;    <br>
	@Transient Session currentSession;   <br>
	<br>
	...<br>
<br>
}<br>
</code></pre>

<h2>Table 2-1 Guzz extended annotations</h2>

<h3>@org.guzz.annotations.Entity</h3>

Use guzz Entity to enchance a domain class to a business domain class.<br>
<br>
<b>Table 2-2 GuzzEntity</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> businessName </td><td> required</td><td> A unique name referenced to the domain class. </td></tr>
<tr><td> interpreter </td><td> Optional </td><td> the interpretor for the domain class.</td></tr></tbody></table>

Example 2-3 shows how to use this annotation to specify the business's name.<br>
<br>
<b>Example 2-3 @GuzzEntity</b>

<pre><code>@javax.persistence.Entity<br>
@org.guzz.annotations.Entity(businessName="comment")<br>
public class Comment {<br>
	....<br>
}<br>
</code></pre>

<h3>@org.guzz.annotations.Table</h3>

<b>Table 2-4 GuzzTable</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Optional</td><td> The name of the table. Defaults to the <a href='#@Table.md'>@Table</a>'s name.<br><br>For simpifying annotation when you don't want to write <a href='#@Table.md'>@Table</a>. </td></tr>
<tr><td> dbGroup </td><td> Optional </td><td> Default: the 'default' database group. <br><br>The database group to store the table.</td></tr>
<tr><td> shadow </td><td> Optional </td><td> Default: no shadow.<br><br>The policy to split the table. </td></tr>
<tr><td> dynamicUpdate </td><td> Optional </td><td> Default: false. <br><br>If you only want to update the changed properties in update() operation, set this to true. </td></tr></tbody></table>

Example 2-5 shows how to use this annotation to specify the primary table name.<br>
<br>
<b>Example 2-5 @GuzzTable</b>

<pre><code>@javax.persistence.Entity<br>
@org.guzz.annotations.Entity(businessName = "comment")<br>
@Table(name="TB_COMMENT", shadow=CommentShadowView.class, dynamicUpdate=false, dbGroup="commentDB")<br>
public class Comment {<br>
	....<br>
}<br>
</code></pre>

<h3>@org.guzz.annotations.Column</h3>

Use Guzz Column to enchance the <a href='#@Column.md'>@Column</a> annotation.<br>
<br>
<b>Table 2-6 GuzzColumn</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> nullValue </td><td> Optional</td><td> The value returned when the stored value in the database is null. </td></tr>
<tr><td> loader </td><td> Optional </td><td> The user-defined loader to fetch the property's value. </td></tr>
<tr><td> type </td><td> Optional </td><td> The data type of the column. <br />eg:string, int, varchar, TranBlob </td></tr></tbody></table>

Example 2-7 shows how to use this annotation to specify the primary table name.<br>
<br>
<b>Example 2-7 @GuzzColumn</b>

<pre><code>@javax.persistence.Entity<br>
@org.guzz.annotations.Entity(businessName="userInfo")<br>
@org.guzz.annotations.Table(name="TB_USER_INFO")<br>
public class UserInfo {	<br>
<br>
	@javax.persistence.Id<br>
	@javax.persistence.Column(name="pk")<br>
	private int id ;	<br>
<br>
	@javax.persistence.Basic(fetch=FetchType.LAZY)<br>
	@org.guzz.annotations.Column(loader = org.guzz.pojo.loader.TwoPhaseClobDataLoader.class, type="clob")<br>
	private TranClob aboutMe ;<br>
<br>
	....<br>
}<br>
</code></pre>

<h3>@org.guzz.annotations.GenericGenerator</h3>

GenericGenerator is used to expand the JPA's four id generators.<br>
<br>
Use this if JPA's standard Id Generator doesn't meet your needs.<br>
<br>
Since the definition of primary key generation strategy by @GenericGenerator achieve, guzz in JPA based on the expansion can be used like the way guzz introduced to generate unique primary key strategy is to add @GenericGenerator through.<br>
<br>
For example, JPA standard usage:<br>
<pre><code>@Id<br>
@GeneratedValue(GenerationType.AUTO)<br>
</code></pre>

Guzz can be used on the specific use to achieve the following:<br>
<pre><code>@GeneratedValue(generator = "paymentableGenerator")  <br>
@GenericGenerator(name = "paymentableGenerator", strategy = "assigned")<br>
</code></pre>

<b>Table 2-8 GuzzGenericGenerator</b>

<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Required</td><td> unique generator name </td></tr>
<tr><td> strategy </td><td> Required </td><td> Generator strategy either a predefined Hibernate strategy or a fully qualified class name. </td></tr>
<tr><td> parameters </td><td> Optional </td><td> Optional generator parameters </td></tr></tbody></table>

Example 2-9 shows how to use this annotation to specify the primary table name.<br>
<br>
<b>Example 2-9 @GuzzGenericGenerator</b>

<pre><code>@Entity<br>
@org.guzz.annotations.Entity(businessName="mailUid")<br>
@Table(name="tb_mailUid")<br>
@GenericGenerator(name = "assignedGen", strategy = "assigned")<br>
public class MailUid {<br>
	<br>
	@Id<br>
	@GeneratedValue(generator="assignedGen")<br>
	@Column(name="m_mailUid")<br>
	private String mailUid ;<br>
	....<br>
}<br>
</code></pre>

<h3>@org.guzz.annotations.GenericGenerators</h3>

A collection of @org.guzz.annotations.GenericGenerator.<br>
<br>
<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> value </td><td> Required</td><td> Generators </td></tr></tbody></table>

<h3>@org.guzz.annotations.Parameter</h3>

Parameter for GenericGenerator (basically key/value pattern).<br>
<br>
<table><thead><th> <b>Attribute</b> </th><th> <b>Required</b> </th><th> <b>Description</b> </th></thead><tbody>
<tr><td> name </td><td> Required</td><td> key </td></tr>
<tr><td> value </td><td> Required</td><td> value </td></tr></tbody></table>

for example:<br>
<pre><code>@GeneratedValue(generator = "paymentableGenerator")<br>
@GenericGenerator(name = "paymentableGenerator", strategy = "seqhilo", <br>
         parameters = { @Parameter(name = "max_lo", value = "5"), @Parameter(name = "sequence", value = "seq_pmg") })<br>
<br>
</code></pre>

<h2>IndexOfAnnotations</h2>

<ul><li>A<br>
<ol><li><a href='#@AttributeOverride.md'>@AttributeOverride</a>
</li><li><a href='#@AttributeOverrides.md'>@AttributeOverrides</a>
</li></ol></li><li>B<br>
<ol><li><a href='#@Basic.md'>@Basic</a>
</li></ol></li><li>C<br>
<ol><li><a href='#@Column.md'>@Column</a>
</li><li><a href='#@org.guzz.annotations.Column.md'>@org.guzz.annotations.Column</a>
</li></ol></li><li>E<br>
<ol><li><a href='#@Entity.md'>@Entity</a>
</li><li><a href='#@org.guzz.annotations.Entity.md'>@org.guzz.annotations.Entity</a>
</li><li><a href='#@Enumerated.md'>@Enumerated</a>
</li></ol></li><li>G<br>
<ol><li><a href='#@GeneratedValue.md'>@GeneratedValue</a>
</li><li><a href='#@org.guzz.annotations.GenericGenerator.md'>@org.guzz.annotations.GenericGenerator</a>
</li><li><a href='#@org.guzz.annotations.GenericGenerators.md'>@org.guzz.annotations.GenericGenerators</a>
</li><li><a href='#@org.guzz.annotations.Parameter.md'>@org.guzz.annotations.Parameter</a>
</li></ol></li><li>I<br>
<ol><li><a href='#@Id.md'>@Id</a>
</li></ol></li><li>L<br>
<ol><li><a href='#@Lob.md'>@Lob</a>
</li></ol></li><li>M<br>
<ol><li><a href='#@MappedSuperclass.md'>@MappedSuperclass</a>
</li></ol></li><li>S<br>
<ol><li><a href='#@SequenceGenerator.md'>@SequenceGenerator</a>
</li></ol></li><li>T<br>
<ol><li><a href='#@Table.md'>@Table</a>
</li><li><a href='#@TableGenerator.md'>@TableGenerator</a>
</li><li><a href='#@Temporal.md'>@Temporal</a>
</li><li><a href='#@Transient.md'>@Transient</a>