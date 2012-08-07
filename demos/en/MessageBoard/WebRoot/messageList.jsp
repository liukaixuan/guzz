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
