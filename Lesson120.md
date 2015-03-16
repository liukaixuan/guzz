# Getting Start #

In this lesson, we will guide you to write a Message-Board system running on 5 database machines, hand by hand.

You are suggested to write it yourself during the lesson. Don't be panic, only 1 database is required to simulate this.

In this lesson, you will learn: How to create a guzz project, how to add record into database, how to separate database read and write, how to split tables and distribute them among several machines, and how to use "Service" in guzz.

# Chapters #




# What's guzz? Where to use? #

guzz[ˈɡuzi], means seeds or crops in Chinese language.

guzz is a persist layer framework similar to hibernate and ibatis. It is designed to replace the later two in your system. After introducing guzz, your traditional ssh(spring + struts + hibernate) architecture will be turned into ssg(spring + struts + guzz). In the meantime, guzz can work together with hibernate and ibatis to solve only the database bottle-neck modules without changing other codes.

guzz is suitable to large-scaled system with huge clicks or huge amount of data, or both. When multiple databases are required, or some tables should be cutted into small ones in your application, guzz is the best choice in all available general purpose framework around the world.

guzz provides table distributing, multiple databases supporting, table shard, read-write separation, and distributed transaction features bases on a simple configuration file, to ease the complication of large system programming and upgrading in the future. You will find it as simple as regular programming. It's transparent and future-upgrading oriented to the developers.

guzz also provides some definitions of Service to help your team building a Cloud Platform from scratch. If you are planning to combine common features and general computing together from some projects, guzz service is a good choice to getting start.

guzz is a free and open source project located in Google Code: http://code.google.com/p/guzz/


# Prepare to start the lesson #

In this lesson, MyEclipse IDE is used to demonstrate the coding, and 1 Mysql5.0 + 1 Tomcat6 is required to run the Message-Board. So, What you need is your developing machine.

Of course, it will even better if you have five or more databases.

One Database is enough for this lesson, it's transparent to guzz how many database machines you actually used.


# Create Project: MessageBoard #

Our Message-Board is based on the architecture of springIOC + springMVC + guzz, so just download the empty project to get start. The empty project is something like "a empty sample project buildxxxxxxx.zip" listed in ：http://code.google.com/p/guzz/downloads/list

Extract the zip file, import the project into Eclipse(File -> Import -> General/Existing Projects into Workspace).

Choose the new project "GuzzEmpty" in the workspace, Right Click -> Refactor -> Rename, type "MessageBoard", and confirm the dialog.

Download the latest guzz release package(something like "guzz1.x.x buildxxxxxx.zip") from http://code.google.com/p/guzz/downloads/list, extract the zip file, and override guzz.jar under MessageBoard project's "/WebRoot/WEB-INF/lib/".

Rename fms.properties to messageBoard.properties under the project's /WebRoot/WEB-INF/ folder. Remember, this file is our main configuration file.


# Configure the project, insert a Message #

## Configure the project ##

We define our message as "Message", create its domain class "example/business/Message.java" under src source folder:
```
package example.business;

import java.util.Date;

public class Message implements java.io.Serializable {
	
	private int id ;

	private String content ;
	
	private Date createdTime ;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}
	
}
```

In the meantime, create Message.java's mapping file Message.hbm.xml under the same folder "example/business/" :
```
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
	<class name="example.business.Message" table="tb_message">
		<id name="id" type="int" column="id">
			<generator class="native" />
		</id>
		<property name="content" type="string" column="content" />
		<property name="createdTime" type="datetime" column="createdTime" />
	</class>
</hibernate-mapping>
```

In the mapping file, we know that our Message(s) will be stored in the table tb\_message. tb\_message has one column "content" to store the message content.

Start our Mysql5.0 and connect to it, create a database mb\_main and the table "tb\_message". SQL:
```
create database mb_main default character set utf8 ;

use mb_main ;

create table tb_message(
	id int not null auto_increment primary key, 
	content text, 
	createdTime datetime
)engine=Innodb ;

```

Now, configure the database connection pool to link the mb\_main database with MessageBoard system:

