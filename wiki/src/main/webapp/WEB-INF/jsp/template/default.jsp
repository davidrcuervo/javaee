<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>


<jsp:useBean id="homeTemplate" class="com.laetienda.lib.http.HttpTemplate" scope="request">
	<jsp:setProperty name="homeTemplate" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

<%-- <c:set target="${homeTemplate}" property="title" value="${settings.get('frontend.template.main.url') }" /> --%> 
<c:set target="${homeTemplate}" property="active" value="wiki" />
${homeTemplate.setPostParameter("context", pageContext.request.contextPath)} 

<c:if test="${hidePathLinksBar ne 'yes'}">
	<jsp:useBean id="pathlinks" scope="request" class="com.laetienda.lib.http.HttpTemplate" >
		<jsp:setProperty name="pathlinks" property="url" value="${settings.get('frontend.template.url') }" />
	</jsp:useBean>
 	${requestScope.pathlinks.setPostParameter("context", pageContext.request.contextPath)} 
 	${requestScope.pathlinks.setPostParameter("servlet", pageContext.request.servletPath)}
 	${requestScope.pathlinks.setPostParameter("path", pageContext.request.pathInfo)}
</c:if>
	 
	<%-- ${requestScope.pathlinks.setPostParameter("context", "pageContext.request.contextPath")} --%>


<c:if test="${not empty pageContext.request.userPrincipal}">
	${requestScope.homeTemplate.setPostParameter("uid", pageContext.request.userPrincipal.name)}
</c:if>

<!DOCTYPE html>
<html>
<head>
	<c:set target="${web}" property="style"> 
		<link rel="stylesheet" href="${web.href('/assets/css/wiki.css')}" />
	</c:set>
	<c:set target="${web}" property="script">
		<script type="text/javascript" src="${web.href('/assets/js/wiki.js')}" />
	</c:set>

	<c:forEach var="script" items="${web.scripts}">
		<c:set target="${homeTemplate}" property="script" value="${script}" />
	</c:forEach>
	<c:forEach var="style" items="${web.styles}">
		<c:set target="${homeTemplate}" property="style" value="${style}" />	
	</c:forEach>
	<c:out value="${homeTemplate.getHtmlTemplate('/header')}" escapeXml="false"/>
	<jsp:getProperty name="homeTemplate" property="header" />
</head>
<body>
	<header><c:out value="${homeTemplate.getHtmlTemplate('/menu')}" escapeXml="false"/></header>
	
	<main>
		<div class="container">
			<div><c:out value="${pathlinks.getHtmlTemplate('/pathlinks')}" escapeXml="false" /></div>
			<div><c:out value="${content}" escapeXml="false" /></div>
		</div>
	</main>
</body>
</html>