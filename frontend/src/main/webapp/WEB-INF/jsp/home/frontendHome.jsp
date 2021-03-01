<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
 
<c:set target="${web}" property="title" value="Home" />
<c:set target="${web}" property="active" value="home" />
<c:set scope="request" var="content">
	<h1><i class="fas fa-home align-middle"></i><span class="align-middle">Hello Word!</span></h1>
	<div><c:out value="${request.getSession(false) == null ? 'null' : 'not null'}" /></div>
	<c:if test="${not empty pageContext.request.userPrincipal}">
		<div>User is Logged In</div>
	</c:if>
	<c:if test="${empty pageContext.request.userPrincipal}">
		<div>User is not Logged In</div>
	</c:if>
	<div><c:out value="${pageContext.request.userPrincipal.name}" /></div>
		
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>
