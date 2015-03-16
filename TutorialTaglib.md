## Setup guzz taglib ##

> Two steps:
  * Introduce guzz to your system, make it run.
  * Add a taglib declaration: <%@ taglib uri="http://www.guzz.org/tags" prefix="g" %> in your JSP file.

> The tld definition is stored under META-INF in guzz.jar.

## Available tags: ##

> -**Tags:**

| **Tag**  | **Function**  | **Support content?**  |
|:---------|:--------------|:----------------------|
| g:boundary  | Add a "limit" search term to all tags inside this tag. | Yes  |
| g:addLimit  | Add a search term to all tags below this tag within a g:boundary. | No  |
| g:addInLimit  | Add a "in" search term to all tags below this tag within a g:boundary. | No  |
| g:count  | Execute a number count query. The default count is "select count(**) ..." from db.**| No  |
| g:get  | Query a list from database, and return the first one. | No  |
| g:list  | Query a list. | No  |
| g:page  | Query a page. The default pagination is stored and returned as org.guzz.dao.PageFlip.  | No  |
| g:out  | Output a string. The tag is similar to c:out, but with more features. <br>It supports: escaping javascript, escaping special characters in xslt, outputing to be a javascript string, native2ascii.<br><br> g:out escapes xml and javascript in default, turn this two attributes to "false" if you need to output xml and javascript string. <table><thead><th> No </th></thead><tbody>
<tr><td> g:inc  </td><td> Increase the database value of a property by the primary key. Service <a href='AppendCoreService.md'>Increment Queue Service</a> is required to active this tag. </td><td> No  </td></tr></tbody></table>

<h2>Attributes in tags:</h2>

<blockquote>In all tags, if the attribute name is the same, the function is the same too. In all tags, the property name should be the property name of java pojo class, not column name in database if there is no explicit declaration.</blockquote>

<blockquote>-<b>Primary attributes:</b></blockquote>

<table><thead><th> <b>Attribute</b>  </th><th> <b>Function</b>  </th><th> <b>Notes</b>  </th></thead><tbody>
<tr><td> limit  </td><td> Search term. eg: userName=${param.name}; status=1; checked=true. <br>Guzz also supports user-defined limit, such as "checkedPost". To support this feature, you have to write a interpreter, read more in pagination section below.<br><br><b>Warnings: one "limit", one search term. Combing two or more search terms by "and"/"or" inside a "limit" is not allowed.</b><br>To enable combing terms querying, you have to declare a g:boundary, and add as many terms as you like by g:addLimit or g:addInLimit inside it.</td><td> One "limit", one search term.<br>No combing operators in "limit". </td></tr>
<tr><td> var  </td><td> Store result to this variable in JSP. Similar to "var" in c:set.  </td><td> - </td></tr>
<tr><td> scope  </td><td> Store scope. Similar to "scope" in c:set. </td><td> page,request,session,application </td></tr>
<tr><td> business  </td><td> The business domain object to query. This should be the attribute value of "name" of "business" tag in guzz.xml, or the full qualified class name of the domain object. </td><td>- </td></tr>
<tr><td> tableCondition  </td><td> How to split tables? Read more in chapters Shadow Table and Custom Table. </td><td>- </td></tr>
<tr><td> skipCount  </td><td> Skip the first giving records. The default value is zero, means no skipping. </td><td>- </td></tr>
<tr><td> pageNo  </td><td> which page of the records to read. The first page is 1, the second is 2...  </td><td>- </td></tr>
<tr><td> pageSize  </td><td> page size </td><td>- </td></tr>
<tr><td> orderBy  </td><td> The order by in query. Separate orderBys by comma. eg: name asc, id desc, friendCount asc </td><td>- </td></tr>
<tr><td> test  </td><td> Used by g:addLimit and g:addInLimit，and is similar to "test" in c:if. <br><br>If the test is true, add the condition; Or, ignore it. The default value is true. </td><td>- </td></tr></tbody></table>

