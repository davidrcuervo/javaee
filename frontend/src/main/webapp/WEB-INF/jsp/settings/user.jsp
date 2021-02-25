<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set target="${web}" property="title" value="User" />
<c:set target="${web}" property="active" value="settings" />

<c:set scope="request" var="content">
	<h1>User information to be edited.</h1>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>