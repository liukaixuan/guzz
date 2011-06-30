<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<g:list var="m_voteItemGroups" business="voteItemGroup"
	limit="voteId=${voteItemForm.item.voteId}" pageSize="200" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>投票项管理</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="nav">
							投票管理 &gt;&gt; 维护投票项
						</div>


						<div class="text">
							<form method="POST">
								<input type="hidden" name="id" value="${param.id}" />
								<input type="hidden" name="voteId" value="${param.voteId}" />

								<spring:bind path="voteItemForm.item.name">
									<div class="cell">
										<label>
											投票项名称：
										</label>
										<input type="text" name="${status.expression}"
											value="${status.value}" maxlength="64" size="42" />
										<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
									</div>
								</spring:bind>
								<spring:bind path="voteItemForm.item.showName">
									<div class="cell">
										<label>
											前台显示名称：
										</label>
										<input type="text" name="${status.expression}"
											value="<c:out value='${status.value}' escapeXml='true' />"
											maxlength="255" size="42" />
										<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
										<nobr>
											<font color="red">&nbsp;*&nbsp;</font>可以包含html代码，最大255字符。
										</nobr>
									</div>
								</spring:bind>
								<spring:bind path="voteItemForm.item.groupId">
									<div class="cell">
										<label>
											分组：
										</label>
										<select name="${status.expression}">
											<option value="0">
												未分组
											</option>
											<c:forEach items="${m_voteItemGroups}" var="m_group">
												<option value="${m_group.id}"
													<c:if test="${m_group.id == status.value}">selected</c:if>>
													<c:out value="${m_group.name}" />
												</option>
											</c:forEach>
										</select><%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
										<nobr>
											<span class="blue">[<a
												href="listVoteItemGroups.jsp?voteId=${voteItemForm.item.voteId}">管理分组</a>]</span>
										</nobr>
									</div>
								</spring:bind>

								<spring:bind path="voteItemForm.item.addedVoteNum">
									<div class="cell">
										<label>
											手工干预投票数：
										</label>
										<input type="text" name="${status.expression}"
											value="${status.value}" maxlength="64" size="42" />
										<nobr>
											&nbsp;正数增加得票，负数减少得票。
											<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%></nobr>
									</div>
								</spring:bind>
								<div class="cell">
									<label>
										<nobr>
											（此处修改后将会自动更新到总投票次数中，请注意到地区中修改干预值以保持数据一致【所有选项得票数=总票数=所有地区得票数】。）

										</nobr>
									</label>
								</div>


								<div class="botton">
									<input type="submit" value="提交" class="bot" />
									<input type="reset" value="取消" class="bot" />
								</div>


							</form>
						</div>
					</td>
				</tr>
			</table>
		</div>

	</body>
</html>
