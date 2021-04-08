<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="template" class="com.laetienda.lib.http.HttpTemplate" scope="request">
	<jsp:setProperty name="template" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

<c:if test="${not empty template}">
	${template.setPostParameter("title", "WebDb")}
	${template.setPostParameter("context", pageContext.request.contextPath)}
</c:if>

<c:if test="${not empty pageContext.request.userPrincipal}">
	${template.setPostParameter("uid", pageContext.request.userPrincipal.name) }
</c:if>

<!DOCTYPE html>
<html>
<head>
	<c:out value="${template.getHtmlTemplate('/header')}" escapeXml="false"/>
</head>
<body>
	<header><c:out value="${template.getHtmlTemplate('/menu')}" escapeXml="false"/></header>
	<main><c:out value="${content}" escapeXml="false" /></main>
</body>
</html>