Open /WebRoot/WEB-INF/guzz.xml, turn to source edit mode. Right click /WebRoot/WEB-INF/messageBoard.properties -> Open With -> Properties File Editor to open the main configuration file. (Caution: Don't use the Advanced Properties Editor.)

Edit guzz.xml: delete the dbgroups of updateDB and logDB; delete the two services; add business declaration of Message. The final guzz.xml should be:
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE guzz-configs PUBLIC "-//GUZZ//DTD MAIN CONFIG//EN" "http://www.guzz.org/dtd/guzz.dtd">

<guzz-configs>
	
	<dialect class="org.guzz.dialect.Mysql5Dialect" />
	
	<tran>
		<dbgroup name="default" masterDBConfigName="masterDB" />
	</tran>
	
	<config-server>
		<server class="org.guzz.config.LocalFileConfigServer">
			<param name="resource" value="messageBoard.properties" />
		</server>
	</config-server>
	
	<!-- business starts -->
	<business dbgroup="default" name="message" file="classpath:example/business/Message.hbm.xml" />
	<!-- business ends -->

</guzz-configs>
```

In the new guzz.xml, Message uses the "default" database group. The "default" database group's master(write) database's configuration group name is "masterDB", the database group has no slave(read-only) database(s). Edit the opened messageBoard.properties file, change the pool configurations in [masterDB](masterDB.md) to link our mb\_main database, and drop other pools's configurations. The final messageBoard.properties should be something like:
```
#guzz app config file.
 
#master db
[masterDB]
guzz.identifer=defaultMasterDB1
guzz.IP=localhost
guzz.maxLoad=120
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/mb_main?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60
 
#debug settings
[guzzDebug]
#runMode=debug/production
runMode=debug
#onError=halt/log/ignore
onError=halt
printSQL=true
printSQLParams=true
ignoreDemonThreadSQL=true
#print out how many nano-seconds a sql takes to execute.
measureTime=true
#only print out slow sqls that takes over xxx mill-seconds to execute. 0 means print out all.
onlySlowSQLInMillSeconds=0

############################### fundamental services #####################

```

Open /WebRoot/WEB-INF/applicationContext.xml，delete bean:insertQueueService.

Deploy the project to Tomcat6, start it. You configure it successful if no exception raised, or check the permission Mysql granted.

Stop the tomcat6.

## Insert a message ##

In the below we will insert our first Message.

Create a jsp file "messagesList.jsp" under /WebRoot/ for visitors to fill the Message form. messagesList.jsp:
```
<%@ page language="java" pageEncoding="UTF-8" errorPage="/WEB-INF/jsp/include/defaultException.jsp"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
	<title>Message List</title>
  </head>
  
  <body>
	
	Leave a message:<br>
	
	<form method="POST" action="./newMessage.do">
		<textarea name="content" cols="80" rows="10"></textarea>
		
		<br/>
		<input type="submit" />
	</form>
  </body>
</html>
```

Create a springMVC controller "example.view.action.NewMessageAction.java" to handle the submission of the Message Form：
```
package example.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.GuzzContext;
import org.guzz.transaction.WriteTranSession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;

public class NewMessageAction implements Controller {
	
	private GuzzContext guzzContext ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String content = request.getParameter("content") ;
		
		WriteTranSession write = guzzContext.getTransactionManager().openRWTran(true) ;
		
		Message msg = new Message() ;
		msg.setContent(content) ;
		msg.setCreatedTime(new java.util.Date()) ;
		
		try{
			write.insert(msg) ;
		}finally{
			write.close() ;
		}
		
		return new ModelAndView("redirect:/messageList.jsp");
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

}
```

Edit "/WebRoot/WEB-INF/dispatcher-servlet.xml", add NewMessageAction's mapping bean:
```
<bean name="/newMessage.do" class="example.view.action.NewMessageAction">
	<property name="guzzContext" ref="guzzContext" />
</bean>
```

Deploy the project, start Tomcat6, visit http://localhost:8080/guzz/messageList.jsp, Type a message "I am the first message. What about you?", Submit.

Open table tb\_message, the first message is there.

## List Messages with pagination ##

We also use "messageList.jsp" to list the posted messages. 30 messages one page. To query messages from tb\_message, we use guzz db taglib. New messageList.jsp :
```
<%@ page language="java" pageEncoding="UTF-8" errorPage="/WEB-INF/jsp/include/defaultException.jsp"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<g:page business="message" var="m_messages" pageNo="${param.pageNo}" pageSize="30" orderBy="id desc" scope="request" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>Message List</title>
  </head>
  
  <body> 
  	 
  	Leave a message:<br>
  	
  	<form method="POST" action="./newMessage.do">
  		<textarea name="content" cols="80" rows="10"></textarea>
  		
  		<br/>
  		<input type="submit" />
  	</form>
  	
  	<hr>
  	<table width="96%" border="1">
  		<tr>
  			<th>No.</th>
  			<th>Content</th>
  			<th>Date</th>
  		</tr>
  		
  		<c:forEach items="${m_messages.elements}" var="m_msg">
  		<tr>
  			<td>${m_messages.index}</td>
  			<td><g:out value="${m_msg.content}" escapeXml="false" escapeScriptCode="true" /></td>
  			<td>${m_msg.createdTime}</td>
  		</tr>
  		</c:forEach>
  	</table>
  	
  	<table width="96%" border="1">
  		<tr>
  			<c:import url="/WEB-INF/jsp/include/console_flip.jsp" />
  		</tr>
  	</table>
  	
  </body>
</html>
```

In this jsp,
```
<g:page business="message" var="m_messages" pageNo="${param.pageNo}" pageSize="30" orderBy="id desc" scope="request" />
```

query and load messages by the passed pageNo, order it by "id desc" in database, then print it with a c:forEach loop later. In the loop, m\_messages.index prints out the current index in all matched records starting from 1, g:out prints out the msg's content that accepts HTML code but transferred javascript.

In the end, we import "/WEB-INF/jsp/include/console\_flip.jsp" to do the general pagination.

Visit: http://localhost:8080/guzz/messageList.jsp again, you can see the messages' list now:

<img width='700px' src='http://guzz.googlecode.com/svn/wiki/no-wikis/messageList.png' />

# Separate database read and write(active the 2rd DB) #

If you have more database, first setup mb\_main database's slave database ([How to Set Up Replication in Mysql.com](http://dev.mysql.com/doc/refman/5.0/en/replication-howto.html)). Edit guzz.xml:
```
<dbgroup name="default" masterDBConfigName="masterDB" />
```

add the dbgroup's slaveDBConfigName attribute, set it to "slaveDB" :
```
<dbgroup name="default" masterDBConfigName="masterDB" slaveDBConfigName="slaveDB" />
```

add the db pool configurations of the slaveDB in messageBoard.properties:
```

[slaveDB]
guzz.identifer=defaultSlaveDB1
guzz.IP=localhost
guzz.maxLoad=80
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/mb_main?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60

```

Of course, the above configuration's jdbcUrl should be pointed to your standalone slave database machine if you have one.

The final messageBoard.properties would be:
```
#guzz app config file.
 
#master db
[masterDB]
guzz.identifer=defaultMasterDB1
guzz.IP=localhost
guzz.maxLoad=120
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/mb_main?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60

[slaveDB]
guzz.identifer=defaultSlaveDB1
guzz.IP=localhost
guzz.maxLoad=80
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/mb_main?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60
 
#debug settings
[guzzDebug]
#runMode=debug/production
runMode=debug
#onError=halt/log/ignore
onError=halt
printSQL=true
printSQLParams=true
ignoreDemonThreadSQL=true
#print out how many nano-seconds a sql takes to execute.
measureTime=true
#only print out slow sqls that takes over xxx mill-seconds to execute. 0 means print out all.
onlySlowSQLInMillSeconds=0

############################### fundamental services #####################
 
```

OK, now you have finished the db read-write separation. Start up the MessageBoard, it will create two database pools. The above g:page will execute queries from the slave database pool automatically. If you call guzz's persist API directly in the future, all read operations with delay opinion will be executed in the slave connections.

# A Mulit-User MessageBoard #

To implement a Mulit-User MessageBoard(A message board you can leave messages to different people), first we have to add a new domain class "User". The "User" has three properties: id, userName, and message count. Create example.business.User.java:
```
package example.business;

public class User implements java.io.Serializable {
	
	private int id ;

	private String userName ;
	
	private int messageCount ;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(int messageCount) {
		this.messageCount = messageCount;
	}
	
}
```

Create its mapping file User.hbm.xml：
```
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
    <class name="example.business.User" table="tb_user">
        <id name="id" type="int" column="id">
        	<generator class="native" />
        </id>
        <property name="userName" type="string" column="userName" />
        <property name="messageCount" type="int" column="messageCount" />
    </class>
</hibernate-mapping>
```

Add a new property "userId" in Message.java and Message.hbm.xml "foreign key" to "User"'s id. The new Message.java and Message.hbm.xml:
```
package example.business;

import java.util.Date;

public class Message implements java.io.Serializable {
	
	private int id ;

	private String content ;
	
	private int userId ;
	
	private Date createdTime ;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}
	
}
```

```
<?xml version="1.0"?>
<!DOCTYPE hibernate-mapping PUBLIC "-//Hibernate/Hibernate Mapping DTD 3.0//EN"
"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd">
<hibernate-mapping>
    <class name="example.business.Message" table="tb_message">
        <id name="id" type="int" column="id">
        	<generator class="native" />
        </id>
        <property name="userId" type="int" column="userId" />
        <property name="content" type="string" column="content" />
        <property name="createdTime" type="datetime" column="createdTime" />
    </class>
