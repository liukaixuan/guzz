<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<%
	/**按照地区，列出一个投票项所有地区的投票情况。*/
%>

<g:get var="m_voteItem" business="voteItem" limit="id=${param.itemId}" />
<g:get var="m_vote" business="bigVote" limit="id=${m_voteItem.voteId}" />

<c:if test="${empty param.order}">
	<g:list var="m_voteLogs" business="territoryVoteLog"
		limit="itemId=${m_voteItem.id}" orderBy="id asc" pageSize="200" />
</c:if>
<c:if test="${not empty param.order}">
	<g:list var="m_voteLogs" business="territoryVoteLog"
		limit="itemId=${m_voteItem.id}" orderBy="${param.order}"
		pageSize="200" />
</c:if>

<g:count var="m_voteNumBase" business="territoryVoteLog"
	selectPhrase="max(voteNum)" limit="itemId=${m_voteItem.id}" />


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>${m_voteItem.name}管理</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="cornerNav">
							${m_vote.name} &gt;&gt;
							<a href="listVoteItems.jsp?voteId=${m_vote.id}">${m_voteItem.name}</a>
							&gt;&gt; 详细地区统计

							<p>
								查看方式：
								<a href="?itemId=${m_voteItem.id}">自然顺序</a> ||
								<a href="?itemId=${m_voteItem.id}&order=voteNum desc">按贡献票数顺序</a>

							</p>
						</div>


						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td style="width: 50px;">
											排名
										</td>
										<td>
											地区
										</td>
										<td style="width: 120px;">
											贡献票数
										</td>
										<td style="width: 260px;">
											票数比例
										</td>
										<td style="width: 260px;">
											更多信息
										</td>
									</tr>
								</thead>

								<c:forEach items="${m_voteLogs}" var="m_log"
									varStatus="m_status">
									<g:get var="m_city" business="voteTerritory"
										limit="id=${m_log.territoryId}" />

									<tr bgcolor="#f6f6f6">
										<td align="center">
											${m_status.index + 1}
										</td>
										<td>
											&nbsp;${m_city.name}
										</td>
										<td>
											${m_log.voteNum}
										</td>

										<c:if test="${m_voteItem.voteNum == 0}">
											<td>
												0%
											</td>
										</c:if>
										<c:if test="${m_voteItem.voteNum != 0}">
											<td valign="middle">
												<fmt:formatNumber var="m_tmp_count" maxFractionDigits="2"
													minFractionDigits="2"
													value="${m_log.voteNum * 100 / m_voteNumBase}" />
												<fmt:formatNumber var="m_tmp_count2" maxFractionDigits="2"
													minFractionDigits="2"
													value="${m_log.voteNum * 100 / m_voteItem.voteNum}" />
												<img src="../css/dot_v_line.gif"
													width="${m_tmp_count * 2}px;" height="15px;">
												&nbsp;${m_tmp_count2}%
											</td>
										</c:if>
										<td>
											<span class="blue">[<a
												href="listCityDetailItems.jsp?cityId=${m_log.territoryId}&order=voteNum%20desc">投票选项分布</a>]</span>
										</td>
									</tr>
								</c:forEach>
							</table>
						</div>

						<div class="footSpacing" style="height: 50px;">
							&nbsp;
						</div>
		</td></tr></table></div>
	</body>
</html>
