<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<g:page var="m_votes" business="bigVote" limit="${consoleUser}"	orderBy="id desc" pageNo="${param.pageNo}" pageSize="20" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Vote List</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>
	<style type="text/css">
	.t1 {background-color:#336699}
	.t2 {background-color:#ffcc00}
	</style>
	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="cornerNav">
							View Stats&nbsp;&nbsp;&nbsp;
						</div>

						<div class="text_sta">
							<table id="table1" width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td>
											Vote No
										</td>
										<td>
											Vote Name
										</td>
										<td>
											status
										</td>
										<td>
											Vote People
										</td>
										<td>
											User Votes
										</td>
										<td>
											Appended Votes
										</td>
										<td>
											Stats
										</td>
									</tr>
								</thead>


								<c:forEach items="${m_votes.elements}" var="m_vote">

									<tr bgcolor="#f6f6f6">
										<td>
											${m_vote.id}
											<br>
										</td>
										<td>
											<span class="blue"><a href="../viewVoteAction.do?voteId=${m_vote.id}"
												target="_blank">${m_vote.name}</a></span>
										</td>
										<td>
											<c:if test="${!m_vote.openToPublicNow}">
												<font class="forbidden">Closed</font>
											</c:if>
											<c:if test="${m_vote.openToPublicNow}">
												<font class="permit">Open</font>
											</c:if>

											<c:if
												test="${m_vote.beginTime != null || m_vote.endTime != null}">
												<img style="vertical-align: bottom; cursor: pointer;"
													title="<g:out value='${m_vote.openTimeDesc}' escapeXml='false' />"
													src="../css/clock.gif" height="28px" />
											</c:if>
										</td>
										<td>
											${m_vote.votePeople}
										</td>
										<td>
											${m_vote.voteNum}
										</td>
										<td>
											${m_vote.addedVoteNum}
										</td>
										<td>
											<span class="blue">[<a
												href="listVoteItems.jsp?voteId=${m_vote.id}">Stat By Vote Item</a>]</span>
											<br />
											<span class="blue">[<a
												href="listVoteCities.jsp?voteId=${m_vote.id}">Stat By Vote Area</a>]</span>
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
