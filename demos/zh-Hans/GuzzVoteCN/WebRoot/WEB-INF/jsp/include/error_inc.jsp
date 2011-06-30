<%@ page pageEncoding="UTF-8"%>
<c:if test="${status.error}">
	<font color="#FF0000">
		错误:[
		<c:forEach	items="${status.errorMessages}"	var="error">
			<c:out value="${error}"/>
		</c:forEach>
		]
	</font>
</c:if>