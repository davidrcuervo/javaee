<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<c:set var="hidePathLinksBar" scope="request" value="yes" />
<c:set scope="request" var="content">
	
	<c:if test="${empty pageContext.request.userPrincipal}">
		<div>You have logged out successfully!!!.</div>
		<div>Hope you come back soon.</div>
	</c:if>
	
	<c:if test="${not empty pageContext.request.userPrincipal}">
		<div>You are logging out....</div>
		<% request.getSession().invalidate(); %>
		<c:redirect url="${web.href('/logout.html')}"/>
	</c:if>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/default.jsp"></jsp:include>