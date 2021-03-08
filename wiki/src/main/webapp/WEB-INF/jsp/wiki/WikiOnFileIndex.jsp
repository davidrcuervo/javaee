<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set scope="request" var="content">
	<h3>Wiki Index</h3>
	<c:forEach var="wiki" items="${applicationScope.wikiIndex.findAll()}">
		<c:set var="link">/${wiki}</c:set>
		<div><a href="${web.href(link)}"><c:out value="${wiki}" /></a></div>
	</c:forEach>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/htmlTemplate.jsp"></jsp:include>