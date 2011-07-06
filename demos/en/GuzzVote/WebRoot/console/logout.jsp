<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp" %>

<%
session.invalidate() ;		
response.sendRedirect("./") ;
%>
