<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set scope="request" var="title" value="Login" />
<c:set scope="request" var="active" value="login" />

<c:set scope="request" var="content">
	<h1>Login</h1>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>