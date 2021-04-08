<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="hidePathLinksBar" scope="request" value="yes" />
<% request.getSession().invalidate(); %>
<c:set scope="request" var="content">
	<div>You have logged out successfully!!!.</div>
	<div>Hope you come back soon.</div>
</c:set>

<c:set var="template" value="${settings.get('template') }" />
<jsp:include page="/WEB-INF/template/${template}/webdb.template.jsp"></jsp:include>