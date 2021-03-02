<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="homeTemplate" class="com.laetienda.lib.bean.HttpTemplate" scope="request"></jsp:useBean>

<c:set target="${homeTemplate}" property="url" value="${settings.get('frontend.template.main.url') }" />
<c:set target="${homeTemplate}" property="title" value="${settings.get('frontend.template.main.url') }" />
<c:set target="${homeTemplate}" property="uriMenu" value="/menu" />
<c:set target="${homeTemplate}" property="uriHeader" value="/header" />
<c:set target="${homeTemplate}" property="active" value="wiki" />

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
		<jsp:setProperty name="homeTemplate" property="script" value="${script}" />
	</c:forEach>
	<c:forEach var="style" items="${web.styles}">
		<jsp:setProperty name="homeTemplate" property="style" value="${style}" />	
	</c:forEach>
	<c:out value="${homeTemplate.header}" escapeXml="false"/>
	<%-- <jsp:getProperty name="homeTemplate" property="header" /> --%>
</head>
<body>
	<header><c:out value="${homeTemplate.menu}" escapeXml="false"/></header>
	<main>
		<div class="container">
			<c:out value="${content}" escapeXml="false" />
		</div>
	</main>
</body>
</html>