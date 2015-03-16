## About Lob ##

> Lob is used to store big data. For different data types, it can be divided into blog and clob, and smallbob, raw, etc in some detailed database providers.

> In guzz, we use "clob" type for big asicll data, and "blog" for large binary data.

  * arning:**In lazy load mode, the caller is responsible for closing the Lob (call lob.close()) in guzz!**

## How to use Clob and Blob? ##

> Let's take a example to describe this.

> Now, we have a User Information table contains a column for portrait image in binary and a column for self-description. We store the portrait with a blob, and the self-description with a clob column.

> For concision, we'll show you the "blob" portrait only.

> -**Write the domain class**

```
public class UserInfo {
	
	private int id ;
	
	private String userId ;
	
	private TranClob aboutMe ;
	
	private TranBlob portraitImg ;
    
    //get&set methods....
}
```

> In this class, TranClob and TranBlob are enclosures for Clob and Blob by guzz. With these, Lob can be closed by the caller.

> Guzz won't leave a tail in lazy loading as hibernate's OpenSessionInView, so that is why you have to close the Lob yourself in pay.

> -**Declare the mapping:**

```
<property name="portraitImg" type="blob" column="portraitImg" lazy="true"></property>    
```

> -**Insert Blob:**

```
                FileInputStream fis = new FileInputStream(“a big file.png”) ;
		
		WriteTranSession tran = tm.openRWTran(false) ;		
		
		try{
			UserInfo info = new UserInfo() ;
			info.setUserId("lucy") ;
			info.setPortraitImg(Guzz.createBlob(fis)) ;
			tran.insert(info) ;

			tran.commit() ;
		}catch(Exception e){
			tran.rollback() ;			
		}finally{
			tran.close() ;
                        fis.close() ;
                }
```

> -**Update Blob：**

> For Oracle, you have to insert a empty Blob before updating it. The following code shows you how to do it.

```
                FileInputStream fis = new FileInputStream(“a big file.png”) ;
		
		WriteTranSession tran = tm.openRWTran(false) ;		
		byte[] tb = new byte[1] ;
		tb[0] = 1 ;
		try{
			//Insert a empty blob
			UserInfo info = new UserInfo() ;
			info.setUserId("lucy") ;
			info.setPortraitImg(Guzz.createBlob(tb)) ;
			tran.insert(info) ;
                        tran.commit() ;

			//Read and Update it with a update row lock
			TranBlob blob = (TranBlob) tran.loadPropForUpdate(info, "portraitImg") ;
		        blob.truncate(0) ;
		        blob.writeIntoBlob(fis, 1) ;
		        tran.commit() ;
		}catch(Exception e){
			tran.rollback() ;			
		}finally{
			tran.close() ;
                        fis.close() ;
                }
```

> -**Load and display a blob:**

> Read from database is a super easy task in guzz, we'd create a jsp file, and let guzz jsp taglib finish the job.

> The JSP file can be simply:

```
<%@page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.guzz.org/tags" prefix="g" %> 

<!-- Query userInfo from the database by uid -->
<g:get business="userInfo" limit="userId=${param.uid}" var="m_userInfo" />

<%
UserInfo info = (UserInfo) pageContext.getAttribute("m_userInfo") ;

//Fetch the Blob column. This is a lazy load, guzz will open a slave database connection to do the job.
TranBlob img = info.getPortraitImg() ;

//write the portrait to JSP output stream.
img.writeOut(out) ;

//Close the blob and its connection acquired in lazy loading.
img.close() ;

%>
```

> Finished!

> In this progress, we can conclude that writing operations in guzz is similar to hibernate, but readings would be much easier with guzz's taglib. We know, the most volatile parts in web development is reading and displaying; so, guzz can save you a lot of time in developing and deploying(changing a jsp file is much easier to deploy than changing a java class).
