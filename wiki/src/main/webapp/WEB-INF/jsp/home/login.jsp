<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<jsp:useBean id="web" scope="request" class="com.laetienda.lib.tomcat.WebEngine" >
	<jsp:setProperty name="web" property="httpServletRequest" value="<%=request%>" />
</jsp:useBean>

<jsp:useBean id="loginTemplate" class="com.laetienda.lib.bean.HttpTemplate" scope="request">
	<jsp:setProperty name="loginTemplate" property="url" value="${settings.get('frontend.url') }" />
</jsp:useBean>

<c:set scope="request" var="content">
	<c:out value="${loginTemplate.getQuickTemplate('/template/main/login')}" escapeXml="false" />
</c:set>

<jsp:include page="/WEB-INF/jsp/template/htmlTemplate.jsp"></jsp:include>