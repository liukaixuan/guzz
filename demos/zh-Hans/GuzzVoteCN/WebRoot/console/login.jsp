<%@ page pageEncoding="UTF-8"%>
<%@page import="org.guzz.service.user.AdminUserService"%>
<%@page import="org.guzz.web.context.GuzzWebApplicationContextUtil"%>
<%@page import="org.guzz.service.user.AdminUser"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<%
	String msg = "";

	if ("POST".equalsIgnoreCase(request.getMethod())) {
		AdminUserService aus = (AdminUserService) GuzzWebApplicationContextUtil
				.getGuzzContext(session.getServletContext())
				.getService("adminUserService");

		String userName = request.getParameter("userName");
		String password = request.getParameter("password");

		if (aus.checkAdminUser(userName, password)) {
			AdminUser u = aus.getUser(userName);
			session.setAttribute("consoleUser", u);

			response.sendRedirect("./");
			return;
		} else {
			msg = "用户名与密码不匹配";
		}

	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>后台登录</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>	
						<div class="login">
							<form method="post" target="_top">
								<h1>
									登录后台
									<c:if test="${not empty msg}">
									<font color="red">（<%=msg%>）</font>
									</c:if>
								</h1>
								<br>
								<br>
								<div class="cell">
									<label>
										用户名：
									</label>
									<input type="text" name="userName" class="name" />
								</div>

								<div class="cell">
									<label>
										密&nbsp;&nbsp;码：
									</label>
									<input type="password" name="password" class="name" />
								</div>

								<div class="botton">
									<input type="submit" value="提交" class="bot" />
									<span class="blue">[<a href="http://services.guzz.org/console/listUserAdmins.jsp" target="_blank">开通新帐号</a>]</span>
									<span class="blue">[<a href="http://services.guzz.org/console/listUserAuthGroups.jsp" target="_blank">管理权限</a>]</span>
								</div>
							</form>
						</div>
					</td>
				</tr>
			</table>
	</body>

</html>