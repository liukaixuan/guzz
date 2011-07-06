<%@ page pageEncoding="UTF-8"%>
<%@page session="false"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp" %>

<c:set var="m_vote" value="${bigVoteTree.bigVote}" />
<c:set var="m_voteItems" value="${bigVoteTree.voteItems}" />
<c:set var="m_cities" value="${bigVoteTree.voteTerritories}" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf8" /> 
<title>vote</title>
<link rel="stylesheet" href="css/master_style.css" type="text/css" />
</head>
<body>
 
<div>

<div style="height: 50px; vertical-align: middle;">
	<br/>
	Total Votes:${m_vote.voteNum}
</div>

<table width="920" border=0 cellpadding=0 cellspacing=0 bgcolor="#721C00">
<tr>
<td valign="top" width="920" bgcolor="#FFFFFF" align="left">


<form method="post" action="makeVoteAction.do" id="voteForm">
    	<input type="hidden" name="voteId" value="${m_vote.id}" />
    	<input type="hidden" name="type" value="html" />
    	
	<table CELLPADDING="10" CELLSPACING="0" width="100%">

 		<tr></tr>
    		
    		    		
    		<tr>
    		<table width="100%" border="0" cellpadding="10" cellspacing="0">
	    		<tr style="height: 1px;"><td></td><td></td><td></td><td></td></tr>
	    		<c:forEach items="${m_voteItems}" var="m_item" varStatus="m_status">
	    			<c:if test="${m_status.index % 4 == 0}">
	    				<tr>
	    			</c:if>
	    			
	    			<td>
		    			<input type="checkbox" name="items" id="items_${m_item.id}" value="${m_item.id}">
		    			<label for="items_${m_item.id}">
		    				<c:if test="${not empty m_item.showName}">
		    					<c:out value="${m_item.showName}" escapeXml="false" />
		    				</c:if>
		    				<c:if test="${empty m_item.showName}">
		    					<c:out value="${m_item.name}" escapeXml="false" />
		    				</c:if>
		    			</label>
    				</td>
    				
    				<c:if test="${m_status.index % 4 == 3}">
    					<tr><td><br/></td></tr>
	    				</tr>
	    			</c:if>
	    		</c:forEach>    			
    		</table>
    		</tr>
<tr></tr>
</table>
<table border=0 WIDTH="100%" bgcolor="#eeeeee" height="50">
<tr>

<td style="font-size:12px" width="99%" align="center">
	
	Area:<select name="cityId">
	 				<option value="-1">Choose you area:</option>
	 				<c:forEach items="${m_cities}" var="m_city">
	 					<option value="${m_city.id}">${m_city.name}</option>
	 				</c:forEach>
	 			</select>
	 			
	<input type="submit" name="submitBtn" value="Make a Vote" class="button"/>	
	&nbsp;<input type="reset" value="reselect" class="button"/>
</td>
</tr>
</table>

</form>

</td></tr>
</table>

<div style="text-align: center; padding-top: 10px;">
	Powered By <a href="http://www.guzz.org/" target="_blank">guzz</a>
</div>

</div>

</body>
</html>