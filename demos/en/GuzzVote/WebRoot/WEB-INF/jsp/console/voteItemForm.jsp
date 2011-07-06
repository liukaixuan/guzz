<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>
<g:list var="m_voteItemGroups" business="voteItemGroup"
	limit="voteId=${voteItemForm.item.voteId}" pageSize="200" />

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Vote Management</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="nav">
							Vote Management &gt;&gt; Maintain vote item
						</div>


						<div class="text">
							<form method="POST">
								<input type="hidden" name="id" value="${param.id}" />
								<input type="hidden" name="voteId" value="${param.voteId}" />

								<spring:bind path="voteItemForm.item.name">
									<div class="cell">
										<label>
											Item Name:
										</label>
										<input type="text" name="${status.expression}"
											value="${status.value}" maxlength="64" size="42" />
										<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
									</div>
								</spring:bind>
								<spring:bind path="voteItemForm.item.showName">
									<div class="cell">
										<label>
											Display Name:
										</label>
										<input type="text" name="${status.expression}"
											value="<c:out value='${status.value}' escapeXml='true' />"
											maxlength="255" size="42" />
										<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
										<nobr>
											<font color="red">&nbsp;*&nbsp;</font>html code is allowed.
										</nobr>
									</div>
								</spring:bind>
								<spring:bind path="voteItemForm.item.groupId">
									<div class="cell">
										<label>
											group:
										</label>
										<select name="${status.expression}">
											<option value="0">
												no-group
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
												href="listVoteItemGroups.jsp?voteId=${voteItemForm.item.voteId}">Manage groups</a>]</span>
										</nobr>
									</div>
								</spring:bind>

								<spring:bind path="voteItemForm.item.addedVoteNum">
									<div class="cell">
										<label>
											Appended Votes:
										</label>
										<input type="text" name="${status.expression}"
											value="${status.value}" maxlength="64" size="42" />
										<nobr>
											&nbsp;
											<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%></nobr>
									</div>
								</spring:bind>


								<div class="botton">
									<input type="submit" value="submit" class="bot" />
									<input type="reset" value="cancel" class="bot" />
								</div>


							</form>
						</div>
					</td>
				</tr>
			</table>
		</div>

	</body>
</html>
