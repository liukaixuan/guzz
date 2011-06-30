<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/include/tags.jsp"%>

<!-- we need propery PAGE_FLIP -->
		<td class="page_bar">
			第<c:out value="${PAGE_FLIP.pageNo}"/>页&nbsp;共<c:out value="${PAGE_FLIP.pageCount}"/>页&nbsp;(记录总数：<c:out value="${PAGE_FLIP.totalCount}"/>)&nbsp;	
			<c:forEach begin="${PAGE_FLIP.pageStart}" end="${PAGE_FLIP.pageEnd}" step="1" var="index">
				<c:if test="${index == PAGE_FLIP.pageNo}"> 
					<span style="color:red;font-weight:bold;"><c:out value="${index }" /></span> 
				</c:if>
				<c:if test="${index != PAGE_FLIP.pageNo}"> 
					<a href='<c:out value="${PAGE_FLIP.flipURL}" />&<c:out value="${PAGE_FLIP.webPageNoParam}" />=<c:out value="${index}"/>'><span style="text-decoration: none;color:gray;"><c:out value="${index}" /></span></a>  
				</c:if>&nbsp;
			</c:forEach>
		</td>
		