<blockquote>-<b>Operators supported in "limit" attribute:</b></blockquote>

<table><thead><th> <b>operator</b> </th><th> <b>function</b> </th><th> <b>notes</b> </th></thead><tbody>
<tr><td> =  </td><td> EQUAL </td><td> equals </td></tr>
<tr><td> == </td><td> EQUAL </td><td> equals </td></tr>
<tr><td> =~= </td><td> EQUAL_IGNORE_CASE </td><td> string equals(case insensitive) </td></tr>
<tr><td> >  </td><td> BIGGER </td><td> bigger than </td></tr>
<tr><td> <  </td><td> SMALLER  </td><td> smaller than </td></tr>
<tr><td> >= </td><td> BIGGER_OR_EQUAL </td><td> bigger or equals </td></tr>
<tr><td> <= </td><td> SMALLER_OR_EQUAL </td><td> smaller or equals </td></tr>
<tr><td> != </td><td> NOT_EQUAL </td><td> not equals </td></tr>
<tr><td> <> </td><td> NOT_EQUAL </td><td> not equals </td></tr>
<tr><td> <code>~~</code> </td><td> LIKE_IGNORE_CASE </td><td> string like(case insensitive).<br><br>Add the "like" matching rules yourself as in sqls. % matches any characters, ? matches one. <br>eg: limit="name<code>~~</code>%${param.name}%" </td></tr>
<tr><td> ~= </td><td> LIKE_CASE_SENSTIVE </td><td> string like(case sensitive or not is decided by the column definition by the database). Notes as above. </td></tr></tbody></table>

<h2>How to use?</h2>

<blockquote>Guzz's taglib is designed to handle database operations, to simplify your works on reading data. Most of the time, it would be used with JSP core taglib.</blockquote>

<h3>Single Search Term</h3>

<blockquote>If your query has no condition or based only on one condition, you can query data by get/list/page tag directly.</blockquote>

<blockquote>For example, we have to analyze the popularity of cities in a country, and pass a parameter "cid" to identify the country.</blockquote>

<pre><code>&lt;g:get var="m_coutry" business="userCoutry" limit="id=${param.cid}" /&gt;<br>
<br>
&lt;c:if test="${empty param.order}"&gt;<br>
	&lt;g:list var="m_cities" business="userCity" limit="coutryId=${m_coutry.id}" orderBy="id asc" pageSize="200" /&gt;<br>
&lt;/c:if&gt;<br>
&lt;c:if test="${not empty param.order}"&gt;<br>
	&lt;g:list var="m_cities" business="userCity" limit="coutryId=${m_coutry.id}" orderBy="${param.order}" pageSize="200" /&gt;<br>
&lt;/c:if&gt;<br>
<br>
&lt;g:count var="m_cityAddedFavNum" selectPhrase="sum(addedFavNum)" business="userCity" limit="coutryId=${m_coutry.id}" /&gt;<br>
<br>
&lt;g:count var="m_maxCityFavNum" business="userCity" selectPhrase="max(favNum)" limit="coutryId=${m_coutry.id}" /&gt;<br>
&lt;g:count var="m_maxCityAddedFavNum" business="userCity" selectPhrase="max(addedFavNum)" limit="coutryId=${m_coutry.id}" /&gt;<br>
<br>
&lt;g:count var="m_cityFavPeopleBase" business="userCity" selectPhrase="max(favPeople)" limit="coutryId=${m_coutry.id}" /&gt;<br>
&lt;c:set var="m_cityFavNumBase" value="${m_maxCityFavNum + m_maxCityAddedFavNum }" /&gt;<br>
<br>
</code></pre>

<h3>Combing Search Terms:</h3>

<blockquote>Use g:boundary and g:limit tags if you have one more conditions in a query. For example:</blockquote>

