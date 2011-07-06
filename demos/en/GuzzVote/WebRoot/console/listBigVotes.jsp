<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp" %>

<g:boundary>
	<c:if test="${param.cid > 0}">
		<g:addLimit limit="channelId=${param.cid}" />
	</c:if>
	<g:addLimit limit="${consoleUser}" />
	
	<g:page var="m_votes" business="bigVote" orderBy="id desc" pageNo="${param.pageNo}" pageSize="20" />
</g:boundary>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Vote List</title>
    <link rel="stylesheet" type="text/css" href="../css/style.css"/>
  </head>
  
  <body>
  <div id="page">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td valign="top" class="right">
  	<div class="cornerNav">
     	Vote Management
     	&nbsp;&nbsp;&nbsp;<a href="bigVoteAction.do">create a new vote</a>
    </div>
        
    <div class="text_sta">
     	<table width="99%" class="statistics" border="0" cellspacing="0" cellpadding="0">
     	<thead>
    		<tr>
     			<td>Vote No</td>
     			<td>Vote Name</td>
     			<td>status</td>
     			<td>city policy</td>
     			<td>vote people</td>
     			<td>user votes</td>
     			<td>appended votes</td>
     			<td>createdTime</td>
     			<td>operations</td>
     		</tr>
     	</thead>
     		
     		<c:forEach items="${m_votes.elements}" var="m_vote">
     		
  			<tr bgcolor="#f6f6f6">
     			<td>${m_vote.id}</td>
     			<td><span class="blue"><a href="../viewVoteAction.do?voteId=${m_vote.id}" target="_blank">${m_vote.name}</a></span></td>
     			<td>
     				<c:if test="${!m_vote.openToPublicNow}">
     					<font class="forbidden">Closed</font>
     				</c:if>
     				<c:if test="${m_vote.openToPublicNow}">
     					<font class="permit">Open</font>
     				</c:if>
     				
     				<c:if test="${m_vote.beginTime != null || m_vote.endTime != null}">
     					<img style="vertical-align: bottom; cursor: pointer;" title="<g:out value='${m_vote.openTimeDesc}' escapeXml='false' />" src="../css/clock.gif" height="28px" />
     				</c:if>     			
     			<br></td>
     			<td>${m_vote.territoryPolicy}<br></td>
     			<td>${m_vote.votePeople}<br></td>
     			<td>${m_vote.voteNum}<br></td>
     			<td>${m_vote.addedVoteNum}<br></td>
     			<td><fmt:formatDate value="${m_vote.createdTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>     			
     			<td>
     				<span class="blue">[<a href="bigVoteAction.do?id=${m_vote.id}">Modify</a>]</span><br/>
     				<span class="blue">[<a href="listVoteItems.jsp?voteId=${m_vote.id}">Manage It</a>]</span>
     			</td>
     		</tr>
     		</c:forEach>
     	</table>
     	
     	<table border="0" width="96%" align="center" class="data">
     		<tr align="left">
     			<c:import url="/WEB-INF/jsp/include/console_flip.jsp" />
     		</tr>
     	</table>
    </div>
    </td></tr></table></div>
  </body>
</html>
