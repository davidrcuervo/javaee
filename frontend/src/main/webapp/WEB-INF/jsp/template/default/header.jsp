<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>   

<c:set var="icon">${web.href('/assets/icons/other/shopping-cart.png')}</c:set>
<c:set target="${web}" property="icon">${not empty web.icon ? web.icon : icon}</c:set>
<c:set target="${web}" property="icon">${not empty param.icon ? param.icon : web.icon}</c:set>

<c:set target="${web}" property="style"><link rel="stylesheet" href="${web.href('/assets/css/frontend.css')}"></c:set>

<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
<title>${web.title}</title>
<link rel="icon" href="${web.icon}">
	
<%--ADDING STYLES --%>
<link rel="stylesheet" href="${web.href('/assets/css/bootstrap/4.6.0/bootstrap-grid.min.css')}">
<link rel="stylesheet" href="${web.href('/assets/css/bootstrap/4.6.0/bootstrap.min.css')}">
<link rel="stylesheet" href="${web.href('/assets/icons/fontawsome/5.15.2/css/all.css') }" />

<c:forEach var="style" items="${web.styles}">
	${style}
</c:forEach>

<%-- ADDING SCRIPTS --%>
<c:forEach var="script" items="${web.scripts}">
	${script}
</c:forEach>

<script type="text/javascript" src="${web.href('/assets/js/frontend.js')}"></script>
<script type="text/javascript" src="${web.href('/assets/js/jquery/jquery-3.5.1.js') }"></script>
<script type="text/javascript" src="${web.href('/assets/js/bootstrap/4.6.0/bootstrap.bundle.min.js')}"></script>