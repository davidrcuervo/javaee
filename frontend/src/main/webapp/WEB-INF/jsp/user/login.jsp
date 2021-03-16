<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="web" scope="request" class="com.laetienda.lib.tomcat.WebEngine" >
	<jsp:setProperty name="web" property="httpServletRequest" value="<%=request%>" />
</jsp:useBean>

<c:set var="action" value="${web.href('/j_security_check') }" scope="request"/>
<c:import var="htmlLoginForm" url="/WEB-INF/jsp/template/main/login.jsp" />

<c:set target="${web}" property="title" value="login" />
<c:set target="${web}" property="active" value="wiki" />

<c:set scope="request" var="content">
	<div><c:out value="${htmlLoginForm}" escapeXml="false"/></div>
	<div>JSessionIDd: <c:out value="${pageContext.request.session.id}" /></div>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>