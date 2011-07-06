<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<%
	/**列出一个地区对某个投票中各个投票项目的支持情况。*/
%>

<g:get var="m_city" business="voteTerritory" limit="id=${param.cityId}" />
<g:get var="m_vote" business="bigVote" limit="id=${m_city.voteId}" />

<c:if test="${empty param.order}">
	<g:list var="m_voteLogs" business="territoryVoteLog"
		limit="territoryId=${m_city.id}" orderBy="id asc" pageSize="200" />
</c:if>
<c:if test="${not empty param.order}">
	<g:list var="m_voteLogs" business="territoryVoteLog"
		limit="territoryId=${m_city.id}" orderBy="${param.order}"
		pageSize="200" />
</c:if>

<g:count var="m_voteNumBase" business="territoryVoteLog"
	selectPhrase="max(voteNum)" limit="territoryId=${m_city.id}" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>${m_city.name}</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="cornerNav">
							${m_vote.name} &gt;&gt;
							<a href="listVoteCities.jsp?voteId=${m_city.voteId}">${m_city.name}</a>
							&gt;&gt; Votes in this area
							<p>
								Order by:
								<a href="?cityId=${m_city.id}">Natural</a> ||
								<a href="?cityId=${m_city.id}&order=voteNum desc">Votes</a>
							</p>
						</div>
				
						<p />
						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thread>
								<tr>
									<th style="width: 50px;">
										No.
									</th>
									<th>
										Vote Item
									</th>
									<th style="width: 120px;">
										Votes
									</th>
									<th style="width: 260px;">
										Percents
									</th>
									<th style="width: 260px;">
										More
									</th>
								</tr>
								</thread>
				
								<c:forEach items="${m_voteLogs}" var="m_log" varStatus="m_status">
									<g:get var="m_item" business="voteItem" limit="id=${m_log.itemId}" />
				
									<tr>
										<td align="center">
											${m_status.index + 1}
										</td>
										<td>
											&nbsp;${m_item.name}
										</td>
										<td>
											${m_log.voteNum}
										</td>
				
										<c:if test="${m_city.voteNum == 0}">
											<td>
												0%
											</td>
										</c:if>
										<c:if test="${m_city.voteNum != 0}">
											<td valign="middle">
												<fmt:formatNumber var="m_tmp_count" maxFractionDigits="2"
													minFractionDigits="2"
													value="${m_log.voteNum * 100 / m_voteNumBase}" />
												<fmt:formatNumber var="m_tmp_count2" maxFractionDigits="2"
													minFractionDigits="2"
													value="${m_log.voteNum * 100 / m_city.voteNum}" />
												<img src="../css/dot_v_line.gif" width="${m_tmp_count * 2}px;"
													height="15px;">
												&nbsp;${m_tmp_count2}%
											</td>
										</c:if>
				
										<td>
											<a
												href="listVoteItemDetailCities.jsp?itemId=${m_log.itemId}&order=voteNum%20desc">Detailed City Stat</a>
										</td>
				
									</tr>
								</c:forEach>
							</table>
						</div>
				
						<div class="footSpacing" style="height: 50px;">
							&nbsp;
						</div>
						
					</td>
				</tr>
			</table>
		</div>

	</body>
</html>
