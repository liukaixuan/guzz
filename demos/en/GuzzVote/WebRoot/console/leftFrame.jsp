<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:boundary>
	<c:if test="${!consoleUser.systemAdmin}">
		<g:addInLimit name="authGroup" value="${consoleUser.authGroups}" />
	</c:if>

	<g:list var="m_channels" business="channel" orderBy="id desc" pageSize="20" />
</g:boundary>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Console Navigation</title>
		<link href="../css/style.css" rel="stylesheet" type="text/css" />
		<script type="text/javascript">
		
		
		
		</script>
	</head>
	<body class="left">
		<div id="page">
			<table width="178px" border="0">
				<tr>
					<td valign="top" class="left" width="178px">
						<div>
							<h2>
								${consoleUser.nickName}
							</h2>
							<div class="line16"></div>
							<div class="menu">
								<ul>
									<li>
										<a target="mainFrame" href="listBigVoteResults.jsp">View Stats</a>
									</li>
									<li>
										<a target="mainFrame" href="listBigVotes.jsp" >Manage Votes</a>
									</li>
									<li>
										<a target="mainFrame" href="bigVoteAction.do">Create a Vote</a>
									</li>
								</ul>
							</div>
							<div class="line16"></div>
							<c:if test="${consoleUser.systemAdmin}">
								<div class="menu">
									<ul>
										<li>
											<a target="mainFrame" href="listChannels.jsp">Channels</a>
										</li>
									</ul>
								</div>
							</c:if>
							<div class="menu">
								<ul>
									<li>
										<a target="_top" href="logout.jsp">Logout</a>
									</li>
								</ul>
							</div>
							<div class="line16"></div>
							<h3>
								Authed Channels:
							</h3>
							<div class="menu">
								<ul>
									<c:forEach items="${m_channels}" var="m_channel">
										<li>
											<a target="mainFrame"
												href="listBigVotes.jsp?cid=${m_channel.id}">${m_channel.name}</a>
										</li>
									</c:forEach>
								</ul>
							</div>
							
							<div class="line16"></div>
							<div style="padding-left:20px; font-size: 10pt;">
								@Powered By <a href="http://www.guzz.org/" target="_blank">guzz</a>
							</div>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</body>

</html>
