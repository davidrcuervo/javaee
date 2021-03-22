<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="content" scope="request">
	<h1>Form Template</h1>
	<form method="${form.method}">
	
		<c:forEach var="input" items="${form.inputs}">
			
			<c:if test="${input.type eq 'TEXT' || input.type eq 'TEXT' || input.type eq 'PASSWORD' || input.type eq 'DATE' || input.type eq 'EMAIL' || input.type eq 'TIME'}">
				<div class="form-group">
					<label for="${input.id}">${input.label}</label>
					<input type="${input.type}" class="form-control" id="${input.id}" placeholder="${input.placeholder}" />
				</div>
			</c:if>
			
			<%-- 
			<c:out value="${input.type}" />
			<c:out value="${input.name}" />
			<c:out value="${input.label}" />
			<c:out value="${input.id}" />
			<c:out value="${input.order}" />
			
			<c:if test="${input.type eq 'TEXT' }">
				<div>input type is TEXT</div>
			</c:if>
			--%>

		</c:forEach>
	
	</form>
</c:set>

<jsp:include page="/WEB-INF/template/webdb.template.jsp"></jsp:include>