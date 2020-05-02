<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set scope="request" var="title" value="Home" />
<c:set scope="request" var="active" value="home" />

<c:set scope="request" var="content">
	<h1>Hello Word!</h1>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>