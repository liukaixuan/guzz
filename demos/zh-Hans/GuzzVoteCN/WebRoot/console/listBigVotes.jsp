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
    <title>投票列表</title>
    <link rel="stylesheet" type="text/css" href="../css/style.css"/>
  </head>
  
  <body>
  <div id="page">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td valign="top" class="right">
  	<div class="cornerNav">
     	投票管理
     	&nbsp;&nbsp;&nbsp;<a href="bigVoteAction.do">创建新的投票</a>
    </div>
        
    <div class="text_sta">
     	<table width="99%" class="statistics" border="0" cellspacing="0" cellpadding="0">
     	<thead>
    		<tr>
     			<td>投票号</td>
     			<td>投票名称</td>
     			<td>状态</td>
     			<td>地区统计策略</td>
     			<td>唯一投票人数</td>
     			<td>得票数</td>
     			<td>追加得票数</td>
     			<td>创建时间</td>
     			<td>管理投票</td>
     		</tr>
     	</thead>
     		
     		<c:forEach items="${m_votes.elements}" var="m_vote">
     		
  			<tr bgcolor="#f6f6f6">
     			<td>${m_vote.id}</td>
     			<td><span class="blue"><a href="../viewVoteAction.do?voteId=${m_vote.id}" target="_blank">${m_vote.name}</a></span></td>
     			<td>
     				<c:if test="${!m_vote.openToPublicNow}">
     					<font class="forbidden">关闭</font>
     				</c:if>
     				<c:if test="${m_vote.openToPublicNow}">
     					<font class="permit">对外开放</font>
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
     				<span class="blue">[<a href="bigVoteAction.do?id=${m_vote.id}">修改</a>]</span><br/>
     				<span class="blue">[<a href="listVoteItems.jsp?voteId=${m_vote.id}">管理投票</a>]</span>
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
