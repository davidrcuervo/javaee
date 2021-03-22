<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="content" scope="request">
	<h1 style="margin: 1em 0em 1em 0em;">Cafeteros Futbol Club</h1>
	
	<div class="container">
		<div class="row">
			<div class="col">
				<a href="${web.apphref('/tournaments', 'wiki') }">
					<div><i class="fas fa-book fa-5x"></i></div>
					<div class="text-align-center">Tournaments</div>
				</a>
			</div>
		</div>
	</div>
</c:set>
<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>