</hibernate-mapping>
```

create tb\_user in the mb\_main database, and alter tb\_message to add "userId":
```
use mb_main ;

create table tb_user(
	id int not null auto_increment primary key, 
	userName varchar(64) not null, 
	messageCount int(11)default 0
)engine=Innodb ;

alter table tb_message add column userId int(11) default 1 ;
create index idx_msg_uid on tb_message(userId) ;

insert into tb_user(userName) values('Lucy') ;
insert into tb_user(userName) values('Lily') ;
insert into tb_user(userName) values('Cathy') ;
insert into tb_user(userName) values('Polly, The Bird') ;

update tb_user set messageCount = (select count(*) from tb_message) where id = 1 ;

```

Add the business declaration of User in guzz.xml:
```
<business dbgroup="default" name="user" file="classpath:example/business/User.hbm.xml" />
```

As this is a mulit-user Message-Board, we define a new parameter "userId"(reference to User's id) to distinguish whose messages in all web requests.

Edit messageList.jsp to support userId:
```
<%@ page language="java" pageEncoding="UTF-8" errorPage="/WEB-INF/jsp/include/defaultException.jsp"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<g:get business="user" var="m_user" limit="id=${param.userId}" />

<g:boundary>
	<g:addLimit limit="userId=${m_user.id}" />
	<g:page business="message" var="m_messages" tableCondition="${m_user.id}" pageNo="${param.pageNo}" pageSize="30" orderBy="id desc" scope="request" />
</g:boundary>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>${m_user.userName}'s Message List</title>
  </head>
  
  <body>  	 
  	Leave a message:<br>  	

  	<form method="POST" action="./newMessage.do">
  		<input type="hidden" name="userId" value="${m_user.id}" />
  		
  		<textarea name="content" cols="80" rows="10"></textarea>  		
  		....
```

In the new messageList.jsp, g:page add a query condition. In the form, "userId" is submitted as a hidden parameter.

Edit NewMessageAction.java to receive userId:
```
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		String content = request.getParameter("content") ;

		Message msg = new Message() ;
		msg.setContent(content) ;
		msg.setCreatedTime(new java.util.Date()) ;
		
		//close auto-commit
		WriteTranSession write = guzzContext.getTransactionManager().openRWTran(false) ;
		
		try{
			User user = (User) write.findObjectByPK(User.class, userId) ;
			user.setMessageCount(user.getMessageCount() + 1) ;
			
			msg.setUserId(userId) ;
			
			write.insert(msg) ;
			write.update(user) ;
			
			write.commit() ;
		}catch(Exception e){
			write.rollback() ;
			
			throw e ;
		}finally{
			write.close() ;
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}
```

We choose to handle the transcation manual as we have to update two tables.

Deploy and restart the application, visit: http://localhost:8080/guzz/messageList.jsp?userId=1 you are reading Lucy's Messages; visit: http://localhost:8080/guzz/messageList.jsp?userId=1 you are reading Lily's Messages.

Mulit\_User Message-Board is done.


# Use two db groups(active the 3rd and 4th databases) #

Our Message-Board is so popular that we find mb\_main database is too busy. It's almost crashed now, and we have to move tb\_user to another database group.

We name this dbgroup to be "userDB". It has a master db and a slave db(our third and fourth database machines).

First, intall the two database machines, setup replications, and export/import tb\_user from mb\_main database.

For demonstration, we create a new database mb\_user in our local mysql for "userDB". It's in the same database instance with mb\_main. SQL:
```
create database mb_user default character set utf8 ;

