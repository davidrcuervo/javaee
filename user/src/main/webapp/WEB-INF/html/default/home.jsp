<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="content" scope="request">
	<div>Hello world</div>
</c:set>

<jsp:include page="/WEB-INF/template/${settings.get('template') }/user.template.jsp"></jsp:include>