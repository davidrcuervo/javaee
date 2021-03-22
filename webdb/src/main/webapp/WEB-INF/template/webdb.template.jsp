<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="template" class="com.laetienda.lib.http.HttpTemplate" scope="request">
	<jsp:setProperty name="template" property="url" value="${settings.get('frontend.url') }" />
</jsp:useBean>

${template.setPostParameter("title", "WebDb")}

<!DOCTYPE html>
<html>
<head>
	
	<c:out value="${template.getQuickTemplate('/template/main/header')}" escapeXml="false"/>
</head>
<body>
	<header><c:out value="${template.getQuickTemplate('/template/main/menu')}" escapeXml="false"/></header>
	<c:out value="${content}" escapeXml="false" />
</body>
</html>