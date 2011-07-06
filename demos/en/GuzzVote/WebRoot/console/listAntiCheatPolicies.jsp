<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:get var="m_vote" business="bigVote" limit="id=${param.voteId}" />
<g:list var="m_policies" business="antiCheatPolicy"
	limit="voteId=${m_vote.id}" orderBy="id asc" pageSize="200" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Anti Cheat</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="cornerNav">
							${m_vote.name} &gt;&gt;
						</div>
						<div class="big_nav">
							<span><b><a
									href="listVoteItems.jsp?voteId=${m_vote.id}">Manage Vote Items</a>
							</b>
							</span>
							<span>|</span><span><b><a
									href="listVoteCities.jsp?voteId=${m_vote.id}">Manage Cities</a>
							</b>
							</span>
							<span>|</span><span><b><a
									href="listVoteExtraProperties.jsp?voteId=${m_vote.id}">User-defined Form</a>
							</b>
							</span>
							<span>|</span><span><b><a
									href="listVoteItemGroups.jsp?voteId=${m_vote.id}">Manage Vote Questions</a>
							</b>
							</span>
							<span>|</span><span  class="cur"><b><a
									href="listAntiCheatPolicies.jsp?voteId=${m_vote.id}">Anti Cheat</a>
							</b>
							</span>
						</div>
						<div class="small_nav">

							<span><b><a
									href="antiCheatPolicyAction.do?voteId=${m_vote.id}&p=cookie">add cookie filter</a>
							</b>
							</span>
							<span>|</span>
							<span><b><a
									href="antiCheatPolicyAction.do?voteId=${m_vote.id}&p=IP">add IP filter</a>
							</b>
							</span>
							<span>|</span>
							<span><b><a
									href="antiCheatPolicyAction.do?voteId=${m_vote.id}&p=extraProp">add user-defined form filter</a>
							</b>
							</span>
						</div>


						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td style="width: 50px;">
											No.
										</td>
										<td>
											Policy Name
										</td>
										<td>
											strategy
										</td>
										<td>
											limited field
										</td>
										<td>
											expires&nbsp;
										</td>
										<td>
											max votes allowed
										</td>
										<td style="width: 120px;">
											Operations
										</td>
									</tr>
								</thead>
								<c:forEach items="${m_policies}" var="m_policy"
									varStatus="m_status">
									<tr bgcolor="#f6f6f6">
										<td>
											${m_status.index + 1}
										</td>
										<td>
											&nbsp;
											<c:out value="${m_policy.name}" escapeXml="true" />
										</td>

										<td>
											&nbsp;${m_policy.policyImpl}
										</td>
										<td>
											&nbsp;${m_policy.limitedField}
										</td>
										<td>
											&nbsp;${m_policy.maxLife} seconds
										</td>
										<td>
											&nbsp;${m_policy.allowedCount}
										</td>

										<td>
											System No:${m_prop.id}
											<br />
											<span class="blue">[<a
												href="antiCheatPolicyAction.do?id=${m_policy.id}">Modify</a>]</span>
											<br />
											<span class="blue">[<a
												href="extendAntiCheatPolicyAction.do?action=delete&id=${m_policy.id}">Delete</a>]</span>
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
