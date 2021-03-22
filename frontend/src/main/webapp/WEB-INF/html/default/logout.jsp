<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<% request.getSession().invalidate(); %>

<c:set scope="request" var="content">
<%--	
	<c:if test="${empty pageContext.request.userPrincipal}">
		<div>You have logged out successfully!!!.</div>
		<div>Hope you come back soon.</div>
	</c:if>

	<c:if test="${not empty pageContext.request.userPrincipal}">
		<% request.getSession().invalidate(); %>
		<c:redirect url="${web.href('/logout.html')}"/>
	</c:if>
 --%>
 	<div>You have logged out successfully!!!.</div>
	<div>Hope you come back soon.</div>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>

