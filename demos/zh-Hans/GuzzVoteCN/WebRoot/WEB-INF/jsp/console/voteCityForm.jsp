<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>投票地区管理</title>
    <link rel="stylesheet" type="text/css" href="../css/style.css"/>
  </head>
  
  <body>
  <div id="page">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td valign="top" class="right">
  	<div class="nav">
     	投票管理 &gt;&gt; 维护投票地区
    </div>
    
    
    <div class="text">
    	<form method="POST">
    		<input type="hidden" name="id" value="${param.id}" />
    		<input type="hidden" name="voteId" value="${param.voteId}" />
    	    	
     		<spring:bind path="cityForm.city.name">
     		<div class="cell"><label>地区名称：</label>
	     			<input type="text" name="${status.expression}" value="${status.value}" maxlength="64" size="42" />
	     			<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="cityForm.city.addedVoteNum">
     		<div class="cell"><label>手工干预投票数：</label>
     				<input type="text" name="${status.expression}" value="${status.value}" maxlength="64" size="42" /><nobr>
     				&nbsp;正数增加得票，负数减少得票。
     			<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %></nobr>
     			</div>
     		</spring:bind>
     		
     		<div class="cell"><label><nobr>（此处修改后将会自动更新到总投票次数中，请注意到地区中修改干预值以保持数据一致【所有选项得票数=总票数=所有地区得票数】。）
     		
     		</nobr></label></div>
     		         		
     		<div class="botton">
     			<input type="submit" value="提交" class="bot" />
				<input type="reset" value="取消" class="bot"/>
     		</div> 	
     	
     	</form>
    </div>
    </td></tr></table></div>
    
  </body>
</html>
