<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="loginError" scope="request" value="true" />

<jsp:include page="/WEB-INF/jsp/user/login.jsp"></jsp:include>
