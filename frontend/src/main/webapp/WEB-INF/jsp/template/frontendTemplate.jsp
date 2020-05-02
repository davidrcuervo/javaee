<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html" charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>${title == null ? 'WEB' : title}</title>
	
	<%--ADDING STYLES --%>
	<c:forEach var="style" items="${web.styles}">
		${style}
	</c:forEach>

	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
	<link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css" integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh" crossorigin="anonymous">
	<link rel="stylesheet" href="${web.href('/assets/css/frontend.css')}" />
	<link rel="stylesheet" href="${web.href('/assets/css/cellphone.css')}" />
	<link rel="stylesheet" href="${web.href('/assets/css/tablet.css')}" />
	<link rel="stylesheet" href="${web.href('/assets/css/desktop.css')}" />

	
	<%-- ADDING SCRIPTS --%>
	<c:forEach var="script" items="${web.scripts}">
		${script}
	</c:forEach>
	
	<script type="text/javascript" src="${web.href('/assets/js/frontend.js')}"></script>
	<script src="https://code.jquery.com/jquery-3.5.0.slim.min.js"></script>
	<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js" integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6" crossorigin="anonymous"></script>
</head>
<body>
	<header>
		<nav id=topNav>
			<ul>
				<li class="show ${active == 'home' ? 'active' : ''}"><a href="${web.href('/home')}"><i class="fa fa-fw fa-home"></i> <span class="navtext">Home</span></a></li>
				<li class="show right onlymobile"><a href="#"><i class="fa fa-bars"></i></a></li>
				<li><a href="#"><i class="fa fa-fw fa-envelope"></i> <span class="navtext">Contact</span></a></li>
				<li class="rightOnWide ${active == 'login' ? 'active' : ''}"><a href="${web.href('/login')}"><i class="fa fa-fw fa-user"></i> <span class="navtext">Login</span></a></li>
				<li class="rightOnWide"><i class="fa fa-fw fa-search"></i><input class="search" type="text" placeholder="Search.."></li>
			</ul>
		</nav>
	</header>
	<main>${content}</main>
</body>
</html>