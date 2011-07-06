<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>Channel</title>
		<link rel="stylesheet" type="text/css" href="../css/style.css" />
	</head>

	<body>
		<div id="page">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top" class="right">
						<div class="nav">
							Channel&gt;&gt;Channel
						</div>


						<div class="text">
							<form method="POST">
								<input type="hidden" name="id" value="${param.id}" />
								<h1>
									Channels are not allowed to be dropped!
								</h1>
								<p />
									<spring:bind path="channelForm.channel.name">
										<div class="cell">
											<label>
												Channel Name:
											</label>
											<input type="text" name="${status.expression}"
												value="${status.value}" maxlength="255" size="42"
												class="name" />
											<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
										</div>
									</spring:bind>

									<spring:bind path="channelForm.channel.authGroup">
										<div class="cell">
											<label>
												Granted Auth Group:
											</label>
											<select name="${status.expression}">
												<c:forEach items="${authGroups}" var="m_group">
													<option value="${m_group.authName}"
														<c:if test="${m_group.authName eq status.value}">selected</c:if>>
														<c:out value="${m_group.showName}" />
													</option>
												</c:forEach>
											</select><%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
										</div>
									</spring:bind>
								<div class="botton">
									<input type="submit" value="submit" class="bot" />
									<input type="reset" value="reset" class="bot" />
								</div>

							</form>
						</div>
					</td>
				</tr>
			</table>
		</div>
	</body>
</html>
