<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>投票自定义属性管理</title>
    <link rel="stylesheet" type="text/css" href="../css/style.css" />
  </head>
  
  <body>
  <div id="page">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td valign="top" class="right">
  	<div class="nav">
     	投票管理  &gt;&gt; 自定义属性管理
    </div>
    
    
    <div class="text">
    	<form method="POST">
    		<input type="hidden" name="id" value="${param.id}" />
    		<input type="hidden" name="voteId" value="${param.voteId}" />
    	    	
     		<spring:bind path="voteProp.prop.paramName">
     		<div class="cell"><br/><label>参数名：</label>
     				<input type="text" name="${status.expression}" value="${status.value}" maxlength="64" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     				<nobr>投票表单中的输入框名称。只能包含字母数字和下划线。不允许出现中文！</nobr>
     				
     		</div>	
     		</spring:bind>
     		<spring:bind path="voteProp.prop.showName">
     		<div class="cell"><br/><label>中文名称：</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="255" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.mustProp">
     		<div class="cell"><br/><label>是否为必填项：</label>
     				<input type="radio" name="${status.expression}" value="true" <c:if test="${status.value}">checked</c:if> /> 投票时必须填写
     				<input type="radio" name="${status.expression}" value="false" <c:if test="${!status.value}">checked</c:if> /> 可以选择性填写
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.validValues">
     		<div class="cell"><br/><label>有效值：</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="255" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     				<nobr>多个值之间用分号分开。如果可以填写任意值，留空。</nobr>
     				<br>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.defaultValue">
     		<div class="cell"><br/><label>默认值：</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="64" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.validRuleName">
     		<div class="cell"><br><label>有效性验证规则：</label>     			
     				<select name="${status.expression}" >
     					<option value="">不做限制</option>     					
     					<c:forEach items="${validatorNames}" var="m_name">
     						<option value="${m_name}" <c:if test="${status.value eq m_name}">selected</c:if> >${m_name}</option>
     					</c:forEach>     				
     				</select>
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.ruleParamValue">
     		<div class="cell"><br><label>有效性规则参数：</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="255" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     				一般不用填写。
     		</div>
     		</spring:bind>
     		
     		<br/><br/>
	     	<div class="button">      		
	     		<input type="submit" value="提交"  />
				<input type="reset" value="取消" />
	     	</div>
     	</form>
    </div>
    
    </td></tr></table></div>
  </body>
</html>
