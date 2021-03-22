<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="template" value="${settings.get('frontend.template') }" />
<c:import var="htmlHeader" url="/WEB-INF/jsp/template/${template}/header.jsp"></c:import>
<c:import var="htmlMenu" url="/WEB-INF/jsp/template/${template}/menu.jsp"></c:import>

<!DOCTYPE html>
<html>
<head>
	<c:out value="${htmlHeader}" escapeXml="false" />
	<c:out value="${htmlMune}" escapeXml="false" />
</head>
<body>
	<header>
		<c:out value="${htmlMenu}" escapeXml="false" />
	</header>
	
	<main>
		<div class="container">
			<c:out value="${content}" escapeXml="false" />
		</div>
	</main>
</body>
</html>