create table mb_user.tb_user select * from mb_main.tb_user ;

alter table mb_user.tb_user modify column id int(11) not null auto_increment primary key ;

drop table mb_main.tb_user ;

```


So far, we have two database groups: mb\_main to store tb\_message, mb\_user to store tb\_user.

Edit guzz.xml, add the new userDB dbgroup inside element "tran" :
`<dbgroup name="userDB" masterDBConfigName="userMasterDB" slaveDBConfigName="userSlaveDB" />`

Edit the business declaration of User to use "userDB" dbgroup:
`<business dbgroup="userDB" name="user" file="classpath:example/business/User.hbm.xml" />`

Edit messageBoard.properties, add userDB's detailed db connection pool configurations [userMasterDB](userMasterDB.md) and [userSlaveDB](userSlaveDB.md):
```
[userMasterDB]
guzz.identifer=userMasterDB1
guzz.IP=localhost
guzz.maxLoad=120
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/mb_user?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60

[userSlaveDB]
guzz.identifer=userSlaveDB1
guzz.IP=localhost
guzz.maxLoad=80
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/mb_user?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60
```

If you the third and fourth DB machines, change the jdbcUrl above to your standalone DBs.

Deploy and start the application. Your Message-Board is now running on 4 databases. Post a new Message, the updates of tb\_user and tb\_message will be be completed in a distributed transcation automatically. Not a single line of your exsiting code is required to change.

**Caution:** 1. The index won't be re-created by "create table ..select..", so remeber to create the index again in production. 2. Some sqls above manupilate tables by "db.table", this may be ignored in replications in Mysql. Execute the sqls again in your slave db. We won't warn this any more.

# Split Message table into small ones #

Our Message-Board achives a increditable success! Now, we have received over 10 million messages in tb\_message to make the system very very slow. The big tb\_message table is a bottle-neck now, and we decide to split it into many small ones to improve the suituation.

Our split rule is: one user's messages, stored in one single small table; the table name for each person is tb\_message_${userId}_

For detailed documentation, please read: [TutorialShadowTable](TutorialShadowTable.md).

To split a table, first you have to define the rules. Create a new java class "example.business.MessageShadowTableView" to do this job:
```
package example.business;

import org.guzz.exception.GuzzException;
import org.guzz.orm.AbstractShadowTableView;

public class MessageShadowTableView extends AbstractShadowTableView {

	public String toTableName(Object tableCondition) {
		if (tableCondition = null) { //Check condition
			throw new GuzzException("null table conditon is not allowed.");
		}

		Integer userId = (Integer) tableCondition;

		//tb_message_${userId}
		return "tb_message_" + userId.intValue() ;
	}

}
```

In MessageShadowTableView we know that the shadow/split condition/parameter is "userId". We have four users, so we have to split the tb\_message into four small tables: tb\_message\_1, tb\_message\_2, tb\_message\_3, tb\_message\_4.
```
use mb_main ;

create table tb_message_1 select * from tb_message where userId = 1 ;
create table tb_message_2 select * from tb_message where userId = 2 ;
create table tb_message_3 select * from tb_message where userId = 3 ;
create table tb_message_4 select * from tb_message where userId = 4 ;

alter table tb_message_1 modify column id int(11) not null auto_increment primary key ;
alter table tb_message_2 modify column id int(11) not null auto_increment primary key ;
alter table tb_message_3 modify column id int(11) not null auto_increment primary key ;
alter table tb_message_4 modify column id int(11) not null auto_increment primary key ;

drop table tb_message ;
```

Edit Message.hbm.xml, add a new attribute shadow="example.business.MessageShadowTableView" in "class" element to tell guzz the shadow/split rules. Change the dtd head to guzz's to avoid the errors IDE would report. Message.hbm.xml:
```
<?xml version="1.0"?>
<!DOCTYPE guzz-mapping PUBLIC "-//GUZZ//GUZZ MAPPING DTD//EN" "http://www.guzz.org/dtd/guzz-mapping.dtd">
<guzz-mapping>
    <class name="example.business.Message" table="tb_message" shadow="example.business.MessageShadowTableView">
        <id name="id" type="int" column="id">
        	<generator class="native" />
        </id>
        <property name="userId" type="int" column="userId" />
        <property name="content" type="string" column="content" />
        <property name="createdTime" type="datetime" column="createdTime" />
    </class>
