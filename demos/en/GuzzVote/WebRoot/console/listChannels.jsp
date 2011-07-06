<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:page var="m_channels" business="channel" orderBy="id desc"
	pageNo="${param.pageNo}" pageSize="20" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Channels</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="cornerNav">
							Channels &nbsp;&nbsp;&nbsp;
							<a href="channelAction.do">Create A New Channel</a>
						</div>

						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td>
											Channel No:
										</td>
										<td>
											Name
										</td>
										<td>
											Authed Group
										</td>
										<td>
											createdTime
										</td>
										<td>
											Operations
										</td>
									</tr>
								</thead>

								<c:forEach items="${m_channels.elements}" var="m_channel">
									<tr bgcolor="#f6f6f6">
										<td>
											${m_channel.id}
										</td>
										<td>
											${m_channel.name}
										</td>
										<td>
											${m_channel.authGroup}
										</td>
										<td>
											<fmt:formatDate value="${m_channel.createdTime}"
												pattern="yyyy-MM-dd HH:mm:ss" />
										</td>
										<td>
											<span class="blue">[<a
												href="channelAction.do?id=${m_channel.id}">Modify</a>]</span>
											<br />
										</td>
									</tr>
								</c:forEach>
							</table>

							<table border="0" width="96%" align="center" class="data">
								<tr align="left">
									<c:import url="/WEB-INF/jsp/include/console_flip.jsp" />
								</tr>
							</table>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</body>
</html>