<pre><code>&lt;g:boundary&gt;<br>
	&lt;g:addInLimit test="${!consoleUser.systemAdmin}" name="authGroup" value="${consoleUser.authGroups}" /&gt;<br>
	&lt;g:addLimit limit="channelStatus=checked" /&gt;<br>
<br>
	&lt;g:list var="m_channels" business="channel" orderBy="id desc" pageSize="20" /&gt;<br>
&lt;/g:boundary&gt;<br>
</code></pre>

<blockquote>We list the first 20 checked channels the user is authorized, or the first 20 checked channels if the user is a system administrator.</blockquote>

<blockquote>The combined conditions are jointed by "and" operator. Taglib doesn't support "or" operator.</blockquote>

<h3>Page Query & Pagination</h3>

<blockquote>The g:page is used to query data of a page, and compute its paginations. The g:page can be thought as a combination of g:list and g:count.</blockquote>

<blockquote>For example:</blockquote>

<pre><code>&lt;g:boundary&gt;<br>
	&lt;c:if test="${param.cid &gt; 0}"&gt;<br>
		&lt;g:addLimit limit="channelId=${param.cid}" /&gt;<br>
	&lt;/c:if&gt;<br>
	&lt;g:addLimit limit="${consoleUser}" /&gt;<br>
	<br>
	&lt;g:page var="m_votes" business="blogVote" orderBy="id desc" pageNo="${param.pageNo}" pageSize="20" /&gt;<br>
&lt;/g:boundary&gt;<br>
</code></pre>

<blockquote>In this example, we read all authorized votes in a channel or all channels if no "cid" is passed in one page. The pageNo is retrieved from parameter "pageNo", and every page contains at most 20 records.</blockquote>

<blockquote>Note that:</blockquote>

<pre><code>&lt;g:addLimit limit="${consoleUser}" /&gt;<br>
</code></pre>

<blockquote>The "limit" is a user-defined one, indicates the condition of "current login console user". This is a advanced usage, <a href='BlogVoteInterpreter.md'>Read More</a>.</blockquote>

<blockquote>The page data stored in "var" by g:page is a instance of org.guzz.dao.PageFlip. You can fetch its records by elements().</blockquote>

<blockquote>Besides variable of attribute "var", g:page also store the data to another inner variable "PAGE_FLIP". If all your pagination displays are similar, this can be very useful for you to write a public pagination jsp.</blockquote>

<blockquote>Example:</blockquote>

<pre><code>&lt;%@ taglib uri="http://www.guzz.org/tags" prefix="g" %&gt; <br>
&lt;%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %&gt; <br>
<br>
&lt;g:page var="m_channels" business="channel" orderBy="id desc" pageNo="${param.pageNo}" pageSize="20" /&gt;<br>
....<br>
&lt;c:forEach items="${m_channels.elements}" var="m_channel"&gt;<br>
     		&lt;tr&gt;<br>
     			&lt;td&gt;${m_channel.id}&lt;/td&gt;<br>
     			&lt;td&gt;${m_channel.name}&lt;/td&gt;<br>
                        .....<br>
     		&lt;/tr&gt;<br>
&lt;/c:forEach&gt;<br>
....<br>
&lt;table border="0" width="96%" align="center" class="data"&gt;<br>
     		&lt;tr align="left"&gt;<br>
     			&lt;c:import url="/WEB-INF/jsp/include/console_flip.jsp" /&gt;<br>
     		&lt;/tr&gt;<br>
&lt;/table&gt;<br>
....<br>
</code></pre>

<blockquote>In the example, we display data by variable "m_channels", and also do pagination by including a public jsp: /WEB-INF/jsp/include/console_flip.jsp.</blockquote>

<blockquote>The content of console_flip.jsp is :</blockquote>