</guzz-mapping>
```

Now, guzz knows that all persist operations to "Message" should be passed to example.business.MessageShadowTableView to compute the runtime split table name. And when you need to operate "Message", you have to pass "userId" as the split parameter(we also call it tableCondition).

Edit messageList.jsp's, pass "userId" tableCondition to g:page:
`<g:page business="message" var="m_messages" tableCondition="${param.userId}" pageNo="${param.pageNo}" pageSize="30" orderBy="id desc" scope="request" />`

Edit NewMessageAction.java, change write.insert(msg) to write.insert(msg, userId) to pass the tableCondition through the second parameter.

Deploy and Restart Tomcat. Now our messages are distributed in 4 small tables.


# Distribute the split small tables into different DB machines #

One year later, we find the main\_db database is too big. Yes, we have split the big table into small ones to improve the performance, and it worked. But they are still stored in one DB machine together. The data is huge in total.

In a real application, you may have split your tables into hundreds of small tables. They may take too much resources in total for a single db; or there is too many tables to manage; or the performance is just not enough for a single db. Anything with huge data.

To slove this problem, we need to distribute the split small tables into different DB groups(different machines).

Guzz introduce "VirtualDB" for this feature. Read more about VirtualDB: [TutorialVirtualDB](TutorialVirtualDB.md)

To show you the flexibility of VirtualDB, our distribution rule for tb\_message\_x tables is: Leave user 1's messages in the default dbgroup(mb\_main DB), others in the userDB dbgroup(mb\_user DB). In your real application, you may distribute data by timeline, and introduce the 5th,6th,7th,8th or even more database machines to store new data.

Now, move tb\_message\_2, tb\_message\_3, tb\_message\_4 from mb\_main to mb\_user:
```
create table mb_user.tb_message_2 select * from mb_main.tb_message_2 ;
create table mb_user.tb_message_3 select * from mb_main.tb_message_3 ;
create table mb_user.tb_message_4 select * from mb_main.tb_message_4 ;

alter table mb_user.tb_message_2 modify column id int(11) not null auto_increment primary key ;
alter table mb_user.tb_message_3 modify column id int(11) not null auto_increment primary key ;
alter table mb_user.tb_message_4 modify column id int(11) not null auto_increment primary key ;

drop table mb_main.tb_message_2 ;
drop table mb_main.tb_message_3 ;
drop table mb_main.tb_message_4 ;
```

Create a new java class to define the rules of Message's VirtualDB:
```
package example.business;

import org.guzz.connection.AbstractVirtualDBView;
import org.guzz.exception.GuzzException;

public class MessageVirtualDBView extends AbstractVirtualDBView {
    
    public String getPhysicsDBGroupName(Object tableCondition) {
		if (tableCondition = null) {
			throw new GuzzException("null table conditon is not allowed.");
		}
		
		int userId = (Integer) tableCondition;
		
		if(userId = 1){
			 //store lucy's messages in the default database.
			return "default" ;
		}else{
			 //store others in the userDB database.
			return "userDB" ;
		}
    }

}
```

For each domain class required the VirtualDB feature, you have to add a virtualdbgroup in guzz.xml to tell guzz the rules.

For our Message, edit guzz.xml, add a new virtualdbgroup under tran:
`<virtualdbgroup name="messageDB" shadow="example.business.MessageVirtualDBView" />`

and change the dbgroup of Message's business declaration to "messageDB". The final guzz.xml is:
```
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE guzz-configs PUBLIC "-//GUZZ//DTD MAIN CONFIG//EN" "http://www.guzz.org/dtd/guzz.dtd">

<guzz-configs>
	
	<dialect class="org.guzz.dialect.Mysql5Dialect" />
	
	<tran>
		<dbgroup name="default" masterDBConfigName="masterDB" slaveDBConfigName="slaveDB" />
		<dbgroup name="userDB" masterDBConfigName="userMasterDB" slaveDBConfigName="userSlaveDB" />
		
		<virtualdbgroup name="messageDB" shadow="example.business.MessageVirtualDBView" />
	</tran>
	
	<config-server>
		<server class="org.guzz.config.LocalFileConfigServer">
			<param name="resource" value="messageBoard.properties" />
		</server>
	</config-server>
	
	<!-- business starts -->
	<business dbgroup="messageDB" name="message" file="classpath:example/business/Message.hbm.xml" />
	<business dbgroup="userDB" name="user" file="classpath:example/business/User.hbm.xml" />
	<!-- business ends -->

</guzz-configs>
```

Deploy and restart Tomcat6. All lucy's messages will be stored in mb\_main, and others in mb\_user. Distributed table shadow/split is done!


# Vote a message with SlowUpdateService(active the 3rd dbgroup, the 5th db machine) #

## Vote a message ##

People asked whether they can vote for good messages like digg did. We said yes! We record good vote, bad vote, and compute a score out of that. A good vote adds 10 points to the score while a bad one minus 8 points. Edit Message.java and Message.hbm.xml to add the three new properties:
```
private int voteYes ;

private int voteNo ;

private int voteScore ;
```

Change the databases:
```
alter table mb_main.tb_message_1 add column voteYes int(11) default 0 ;
alter table mb_main.tb_message_1 add column voteNo int(11) default 0 ;
alter table mb_main.tb_message_1 add column voteScore int(11) default 0 ;

alter table mb_user.tb_message_2 add column voteYes int(11) default 0 ;
alter table mb_user.tb_message_2 add column voteNo int(11) default 0 ;
alter table mb_user.tb_message_2 add column voteScore int(11) default 0 ;

alter table mb_user.tb_message_3 add column voteYes int(11) default 0 ;
alter table mb_user.tb_message_3 add column voteNo int(11) default 0 ;
alter table mb_user.tb_message_3 add column voteScore int(11) default 0 ;

alter table mb_user.tb_message_4 add column voteYes int(11) default 0 ;
alter table mb_user.tb_message_4 add column voteNo int(11) default 0 ;
alter table mb_user.tb_message_4 add column voteScore int(11) default 0 ;
```

Add a new Action to handle the vote request, example.view.action.VoteMessageAction.java：
```
package example.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.Assert;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;

public class VoteMessageAction implements Controller {
	
	private GuzzContext guzzContext ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		int msgId = RequestUtil.getParameterAsInt(request, "msgId", 0) ;
		String type = request.getParameter("type") ;

		//auto-commit
		WriteTranSession write = guzzContext.getTransactionManager().openRWTran(true) ;
		
