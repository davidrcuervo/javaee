<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:import var="htmlLoginForm" url="/WEB-INF/jsp/template/main/login.jsp" />

<jsp:useBean id="web" scope="request" class="com.laetienda.lib.tomcat.WebEngine" >
	<jsp:setProperty name="web" property="httpServletRequest" value="<%=request%>" />
</jsp:useBean>


<c:set target="${web}" property="title" value="login" />
<c:set target="${web}" property="active" value="wiki" />

<c:set scope="request" var="content">
	<div class="row justify-content-md-center">
	<div class="col-sm-4">
		<h2>Please login:</h2>
		<c:out value="${htmlLoginForm}" escapeXml="false"/>
	</div>
	</div>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>