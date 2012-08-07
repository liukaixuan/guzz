<%@ page language="java" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ include file="/WEB-INF/jsp/include/tags.jsp"%>
<%
    Throwable ex = (Throwable) request.getAttribute("exception");

	if(ex == null){
		ex = exception ;
	}

	String msg = ex.getMessage() ;
	String tips = "" ;
	
	if(msg != null && msg.contains("exception")){
		
		msg = "Error occurs, please retry!" ;
	}else if (msg == null){
		msg = "Error occurs, please retry!" ;
	}
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Errors</title>
<link href="css/err.css" rel="stylesheet" type="text/css" />
</head>
<body>

<div id="top"></div>

<table>
	<tr>
		<td><div id="img" ><img src="css/error.jpg" width="170" height="170" /></div></td>
		<td>
			<div class="lan" id="menu" align="center" style="line-height: 2em;">
				Error:<%=msg%>
				
			</div>
		</td>
	</tr>
	<tr>
	<td>
	<ul>
			<li><a href="javascript:history.back(-1)" target="_top">Return Back!</a></li>
				</ul>
	</td>
	</tr>
</table>

<!--
详细错误: 
<% ex.printStackTrace(new java.io.PrintWriter(out)); %>
-->
</body>
</html>