		try{
			//set tableCondition
			Guzz.setTableCondition(userId) ;
			Message msg = (Message) write.findObjectByPK(Message.class, msgId) ;
			Assert.assertNotNull(msg, "msg not found!") ;
			
			if("yes".equals(type)){
				msg.setVoteYes(msg.getVoteYes() + 1) ;
				msg.setVoteScore(msg.getVoteScore() + 10) ;
			}else{
				msg.setVoteNo(msg.getVoteNo() + 1) ;
				msg.setVoteScore(msg.getVoteScore() - 8) ;
			}
			
			write.update(msg) ;
		}finally{
			write.close() ;
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

}
```

In this Action, we call Guzz.setTableCondition(userId) to set the value of userId as the default tableCondition for all persist operations in this thread, then read the Message, inc/dec the count, and update it.

Add the mapping of this Action in dispatcher-servlet.xml:
```
<bean name="/voteMessage.do" class="example.view.action.VoteMessageAction">
	<property name="guzzContext" ref="guzzContext" />
</bean>
```

Edit messageList.jsp , list votes and the link to vote:
```
<%@ page language="java" pageEncoding="UTF-8" errorPage="/WEB-INF/jsp/include/defaultException.jsp"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:get business="user" var="m_user" limit="id=${param.userId}" />

<g:boundary>
	<g:addLimit limit="userId=${m_user.id}" />
	<g:page business="message" var="m_messages" tableCondition="${m_user.id}" pageNo="${param.pageNo}" pageSize="30" orderBy="id desc" scope="request" />
</g:boundary>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>${m_user.userName}'s Message List</title>
  </head>
  
  <body>  	 
  	Leave a message:<br>
  	
  	<form method="POST" action="./newMessage.do">
  		<input type="hidden" name="userId" value="${m_user.id}" />
  		
  		<textarea name="content" cols="80" rows="10"></textarea>
  		
  		<br/>
  		<input type="submit" />
  	</form>
  	
  	<hr>
  	<table width="96%" border="1">
  		<tr>
  			<th>No.</th>
  			<th>Vote</th>
  			<th>Content</th>
  			<th>Date</th>
  		</tr>
  		
  		<c:forEach items="${m_messages.elements}" var="m_msg">
  		<tr>
  			<td>${m_messages.index}</td>
  			<td>
  				voteYes: <a href="./voteMessage.do?type=yes&userId=${m_msg.userId}&msgId=${m_msg.id}">${m_msg.voteYes}</a><br>
  				voteNo: <a href="./voteMessage.do?type=no&userId=${m_msg.userId}&msgId=${m_msg.id}">${m_msg.voteNo}</a><br>
  				voteScore: ${m_msg.voteScore}
  			</td>
  			<td>vote<g:out value="${m_msg.content}" escapeXml="false" escapeScriptCode="true" /></td>
  			<td>${m_msg.createdTime}</td>
  		</tr>
  		</c:forEach>
  	</table>
  	
  	<table width="96%" border="1">
  		<tr>
  			<c:import url="/WEB-INF/jsp/include/console_flip.jsp" />
  		</tr>
  	</table>
  	
  </body>
</html>
```

Deploy and restart, now we can vote for a message.

## SlowUpdateService(Active the 5th database machine) ##

Soon after deploying vote feature to our production system, a critical problem raised: People Love It! Millions of votes every day! Every time a vote comes, we load the message and update it back, but the database cann't hold it anymore. There are too much queries.

To improve this, we hope to queue the vote operations, combing updates for the same row's same column, and write back to the database in a batch way to reduce the cost.

fortunately, we don't have to write this ourself, guzz shipped with SlowUpdateService to handle this suitation as a core Service. Read how it works: [AppendCoreService](AppendCoreService.md)

To reduce the compact to the main databases, we active a new temp database machine to record the vote operations.

Create database mb\_temp, and table tb\_guzz\_su in it:
```
create database mb_temp default character set utf8 ;

use mb_temp ;

create table tb_guzz_su(
	gu_id bigint not null auto_increment primary key, 
	gu_db_group varchar(32) not null, 
	gu_tab_name varchar(64) not null, 
	gu_inc_col varchar(64) not null ,
	gu_tab_pk_col varchar(64) not null,
	gu_tab_pk_val varchar(64) not null ,
	gu_inc_count int(11) not null
)engine=Innodb ;
```

Add a new dbgroup "tempDB" for mb\_temp database, and add tb\_guzz\_su's business declaration:
```
<tran>
		....
		<dbgroup name="tempDB" masterDBConfigName="tempMasterDB" />
		....
</tran>

<business dbgroup="tempDB" name="guzzSlowUpdate" file="classpath:example/business/IncUpdateBusiness.hbm.xml" />
```

Move fms/business/IncUpdateBusiness.hbm.xml to example/business/ under src . Delete the folder: src/fms/.

The default SlowUpdateService is started when guzz starts, named "guzzSlowUpdate". The configuration name for this Sevice is also "guzzSlowUpdate".

Edit messageBoard.properties, add the pool configuration for tempDB, and configurations for guzzSlowUpdate Service.
```
[tempMasterDB]
guzz.identifer=tempMasterDB1
guzz.IP=localhost
guzz.maxLoad=120
driverClass=com.mysql.jdbc.Driver
jdbcUrl=jdbc:mysql://localhost:3306/mb_temp?useUnicode=true&amp;characterEncoding=UTF-8&amp;useServerPrepStmts=true
user=root
password=root
acquireIncrement=10
idleConnectionTestPeriod=60

[guzzSlowUpdate]
#max size of cached queue
queueSize=20480

#batch size for updating to the temporary database.
batchSize=2048
```

Edit applicationContext.xml, add a new bean to export the service as a spring bean:
```
    <bean id="guzzSlowUpdateService" class="org.guzz.web.context.spring.GuzzServiceFactoryBean"> 
        <property name="serviceName" value="guzzSlowUpdate" /> 
    </bean>
```

Change VoteMessageAction.java to use the guzzSlowUpdate service:
```
package example.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.service.core.SlowUpdateService;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;

public class VoteMessageAction implements Controller {
	
