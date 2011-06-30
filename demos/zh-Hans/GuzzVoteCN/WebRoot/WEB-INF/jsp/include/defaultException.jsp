<%@ page language="java" pageEncoding="UTF-8" %>
<%@ include file="/WEB-INF/jsp/include/tags.jsp"%>
<%
    Exception ex = (Exception) request.getAttribute("exception");

	String msg = ex.getMessage() ;
	String tips = "" ;
	
	if(msg != null && msg.contains("exception")){
		
		msg = "系统遇到错误，请重试。" ;
	}else if (msg == null){
		msg = "系统遇到错误，请重试。" ;
	}
	
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>错误提示</title>
<link href="css/err.css" rel="stylesheet"	type="text/css" />
</head>
<body>

<div id="top"></div>

<table>
	<tr>
		<td><div id="img" ><img src="css/error.jpg" width="170" height="170" /></div></td>
		<td>
			<div class="lan" id="menu" align="center" style="line-height: 2em;">
				错误：<%=msg%>
				
			</div>
		</td>
	</tr>
	<tr>
	<td>
	<ul>
			<li><a href="javascript:history.back(-1)" target="_top">返回上一步</a></li>
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