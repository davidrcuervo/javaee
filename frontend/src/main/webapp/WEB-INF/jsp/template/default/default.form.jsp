<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="form" type="com.laetienda.frontend.repository.FormRepository" class="com.laetienda.frontend.repository.FormRepoImpl" >
</jsp:useBean>

<jsp:useBean id="dbrow" type="com.laetienda.lib.form.Form">
</jsp:useBean>

<form method="${form.method}">
	
	<c:forEach var="input" items="${form.inputs}">
		
		<c:if test="${input.type eq 'TEXT' || input.type eq 'TEXT' || input.type eq 'PASSWORD' || input.type eq 'DATE' || input.type eq 'EMAIL' || input.type eq 'TIME'}">
			<div class="form-group">
				<label for="${input.id}">${input.label}</label>
				<input type="${input.type}" class="form-control" id="${input.id}" placeholder="${input.placeholder}" />
			</div>
		</c:if>
		
		<c:if test="${input.type eq 'SELECT_MULTIPLE' }">
			<div class="form-group">
				<label for="${input.id}">${input.label}</label>
				<select multiple class="form-control" id="${input.id}">
					<c:set var="tmpjsonname">${input.options}</c:set>
					<c:set var="json">${param[tmpjsonname]}</c:set>
					<c:forEach var="option" items="${input.getOptions(json)}">
						<option value="${option.value}" ${option.selected ? 'selected' : ''} ${option.disabled ? 'disabled' : '' } >
							${option.label}
						</option>
					</c:forEach>
				</select>
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
	<button type="submit" class="btn btn-primary">${form.action}</button>
</form>