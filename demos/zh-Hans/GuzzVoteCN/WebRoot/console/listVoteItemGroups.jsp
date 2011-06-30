<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:get var="m_vote" business="bigVote" limit="id=${param.voteId}" />
<g:list var="m_voteItemGroups" business="voteItemGroup"
	limit="voteId=${m_vote.id}" orderBy="id asc" pageSize="200" />


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
		<title>投票分组管理</title>
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
							<span>|</span><span  class="cur"><b><a
									href="listVoteItemGroups.jsp?voteId=${m_vote.id}">管理选项分组</a>
							</b>
							</span>
							<span>|</span><span><b><a
									href="listAntiCheatPolicies.jsp?voteId=${m_vote.id}">反作弊</a>
							</b>
							</span>

						</div>
						<div class="small_nav">

							<span><b><a
									href="voteItemGroupAction.do?voteId=${m_vote.id}"
									title="创建后无法删除，请谨慎操作！">添加分组</a>
							</b>
							</span>
						</div>


						<div class="text_sta">
							<table width="99%" class="statistics" border="0" cellspacing="0"
								cellpadding="0">
								<thead>
									<tr>
										<td>
											顺序
										</td>
										<td>
											分组名称
										</td>
										<td>
											创建时间
										</td>
										<td>
											管理
										</td>
									</tr>
								</thead>

								<c:forEach items="${m_voteItemGroups}" var="m_group"
									varStatus="m_status">
									<tr bgcolor="#f6f6f6">
										<td align="center" width="30px">
											${m_status.index + 1}
										</td>
										<td>
											<c:out value="${m_group.name}" escapeXml="true" />
										</td>
										<td>
											<fmt:formatDate value="${m_group.createdTime}"
												pattern="yyyy-MM-dd HH:mm:ss" />
										</td>
										<td align="center">
											系统编号：${m_group.id}
											<br />
											<a href="voteItemGroupAction.do?id=${m_group.id}">修改</a>
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