	private SlowUpdateService slowUpdateService ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		int msgId = RequestUtil.getParameterAsInt(request, "msgId", 0) ;
		String type = request.getParameter("type") ;

		if("yes".equals(type)){
			//public void updateCount(Class domainClass, Object tableCondition, String propToUpdate, Serializable pkValue, int countToInc) ;
			this.slowUpdateService.updateCount(Message.class, userId, "voteYes", msgId, 1) ;
			this.slowUpdateService.updateCount(Message.class, userId, "voteScore", msgId, 10) ;
		}else{
			this.slowUpdateService.updateCount(Message.class, userId, "voteNo", msgId, 1) ;
			this.slowUpdateService.updateCount(Message.class, userId, "voteScore", msgId, -8) ;
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}

	public SlowUpdateService getSlowUpdateService() {
		return slowUpdateService;
	}

	public void setSlowUpdateService(SlowUpdateService slowUpdateService) {
		this.slowUpdateService = slowUpdateService;
	}

}
```

In the new Action, we call updateCount to write the operation to the queue instead of database.

Edit dispatcher-servlet.xml to inject guzzSlowUpdateService for the bean /voteMessage.do :
```
<bean name="/voteMessage.do" class="example.view.action.VoteMessageAction">
	<property name="slowUpdateService" ref="guzzSlowUpdateService" />
</bean>
```

Deploy and restart the application. Vote. The vote count does't change. It is Ok. Open the table tb\_guzz\_su in mb\_temp, the vote operation is already written in the queue.

## SlowUpdateServerService ##

To write operations in tb\_guzz\_su back to the main db, we need to configure the serverside-part of SlowUpdateService: SlowUpdateServerService.

Edit guzz.xml, declare the SlowUpdateServerService:
`<service name="slowUpdateServer" configName="guzzSlowUpdateServer" class="org.guzz.service.db.impl.SlowUpdateServerImpl" />`

Edit messageBoard.properties, add Service slowUpdateServer's detailed configurations:
```
[guzzSlowUpdateServer]
#batch size for updating the main database
batchSize=50

#page size for reading from the temporary table
pageSize=40

#how many pages to read from the temporary table for one loop updating
combinePageCount=10

#millseconds to wait for the next round of updates checking
updateInterval=500
```

Deploy and restart the system. Check tb\_guzz-su, the queue is consumed. Visit http://localhost:8080/guzz/messageList.jsp?userId=1 , the count works again.


# Delete a message, Batch Delete #

## Delete a message ##

Create a new Action to handle Message Delete: example.view.action.DeleteMessageAction.java:
```
package example.view.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.service.core.SlowUpdateService;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;
import example.business.User;

public class DeleteMessageAction implements Controller {
	
	private GuzzContext guzzContext ;
	
	private SlowUpdateService slowUpdateService ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		int msgId = RequestUtil.getParameterAsInt(request, "msgId", 0) ;
		
		//auto-commit
		WriteTranSession write = guzzContext.getTransactionManager().openRWTran(true) ;
		
		try{
			Guzz.setTableCondition(userId) ;
			Message msg = (Message) write.findObjectByPK(Message.class, msgId) ;
			
			if(msg != null){
				write.delete(msg) ;
				
				//dec the message count
				this.slowUpdateService.updateCount(User.class, null, "messageCount", userId, -1) ;
			}
		}finally{
			write.close() ;
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

	public SlowUpdateService getSlowUpdateService() {
		return slowUpdateService;
	}

	public void setSlowUpdateService(SlowUpdateService slowUpdateService) {
		this.slowUpdateService = slowUpdateService;
	}

}
```

add it to dispatcher-servlet.xml :
```
<bean name="/deleteMessage.do" class="example.view.action.DeleteMessageAction">
	<property name="guzzContext" ref="guzzContext" />
	<property name="slowUpdateService" ref="guzzSlowUpdateService" />
</bean>
```

Edit messageList.jsp, add a "delete" link to "./deleteMessage.do?userId=${m\_msg.userId}&msgId=${m\_msg.id}". Click the link, you are deleting a message. When a message is deleted, we also use slowUpdateService to dec user's message count.

## Batch Delete ##

Add a new Form in messageList.jsp. When submit the form, post all selected messages' ids to the server-side Action to perform the deleting in batch. The new messageList.jsp:
```
<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:get business="user" var="m_user" limit="id=${param.userId}" />

<g:boundary>
	<g:addLimit limit="userId=${m_user.id}" />
	<g:page business="message" var="m_messages" tableCondition="${m_user.id}" pageNo="${param.pageNo}" pageSize="30" orderBy="id desc" scope="request" />
</g:boundary>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
    <title>${m_user.userName}'s Message List</title>
  </head>
  
  <body>  	  
  	Leave a message:<br>
  	
  	<form method="POST" action="./newMessage.do">
  		<input type="hidden" name="userId" value="${m_user.id}" />
  		
  		<textarea name="content" cols="80" rows="10"></textarea>
  		
  		<br/>
  		<input type="submit" />
  	</form>
  	
