<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<g:page var="m_votes" business="bigVote" limit="${consoleUser}"	orderBy="id desc" pageNo="${param.pageNo}" pageSize="20" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>投票结果列表</title>
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
							查看投票结果统计&nbsp;&nbsp;&nbsp;
						</div>

						<div class="text_sta">
							<table id="table1" width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td>
											投票号
										</td>
										<td>
											投票名称
										</td>
										<td>
											状态
										</td>
										<td>
											唯一投票人数
										</td>
										<td>
											得票数
										</td>
										<td>
											追加得票数
										</td>
										<td>
											统计结果
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
												<font class="forbidden">关闭</font>
											</c:if>
											<c:if test="${m_vote.openToPublicNow}">
												<font class="permit">对外开放</font>
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
												href="listVoteItems.jsp?voteId=${m_vote.id}">按投票项统计</a>]</span>
											<br />
											<span class="blue">[<a
												href="listVoteCities.jsp?voteId=${m_vote.id}">按地区统计</a>]</span>
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
