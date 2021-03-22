<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<jsp:useBean id="web" scope="request" class="com.laetienda.lib.tomcat.WebEngine" >
	<jsp:setProperty name="web" property="httpServletRequest" value="<%=request%>" />
</jsp:useBean>

<jsp:useBean id="loginTemplate" class="com.laetienda.lib.http.HttpTemplate" scope="request">
	<jsp:setProperty name="loginTemplate" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

<c:set var="action" value="${web.href('/j_security_check')}" />
${requestScope.loginTemplate.setPostParameter("action", action)}

<c:set var="hidePathLinksBar" scope="request" value="yes" />
<c:set scope="request" var="content">
	<c:out value="${loginTemplate.getHtmlTemplate('/login')}" escapeXml="false" />
</c:set>

<jsp:include page="/WEB-INF/jsp/template/default.jsp"></jsp:include>