<pre><code>&lt;%@ page contentType="text/html;charset=UTF-8"%&gt;<br>
&lt;%@ taglib uri="http://www.guzz.org/tags" prefix="g" %&gt; <br>
&lt;%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %&gt; <br>
<br>
&lt;!-- we need property PAGE_FLIP --&gt;<br>
		&lt;td class="page_bar"&gt;<br>
			Current page: &lt;c:out value="${PAGE_FLIP.pageNo}"/&gt;&amp;nbsp;total:&lt;c:out value="${PAGE_FLIP.pageCount}"/&gt; pages&amp;nbsp;<br>
			(total records:&lt;c:out value="${PAGE_FLIP.totalCount}"/&gt;)&amp;nbsp;	<br>
			&lt;c:forEach begin="${PAGE_FLIP.pageStart}" end="${PAGE_FLIP.pageEnd}" step="1" var="index"&gt;<br>
				&lt;c:if test="${index == PAGE_FLIP.pageNo}"&gt; <br>
					&lt;span style="color:red;font-weight:bold;"&gt;&lt;c:out value="${index }" /&gt;&lt;/span&gt; <br>
				&lt;/c:if&gt;<br>
				&lt;c:if test="${index != PAGE_FLIP.pageNo}"&gt; <br>
					&lt;a href='&lt;c:out value="${PAGE_FLIP.flipURL}" /&gt;&amp;&lt;c:out value="${PAGE_FLIP.webPageNoParam}" /&gt;=&lt;c:out value="${index}"/&gt;'&gt;<br>
                                        &lt;span style="text-decoration: none;color:gray;"&gt;&lt;c:out value="${index}" /&gt;&lt;/span&gt;&lt;/a&gt;  <br>
				&lt;/c:if&gt;&amp;nbsp;<br>
			&lt;/c:forEach&gt;<br>
		&lt;/td&gt;<br>
</code></pre>

<blockquote>PageFlip can not only provide the records, the pagination algorithm, but also all parameters in HTTP "GET" methods without "pageNo". With PageFlip, you can write a public pagination jsp, and use it for all "list data" features.</blockquote>

<blockquote>The implementation class of PageFlip can be designated in SearchExpression. The taglib doesn't support designate it for now.</blockquote>

<blockquote>It's highly recommended that every pagination is designed this way.</blockquote>

<h3>g:inc taglib:</h3>

<blockquote>-<b>Principle:</b> When a g:inc is executed, it inserts a record into a temporary table and returns. The record contains the table, row (by primary key), updated column and the increment value. In another thread, the records in the temporary table are read and combined (operation for the same row) to reduce sql count needed, and execute them in the formal database.</blockquote>

<blockquote>-<b>Attributes in g:inc:</b></blockquote>

<table><thead><th> <b>Attribute</b>  </th><th> <b>Function</b>  </th><th> <b>Notes</b>  </th></thead><tbody>
<tr><td> business  </td><td> the domain business to update. the business/name in guzz.xml, or the full qualified class name of the domain class. </td><td> - </td></tr>
<tr><td> updatePropName </td><td> the property to increase.  </td><td> - </td></tr>
<tr><td> pkValue  </td><td> primary key value </td><td> Can only be updated by primary key. </td></tr>
<tr><td> count  </td><td> Increment  </td><td> A positive value means add, a negative value means minus. </td></tr>
<tr><td> tableCondition </td><td> How to split table of the business object. Read more on Shadow Table and Custom Table. </td><td>- </td></tr></tbody></table>

<blockquote>-<b>Example:</b></blockquote>

<pre><code>&lt;g:inc business="blogArticle" pkValue="${article.id}" updatePropName="readCount" count="1" /&gt;<br>
<br>
&lt;g:inc business="comment" pkValue="${comment.id}" updatePropName="readCount" count="1" tableCondition="${article.channel}" /&gt;<br>
</code></pre>

<h2>Taglib Documentation</h2>

<blockquote><a href='http://www.guzz.org/docs/tlddocs/index.html'>http://www.guzz.org/docs/tlddocs/index.html</a></blockquote>
