<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="web" scope="request" class="com.laetienda.lib.tomcat.WebEngine" >
	<jsp:setProperty name="web" property="httpServletRequest" value="<%=request%>" />
</jsp:useBean>

<jsp:useBean id="loginTemplate" class="com.laetienda.lib.http.HttpTemplate" scope="request" type="com.laetienda.lib.http.TemplateRepository">
	<jsp:setProperty name="loginTemplate" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

<c:if test="${not empty loginTemplate}">
	<c:set var="action" value="${web.href('/j_security_check')}" />
	${loginTemplate.setPostParameter("action", action)}
</c:if>

<c:set var="hidePathLinksBar" scope="request" value="yes" />
<c:set var="content" scope="request">
	<div><c:out value="${loginTemplate.getHtmlTemplate('/login') }" escapeXml="false" /></div>
</c:set>

<jsp:include page="/WEB-INF/template/${settings.get('template') }/user.template.jsp"></jsp:include>