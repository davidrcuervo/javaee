<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set target="${web}" property="title" value="Home" />
<c:set target="${web}" property="active" value="home" />
<c:set scope="request" var="content">
	<h1><i class="fas fa-home align-middle"></i><span class="align-middle">Hello Word!</span></h1>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>