  	<hr>
  	<form method="POST" action="./deleteMessage.do">
  		<input type="hidden" name="userId" value="${m_user.id}" />
  		
	  	<table width="96%" border="1">
	  		<tr>
	  			<th>No.</th>
	  			<th>Vote</th>
	  			<th>Content</th>
	  			<th>Date</th>
	  			<th>OP</th>
	  		</tr>
	  		
	  		<c:forEach items="${m_messages.elements}" var="m_msg">
	  		<tr>
	  			<td><input type="checkbox" name="ids" value="${m_msg.id}" />${m_messages.index}</td>
	  			<td>
	  				voteYes: <a href="./voteMessage.do?type=yes&userId=${m_msg.userId}&msgId=${m_msg.id}">${m_msg.voteYes}</a><br>
	  				voteNo: <a href="./voteMessage.do?type=no&userId=${m_msg.userId}&msgId=${m_msg.id}">${m_msg.voteNo}</a><br>
	  				voteScore: ${m_msg.voteScore}
	  			</td>
	  			<td>vote<g:out value="${m_msg.content}" escapeXml="false" escapeScriptCode="true" /></td>
	  			<td>${m_msg.createdTime}</td>
	  			<td><a href="./deleteMessage.do?userId=${m_msg.userId}&msgId=${m_msg.id}">Delete</a></td>
	  		</tr>
	  		</c:forEach>
	  	</table>	  	
	  	<table width="96%" border="1">
	  		<tr>
	  			<c:import url="/WEB-INF/jsp/include/console_flip.jsp" />
	  		</tr>
	  	</table>
	  	
	  	<table width="96%" border="1">
	  		<tr>
	  			<td><input type="submit" value="Delete All Selected Messages" /></td>
	  		</tr>
	  	</table>	
  	</form>
  	
  </body>
</html>
```

Edit DeleteMessageAction.java to accept POST batch delete:
```
package example.view.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.guzz.Guzz;
import org.guzz.GuzzContext;
import org.guzz.jdbc.ObjectBatcher;
import org.guzz.orm.se.SearchExpression;
import org.guzz.orm.se.Terms;
import org.guzz.service.core.SlowUpdateService;
import org.guzz.transaction.ReadonlyTranSession;
import org.guzz.transaction.WriteTranSession;
import org.guzz.util.RequestUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import example.business.Message;
import example.business.User;

public class DeleteMessageAction implements Controller {
	
	private GuzzContext guzzContext ;
	
	private SlowUpdateService slowUpdateService ;

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId = RequestUtil.getParameterAsInt(request, "userId", 0) ;
		
		if("POST".equals(request.getMethod())){//Batch delete
			int[] ids = RequestUtil.getParameterAsIntArray(request, "ids", 0) ;
			
			if(ids.length = 0){
				return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
			}

			List<Message> msgs = null ;
			
			//load the Messages to delete.
			SearchExpression se = SearchExpression.forLoadAll(Message.class) ;
			se.setTableCondition(userId) ;
			se.and(Terms.in("id", ids)) ;
			
			//read from slave db.
			ReadonlyTranSession read = guzzContext.getTransactionManager().openDelayReadTran() ;
			try{
				msgs = read.list(se) ;
			}finally{
				read.close() ;
			}
			
			//Open write connections to the master db.
			WriteTranSession write = guzzContext.getTransactionManager().openRWTran(false) ;
			try{
				//Perform Batch operation.
				ObjectBatcher batcher = write.createObjectBatcher() ;
				batcher.setTableCondition(userId) ;
			
				for(Message msg : msgs){
					batcher.delete(msg) ;					
				}
				
				batcher.executeUpdate() ;
				
				write.commit() ;
			}catch(Exception e){
				write.rollback() ;
				
				throw e ;
			}finally{
				write.close() ;
			}
			
			//dec the message count
			this.slowUpdateService.updateCount(User.class, null, "messageCount", userId, -msgs.size()) ;
		}else{
			//Delete one message
			int msgId = RequestUtil.getParameterAsInt(request, "msgId", 0) ;
			
			//auto-commit
			WriteTranSession write = guzzContext.getTransactionManager().openRWTran(true) ;
			
			try{
				Guzz.setTableCondition(userId) ;
				Message msg = (Message) write.findObjectByPK(Message.class, msgId) ;
				
				if(msg != null){
					write.delete(msg) ;
					
					//dec the message count
					this.slowUpdateService.updateCount(User.class, null, "messageCount", userId, -1) ;
				}
			}finally{
				write.close() ;
			}
		}
		
		return new ModelAndView("redirect:/messageList.jsp", "userId", userId);
	}

	public GuzzContext getGuzzContext() {
		return guzzContext;
	}

	public void setGuzzContext(GuzzContext guzzContext) {
		this.guzzContext = guzzContext;
	}

	public SlowUpdateService getSlowUpdateService() {
		return slowUpdateService;
	}

	public void setSlowUpdateService(SlowUpdateService slowUpdateService) {
		this.slowUpdateService = slowUpdateService;
	}

}
```

When the requested method is "POST", we read the passed "ids" parameters to a int[.md](.md), and find all matched Messages with SearchExpression query. Then, create a batcher, and delete them.

Guzz provides two kinds of batch APIs, one is based on Object operations called "ObjectBatcher", the other is based on directly SQL statement called "SQLBatcher". In this example, we use ObjectBatcher.


# Appendix & Downloads #

The final fininshed project code: http://guzz.googlecode.com/svn/wiki/no-wikis/MessageBoard.zip (size: 31K)

The code doesn't contain any depended jars under /WebRoot/WEB-INF/lib/ .
