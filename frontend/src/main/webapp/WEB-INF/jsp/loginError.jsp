<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="template" value="${settings.get('frontend.template') }" />
<jsp:include page="/WEB-INF/jsp/${template}/loginError.jsp"></jsp:include>