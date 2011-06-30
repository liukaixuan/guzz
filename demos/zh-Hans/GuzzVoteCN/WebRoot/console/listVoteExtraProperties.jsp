<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<g:get var="m_vote" business="bigVote" limit="id=${param.voteId}" />
<g:list var="m_props" business="voteExtraProperty"
	limit="voteId=${m_vote.id}" orderBy="id asc" pageSize="200" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>投票自定义属性管理</title>
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
							<span>|</span><span class="cur"><b><a
									href="listVoteExtraProperties.jsp?voteId=${m_vote.id}">管理自定义属性</a>
							</b>
							</span>
							<span>|</span><span><b><a
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
									href="voteExtraPropertyAction.do?voteId=${m_vote.id}">创建新属性</a>
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
											中文名称
										</td>
										<td>
											参数值
										</td>
										<td>
											是否必填
										</td>
										<td>
											可选值
										</td>
										<td>
											默认值
										</td>
										<td>
											有效性规则
										</td>
										<td style="width: 120px;">
											管理
										</td>
									</tr>
								</thead>
								<c:forEach items="${m_props}" var="m_prop" varStatus="m_status">
									<tr bgcolor="#f6f6f6">
										<td>
											${m_status.index + 1}
										</td>
										<td>
											&nbsp;
											<c:out value="${m_prop.showName}" escapeXml="true" />
										</td>

										<td>
											${m_prop.paramName}
										</td>

										<td>
											<c:if test="${m_prop.mustProp}">
												<font color='red'>必填属性</font>
											</c:if>
											<c:if test="${!m_prop.mustProp}">
												<font color='green'>选填属性</font>
											</c:if>
										</td>

										<td>
											${m_prop.validValues}
										</td>
										<td>
											${m_prop.defaultValue}
										</td>
										<td>
											${m_prop.validRuleName}
										</td>

										<td>
											系统编号：${m_prop.id}
											<br />
											<span class="blue">[<a
												href="voteExtraPropertyAction.do?id=${m_prop.id}">修改</a>]</span>
											<br />
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
