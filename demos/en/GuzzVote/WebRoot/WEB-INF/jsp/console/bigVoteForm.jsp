<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp" %>

<g:boundary>
	<c:if test="${!consoleUser.systemAdmin}">
		<g:addInLimit name="authGroup" value="${consoleUser.authGroups}" />
	</c:if>

	<g:list var="m_channels" business="channel" orderBy="id asc" pageSize="200" />
</g:boundary>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Vote Management</title>
    <link rel="stylesheet" type="text/css" href="../css/style.css"/>
    <script language="javascript" type="text/javascript" src="../js/utils.js"></script>
    <script language="javascript" type="text/javascript" src="../js/Calendar_Obj.js"></script>
	<script language="javascript" type="text/javascript" src="../js/MyCalendar.js"></script>
	<script language="javascript" type="text/javascript" src="../js/calendar_lang/calendar-en.js"></script>
	<link rel="stylesheet" type="text/css" href="../js/calendar_lang/calendar-blue.css"/>
  </head>
  
  <body>
    <div id="page">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td valign="top" class="right">
  	<div class="cornerNav">
     	Vote Management&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    </div>
    
    
    <div class="text">
    	<form method="POST">
    		<input type="hidden" name="id" value="${param.id}" />
     		<spring:bind path="bigVoteForm.vote.name">
     		<div class="cell"><label>Vote Name:</label>
     			<nobr><input type="text" name="${status.expression}" value="${status.value}" maxlength="128" size="42" class="name" /><font color="red">&nbsp;*&nbsp;</font></nobr>
     			<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		
     		</spring:bind>
     		<spring:bind path="bigVoteForm.vote.channelId">
     			<div class="cell"><label>Channel:</label>
     				<select name="${status.expression}">
     					<c:forEach items="${m_channels}" var="m_channel">
     						<option value="${m_channel.id}" <c:if test="${m_channel.id eq status.value}">selected</c:if>><c:out value="${m_channel.name}" /></option>
     					</c:forEach>
     				</select>
     				
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="bigVoteForm.vote.territoryPolicy">
     		<div class="cell"><label>Area Policy:</label>
     				<select name="${status.expression}">
     					<c:forEach items="${territoryPolicies}" var="m_policy">
     						<option value="${m_policy.key}" <c:if test="${m_policy.key eq status.value}">selected</c:if>><c:out value="${m_policy.value}" /></option>
     					</c:forEach>
     				</select>
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     				</div>
     		</spring:bind>
     		<spring:bind path="bigVoteForm.vote.maxItemsPerVote">
     		<div class="cell"><label>Limits:</label>
     			<input type="text" name="${status.expression}" value="${status.value}" maxlength="4" size="42" class="name"/><nobr><font color="red">&nbsp;*&nbsp;</font>Max items can be selected one vote.</nobr>
     			<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		
     		<spring:bind path="bigVoteForm.startTime">
     		<div class="cell"><label>Start Time:</label>
     				<script language="JavaScript" type="text/javascript">
						JSCalendar.drawCalendar('${status.expression}', "<g:out value='${status.value}' default='${bigVoteForm.vote.beginTimeStr}' />", '%Y-%m-%d %H:%M', '24', true, true, "width:175px");
					</script><%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="bigVoteForm.endTime">
     		<div class="cell"><label>Finish Time:</label>
	     			<script language="JavaScript" type="text/javascript">
						JSCalendar.drawCalendar('${status.expression}', "<g:out value='${status.value}' default='${bigVoteForm.vote.endTimeStr}' />", '%Y-%m-%d %H:%M', '24', true, true, "width:175px");
					</script><%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		<spring:bind path="bigVoteForm.vote.status">
     		<div class="cell"><label>Status:</label>
     				<select name="${status.expression}" >
     					<option <c:if test="${status.value == 0}"> selected</c:if> value="0">Closed</option>
     					<option <c:if test="${status.value == 1}"> selected</c:if>  value="1">Open</option>
     				</select><nobr><font color ="red">&nbsp;*&nbsp;</font></nobr>
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<c:if test="${bigVoteForm.new}">
     		<spring:bind path="bigVoteForm.addChineseProvinces">
     		<div class="cell"><label>Area</label>
     				<nobr>
     				<select name="${status.expression}">
     					<option value="true">Add all provinces of China into the area list</option>
     					<option value="false">Add by myself</option>
     				</select>
     				</nobr></div>
     		</spring:bind>
     		</c:if>
     		
     		<div class="botton">
     			<input type="submit" value="submit" class="bot" />
     			<input type="reset" value="cancel" class="bot"/>
     		</div>
     		    	
     	
     	</form>
    </div>
    
</td></tr></table></div>   
  </body>
</html>
