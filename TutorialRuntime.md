## Requirements： ##

> JDK：1.5+

> JDK: 1.4+ (Recompling the code yourself when required. The RCP and annotation won't be valid.)

> Servlet 2.4 is required under Web Containers.

> Guzz depends on dom4J, cglib and commons-log which can be found under the download page.

## Maven： ##

maven server:

```
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
	<groupId>org.guzz</groupId>  
	<artifactId>guzz</artifactId>  
	<version>1.3.0</version>  
	<packaging>jar</packaging>
	<description>A full stack data-layer solution framework with many unique features.</description>
	<url>http://code.google.com/p/guzz/</url>
	<licenses>
	    <license>
	    	<name>The Apache Software License, Version 2.0</name>
	      	<url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
	      	<distribution>repo</distribution>
		</license>
	</licenses>
	<scm>
	    <connection>scm:svn:http://guzz.googlecode.com/svn/trunk/</connection>
	    <developerConnection>scm:svn:https://guzz.googlecode.com/svn/trunk/</developerConnection>
	    <url>http://guzz.googlecode.com/svn/trunk/</url>
	</scm>
	
	<properties>
	    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
	    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
	    <lib.spring.version>3.0.5.RELEASE</lib.spring.version>
	</properties>

	<repositories>
		<repository>
			<id>official</id>
			<name>Maven Official Repository</name>
			<url>http://repo2.maven.org/maven2/</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
		
		<repository>
			<id>jboss</id>
			<name>Jboss Repository</name>
			<url>https://repository.jboss.org/nexus/content/repositories/public/</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
		
		<repository>
			<id>java.net</id>
			<name>Java.net Repository</name>
			<url>http://download.java.net/maven/2/</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>

	<dependencies>
	    <dependency>
	        <groupId>cglib</groupId>  
	        <artifactId>cglib-nodep</artifactId>  
	        <version>2.2.2</version>  
	    </dependency>
	    <dependency>  
	        <groupId>dom4j</groupId>  
	        <artifactId>dom4j</artifactId>  
	        <version>1.6.1</version>  
	    </dependency>
	    <dependency>  
	        <groupId>jaxen</groupId>  
	        <artifactId>jaxen</artifactId>  
	        <version>1.1.1</version>  
	    </dependency>
	    <dependency>  
	        <groupId>saxpath</groupId>  
	        <artifactId>saxpath</artifactId>  
	        <version>1.0-FCS</version>  
	    </dependency>
	    <dependency>
	    	<groupId>commons-logging</groupId>
	    	<artifactId>commons-logging</artifactId>
	    	<version>1.1.1</version>
	    </dependency>
	    <dependency>
	    	<groupId>org.eclipse.persistence</groupId>
	    	<artifactId>javax.persistence</artifactId>
	    	<version>2.0.0</version>
	    </dependency>
	    
	    <dependency>
	    	<groupId>junit</groupId>
	    	<artifactId>junit</artifactId>
	    	<version>3.8.1</version>
	    	<scope>test</scope>
	    </dependency>
	    
	    <dependency>  
	        <groupId>c3p0</groupId>  
	        <artifactId>c3p0</artifactId>  
	        <version>0.9.1.2</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
		    <groupId>commons-dbcp</groupId>
		    <artifactId>commons-dbcp</artifactId>
		    <version>1.3</version><!-- 1.4 is compiled with JDK6 -->
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>javax.servlet</groupId>
	    	<artifactId>servlet-api</artifactId>
	    	<version>2.5</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>javax.servlet</groupId>
	    	<artifactId>jstl</artifactId>
	    	<version>1.2</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>javax.servlet.jsp</groupId>
	    	<artifactId>jsp-api</artifactId>
	    	<version>2.1</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>org.springframework</groupId>
	    	<artifactId>spring-core</artifactId>
		   <version>${lib.spring.version}</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>org.springframework</groupId>
	    	<artifactId>spring-beans</artifactId>
		   <version>${lib.spring.version}</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>org.springframework</groupId>
	    	<artifactId>spring-context</artifactId>
		   <version>${lib.spring.version}</version>
	    	<scope>provided</scope>
	    </dependency>	    
	    <dependency>
		   <groupId>org.springframework</groupId>
		   <artifactId>spring-tx</artifactId>
		   <version>${lib.spring.version}</version>
	    	<scope>provided</scope>
		</dependency>
		<dependency>
		   <groupId>org.springframework</groupId>
		   <artifactId>spring-jdbc</artifactId>
		   <version>${lib.spring.version}</version>
	    	<scope>provided</scope>
		</dependency>
	    <dependency>
		    <groupId>hessian</groupId>
		    <artifactId>hessian</artifactId>
		    <version>3.0.1</version>
	    	<scope>provided</scope>
		</dependency>
	    <dependency>
	    	<groupId>mysql</groupId>
	    	<artifactId>mysql-connector-java</artifactId>
	    	<version>5.1.16</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>org.springframework</groupId>
	    	<artifactId>spring-web</artifactId>
	    	<version>3.0.5.RELEASE</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>org.phprpc</groupId>
	    	<artifactId>phprpc-client</artifactId>
	    	<version>1.0</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>com.oracle</groupId>
	    	<artifactId>ojdbc14</artifactId>
	    	<version>10.2.0.1.0</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>com.h2database</groupId>
	    	<artifactId>h2</artifactId>
	    	<version>1.3.156</version>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>javax.transaction</groupId>
	    	<artifactId>jta</artifactId>
	    	<version>1.1</version>
	    	<type>pom</type>
	    	<scope>provided</scope>
	    </dependency>
	    <dependency>
	    	<groupId>javax.transaction</groupId>
	    	<artifactId>transaction-api</artifactId>
	    	<version>1.1</version>
	    	<type>pom</type>
	    	<scope>provided</scope>
	    </dependency>
	</dependencies>
  
  	<!-- Plugins-->
	<build>		
		<finalName>guzz</finalName>
		<directory>target</directory>
		<sourceDirectory>src/main/java</sourceDirectory>
		<outputDirectory>target/classes</outputDirectory>
		<testSourceDirectory>src/test/java</testSourceDirectory>
		<testOutputDirectory>target/test-classes</testOutputDirectory>
		<defaultGoal>install</defaultGoal>
		
		<testResources>
			<testResource>
				<directory>src/test/resources</directory>
				<filtering>false</filtering>
			</testResource>
		</testResources>
	
		<pluginManagement>
			<plugins>
				<!-- compiler -->
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-compiler-plugin</artifactId>
					<version>2.3.2</version>
					<configuration>
						<source>1.5</source>
						<target>1.5</target>
                    </configuration>
				</plugin>

				<!-- test -->
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-surefire-plugin</artifactId>
					<version>2.9</version>
					<configuration>
						<forkMode>always</forkMode> 
						<testClassesDirectory>target/test-classes</testClassesDirectory>
					</configuration>
				</plugin>

				<!-- resource -->
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-resources-plugin</artifactId>
					<version>2.5</version>
					<configuration>
						<encoding>${project.build.sourceEncoding}</encoding>
					</configuration>
				</plugin>

				<!-- eclipse -->
				<plugin>
					<groupId>org.apache.maven.plugins</groupId>
					<artifactId>maven-eclipse-plugin</artifactId>
					<version>2.8</version>
					<configuration>
						<sourceExcludes>
							<sourceExclude>**/.svn/</sourceExclude>
						</sourceExcludes>
						<downloadSources>true</downloadSources>
					</configuration>
				</plugin>
				
			</plugins>
		</pluginManagement>
	</build>

</project>

```

maven client:

```
<dependency>  
	<groupId>org.guzz</groupId>  
	<artifactId>guzz</artifactId>  
	<version>1.3.0</version>  
</dependency>
```

## How to create a guzz project? ##

> We suggest that you download the empty project of Guzz, and import it into Eclipse/MyEclipse to have the start.
