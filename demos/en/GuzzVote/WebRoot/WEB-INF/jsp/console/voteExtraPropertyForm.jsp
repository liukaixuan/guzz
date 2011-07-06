<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>    
  	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>User-defined Form</title>
    <link rel="stylesheet" type="text/css" href="../css/style.css" />
  </head>
  
  <body>
  <div id="page">
<table width="100%" border="0" cellspacing="0" cellpadding="0">
  <tr>
  <td valign="top" class="right">
  	<div class="nav">
     	User-defined Form &gt;&gt; field
    </div>
    
    
    <div class="text">
    	<form method="POST">
    		<input type="hidden" name="id" value="${param.id}" />
    		<input type="hidden" name="voteId" value="${param.voteId}" />
    	    	
     		<spring:bind path="voteProp.prop.paramName">
     		<div class="cell"><br/><label>Parameter Name:</label>
     				<input type="text" name="${status.expression}" value="${status.value}" maxlength="64" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     				
     		</div>	
     		</spring:bind>
     		<spring:bind path="voteProp.prop.showName">
     		<div class="cell"><br/><label>Display Name:</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="255" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.mustProp">
     		<div class="cell"><br/><label>Required:</label>
     				<input type="radio" name="${status.expression}" value="true" <c:if test="${status.value}">checked</c:if> /> Must
     				<input type="radio" name="${status.expression}" value="false" <c:if test="${!status.value}">checked</c:if> /> Optional
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.validValues">
     		<div class="cell"><br/><label>Available Value:</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="255" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     				<nobr>separate by comma. Leave blank if any value is allowed.</nobr>
     				<br>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.defaultValue">
     		<div class="cell"><br/><label>Default Value:</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="64" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.validRuleName">
     		<div class="cell"><br><label>Valid Rule:</label>     			
     				<select name="${status.expression}" >
     					<option value="">No Limit</option>     					
     					<c:forEach items="${validatorNames}" var="m_name">
     						<option value="${m_name}" <c:if test="${status.value eq m_name}">selected</c:if> >${m_name}</option>
     					</c:forEach>     				
     				</select>
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     			</div>
     		</spring:bind>
     		
     		<spring:bind path="voteProp.prop.ruleParamValue">
     		<div class="cell"><br><label>Valid Rule Parameter:</label>
     				<input type="text" name="${status.expression}" value="<c:out value='${status.value}' escapeXml='true' />" maxlength="255" size="42" />
     				<%@ include file="/WEB-INF/jsp/include/error_inc.jsp" %>
     		</div>
     		</spring:bind>
     		
     		<br/><br/>
	     	<div class="button">      		
	     		<input type="submit" value="submit"  />
				<input type="reset" value="cancel" />
	     	</div>
     	</form>
    </div>
    
    </td></tr></table></div>
  </body>
</html>
