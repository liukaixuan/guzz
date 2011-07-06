<%@ page pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include/tags.jsp"%>

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
							Vote Management&gt;&gt; Maintain a Group/Question
						</div>


						<div class="text">
							<form method="POST">
								<input type="hidden" name="id" value="${param.id}" />
								<input type="hidden" name="voteId" value="${param.voteId}" />

								<spring:bind path="voteItemGroup.group.name">

									<div class="cell">
										<label>
											Group Name:
										</label>
										<input type="text" name="${status.expression}"
											value="${status.value}" maxlength="64" size="42" />
										<%@ include file="/WEB-INF/jsp/include/error_inc.jsp"%>
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
