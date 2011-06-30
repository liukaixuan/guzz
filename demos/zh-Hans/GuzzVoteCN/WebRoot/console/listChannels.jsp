<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:page var="m_channels" business="channel" orderBy="id desc"
	pageNo="${param.pageNo}" pageSize="20" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>频道列表</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="cornerNav">
							频道管理 &nbsp;&nbsp;&nbsp;
							<a href="channelAction.do">创建新频道</a>
						</div>

						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td>
											频道号
										</td>
										<td>
											频道名称
										</td>
										<td>
											所属用户组
										</td>
										<td>
											创建时间
										</td>
										<td>
											管理投票
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
												href="channelAction.do?id=${m_channel.id}">修改</a>]</span>
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
