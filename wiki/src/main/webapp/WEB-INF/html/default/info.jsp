<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<%-- <c:set target="${web}" property="title" value="indexwiki" /> --%>

<c:set target="${web}" property="style">
	<link rel="stylesheet" href="${web.href('/assets/css/indexwiki.css')}" />
</c:set>

<c:set target="${web}" property="script">
	<script type="text/javascript" src="${web.href('/assets/js/wiki.js')}" />
</c:set>

<c:set scope="request" var="content">
	<h2>Hello World!!</h2>
	
	<c:if test="${not empty pageContext.request.userPrincipal}">
		<div>User is Logged In</div>
		
		<div>testid: <c:out value="${sessionScope.idtest}" /></div>
	</c:if>
	
	<c:if test="${empty pageContext.request.userPrincipal}">
		<div>User is not Logged In</div>
	</c:if>
	<div>Session id: <c:out value="${pageContext.session.id}" /></div>
	<div>Domain: <c:out value="${pageContext.request.serverName}" /></div>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/default.jsp"></jsp:include>