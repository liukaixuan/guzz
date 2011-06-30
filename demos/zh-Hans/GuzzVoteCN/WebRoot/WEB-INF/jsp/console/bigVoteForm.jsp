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
    <title>投票管理</title>
    <link rel="stylesheet" type="text/css" href="../css/style.css"/>
    <script language="javascript" type="text/javascript" src="../js/utils.js"></script>
    <script language="javascript" type="text/javascript" src="../js/Calendar_Obj.js"></script>
	<script language="javascript" type="text/javascript" src="../js/MyCalendar.js"></script>
	<script language="javascript" type="text/javascript" src="../js/calendar_lang/cn_utf8.js"></script>
	<link rel="stylesheet" type="text/css" href="../js/calendar_lang/calendar-blue.css"/>
  </head>
  
  <body>
    <div id="page">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td valign="top" class="right">
  	<div class="cornerNav">
     	投票管理&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
    </div>
    
    
    <div class="text">
    	<form method="POST">
    		<input type="hidden" name="id" value="${param.id}" />
     		<spring:bind path="bigVoteForm.vote.name">
     		<div class="cell"><label>投票名称：</label>
     			<nobr><input type="text" name="${status.expression}" value="${status.value}" maxlength="128" size="42" class="name" /><font color="red">&nbsp;*&nbsp;</font></nobr>
     			<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		
     		</spring:bind>
     		<spring:bind path="bigVoteForm.vote.channelId">
     			<div class="cell"><label>所属频道：</label>
     				<select name="${status.expression}">
     					<c:forEach items="${m_channels}" var="m_channel">
     						<option value="${m_channel.id}" <c:if test="${m_channel.id eq status.value}">selected</c:if>><c:out value="${m_channel.name}" /></option>
     					</c:forEach>
     				</select>
     				
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="bigVoteForm.vote.territoryPolicy">
     		<div class="cell"><label>地区维护策略：</label>
     				<select name="${status.expression}">
     					<c:forEach items="${territoryPolicies}" var="m_policy">
     						<option value="${m_policy.key}" <c:if test="${m_policy.key eq status.value}">selected</c:if>><c:out value="${m_policy.value}" /></option>
     					</c:forEach>
     				</select>
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     				</div>
     		</spring:bind>
     		<spring:bind path="bigVoteForm.vote.maxItemsPerVote">
     		<div class="cell"><label>允许选择最多项：</label>
     			<input type="text" name="${status.expression}" value="${status.value}" maxlength="4" size="42" class="name"/><nobr><font color="red">&nbsp;*&nbsp;</font>最多100</nobr>
     			<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		
     		<spring:bind path="bigVoteForm.startTime">
     		<div class="cell"><label>投票开始时间：</label>
     				<script language="JavaScript" type="text/javascript">
						JSCalendar.drawCalendar('${status.expression}', "<g:out value='${status.value}' default='${bigVoteForm.vote.beginTimeStr}' />", '%Y-%m-%d %H:%M', '24', true, true, "width:175px");
					</script><%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="bigVoteForm.endTime">
     		<div class="cell"><label>投票结束时间：</label>
	     			<script language="JavaScript" type="text/javascript">
						JSCalendar.drawCalendar('${status.expression}', "<g:out value='${status.value}' default='${bigVoteForm.vote.endTimeStr}' />", '%Y-%m-%d %H:%M', '24', true, true, "width:175px");
					</script><%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		<spring:bind path="bigVoteForm.vote.status">
     		<div class="cell"><label>状态：</label>
     				<select name="${status.expression}" >
     					<option <c:if test="${status.value == 0}"> selected</c:if> value="0">关闭</option>
     					<option <c:if test="${status.value == 1}"> selected</c:if>  value="1">开启</option>
     				</select><nobr><font color ="red">&nbsp;*&nbsp;</font>投票在关闭状态下将禁止投票</nobr>
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<c:if test="${bigVoteForm.new}">
     		<spring:bind path="bigVoteForm.addChineseProvinces">
     		<div class="cell"><label>默认投票地区：</label>
     				<nobr>
     				<select name="${status.expression}">
     					<option value="true">默认添加所有中国省和直辖市</option>
     					<option value="false">手工添加</option>
     				</select>
     				</nobr></div>
     		</spring:bind>
     		</c:if>
     		
     		<div class="botton">
     			<input type="submit" value="提交" class="bot" />
     			<input type="reset" value="取消" class="bot"/>
     		</div>
     		    	
     	
     	</form>
    </div>
    
</td></tr></table></div>   
  </body>
</html>
