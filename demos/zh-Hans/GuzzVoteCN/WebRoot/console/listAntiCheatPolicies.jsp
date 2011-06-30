<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:get var="m_vote" business="bigVote" limit="id=${param.voteId}" />
<g:list var="m_policies" business="antiCheatPolicy"
	limit="voteId=${m_vote.id}" orderBy="id asc" pageSize="200" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>投票反作弊管理</title>
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
									href="listVoteItems.jsp?voteId=${m_vote.id}">管理投票选项</a>
							</b>
							</span>
							<span>|</span><span><b><a
									href="listVoteCities.jsp?voteId=${m_vote.id}">管理投票地区</a>
							</b>
							</span>
							<span>|</span><span><b><a
									href="listVoteExtraProperties.jsp?voteId=${m_vote.id}">管理自定义属性</a>
							</b>
							</span>
							<span>|</span><span><b><a
									href="listVoteItemGroups.jsp?voteId=${m_vote.id}">管理选项分组</a>
							</b>
							</span>
							<span>|</span><span  class="cur"><b><a
									href="listAntiCheatPolicies.jsp?voteId=${m_vote.id}">反作弊</a>
							</b>
							</span>
						</div>
						<div class="small_nav">

							<span><b><a
									href="antiCheatPolicyAction.do?voteId=${m_vote.id}&p=cookie">添加cookie限制</a>
							</b>
							</span>
							<span>|</span>
							<span><b><a
									href="antiCheatPolicyAction.do?voteId=${m_vote.id}&p=IP">添加IP限制</a>
							</b>
							</span>
							<span>|</span>
							<span><b><a
									href="antiCheatPolicyAction.do?voteId=${m_vote.id}&p=extraProp">添加自定义属性限制</a>
							</b>
							</span>
						</div>


						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td style="width: 50px;">
											序号
										</td>
										<td>
											名称
										</td>
										<td>
											策略
										</td>
										<td>
											所限制属性
										</td>
										<td>
											有效时间&nbsp;
										</td>
										<td>
											最多允许票数
										</td>
										<td style="width: 120px;">
											管理
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
											&nbsp;${m_policy.maxLife}秒
										</td>
										<td>
											&nbsp;${m_policy.allowedCount}
										</td>

										<td>
											系统编号：${m_prop.id}
											<br />
											<span class="blue">[<a
												href="antiCheatPolicyAction.do?id=${m_policy.id}">修改</a>]</span>
											<br />
											<span class="blue">[<a
												href="extendAntiCheatPolicyAction.do?action=delete&id=${m_policy.id}">删除</a>]</span>
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
