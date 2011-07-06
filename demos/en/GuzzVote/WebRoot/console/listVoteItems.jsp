<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>


<g:get var="m_vote" business="bigVote" limit="id=${param.voteId}" />

<g:list var="m_voteItemGroups" business="voteItemGroup"
	limit="voteId=${m_vote.id}" pageSize="200" />

<c:if test="${empty param.order}">
	<g:list var="m_voteItems" business="voteItem"
		limit="voteId=${m_vote.id}" orderBy="id asc" pageSize="200" />
</c:if>
<c:if test="${not empty param.order}">
	<g:list var="m_voteItems" business="voteItem"
		limit="voteId=${m_vote.id}" orderBy="${param.order}" pageSize="200" />
</c:if>

<g:count var="m_voteNumBase" business="voteItem"
	selectPhrase="max(voteNum)" limit="voteId=${m_vote.id}" />
<c:set var="m_voteNumBase"
	value="${m_voteNumBase + m_vote.addedVoteNum }" />


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<link href="../css/style.css" rel="stylesheet" type="text/css" />

		<title>Vote Item</title>
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="cornerNav">
							${m_vote.name} &gt;&gt;&nbsp;
						</div>
						<div class="big_nav">
							<span class="cur"><b><a
									href="listVoteItems.jsp?voteId=${m_vote.id}">Manage Vote Items</a>
							</b>
							</span><span>|</span><span><b><a
									href="listVoteCities.jsp?voteId=${m_vote.id}">Manage Cities</a>
							</b>
							</span><span>|</span><span><b><a
									href="listVoteExtraProperties.jsp?voteId=${m_vote.id}">User-defined Form</a>
							</b>
							</span><span>|</span><span><b><a
									href="listVoteItemGroups.jsp?voteId=${m_vote.id}">Manage Vote Questions</a>
							</b>
							</span><span>|</span><span><b><a
									href="listAntiCheatPolicies.jsp?voteId=${m_vote.id}">Anti Cheat</a>
							</b>
							</span>
						</div>
						<div class="small_nav">
							<span><b><a
									href="voteItemAction.do?voteId=${m_vote.id}">Add Vote Item</a>
							</b>
							</span><span>|</span><span><b><a
									href="?voteId=${m_vote.id}&order=id asc">Natural Order</a>
							</b>
							</span><span>|</span><span><b><a
									href="?voteId=${m_vote.id}&order=voteNum desc">Votes Order</a>
							</b>
							</span><span>|</span><span><b><a
									href="?voteId=${m_vote.id}&order=groupId asc">Group Order</a>
							</b>
							</span>
						</div>

						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td>
											No.
										</td>
										<td>
											Item name
										</td>
										<c:if test="${not empty m_voteItemGroups}">
											<td>
												group
											</td>
										</c:if>
										<td style="widtd: 120px;">
											user votes
										</td>
										<td style="widtd: 120px;">
											appended votes
										</td>
										<td style="widtd: 260px;">
											rates
										</td>
										<td style="widtd: 100px;">
											Operations
										</td>
									</tr>
								</thead>

								<c:forEach items="${m_voteItems}" var="m_item"
									varStatus="m_status">
									<tr bgcolor="#f6f6f6">
										<td align="center" width="30px">
											${m_status.index + 1}
										</td>
										<td>
											&nbsp;
											<c:out value="${m_item.name}" escapeXml="true" />
										</td>
										<c:if test="${not empty m_voteItemGroups}">
											<td>
												<c:if test="${m_item.groupId > 0}">
													<c:forEach items="${m_voteItemGroups}" var="m_group">
														<c:if test="${m_group.id == m_item.groupId}">
															<c:out value="${m_group.name}" />
														</c:if>
													</c:forEach>
												</c:if>
												<c:if test="${m_item.groupId <= 0}">
													<font color="red">No Group</font>
												</c:if>
											</td>
										</c:if>
										<td>
											${m_item.voteNum}
										</td>
										<td>
											${m_item.addedVoteNum}
										</td>
										<c:if test="${m_voteNumBase == 0}">
											<td>
												0%
											</td>
										</c:if>
										<c:if test="${m_voteNumBase != 0}">
											<fmt:formatNumber var="m_tmp_count" maxIntegerDigits="3"
												maxFractionDigits="2" minFractionDigits="2"
												value="${m_item.showVoteNum * 100 / m_voteNumBase}" />
											<fmt:formatNumber var="m_tmp2_count" maxIntegerDigits="3"
												maxFractionDigits="2" minFractionDigits="2"
												value="${m_item.showVoteNum * 100 / m_vote.showVoteNum}" />
											<td valign="middle" title="Total Votes:${m_item.showVoteNum}">
												<img src="../css/dot_v_line.gif"
													width="${m_tmp_count * 2}px;" height="15px;">
												&nbsp;${m_tmp2_count}%
											</td>
										</c:if>
										<td style="width: 120px;" >
											System No: ${m_item.id}
											<br />
											<span class="blue">[<a
												href="listVoteItemDetailCities.jsp?itemId=${m_item.id}">Detailed Stat</a>]</span>
											<br />
											<span class="blue">[<a
												href="voteItemAction.do?id=${m_item.id}">Modify</a>]</span>

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
