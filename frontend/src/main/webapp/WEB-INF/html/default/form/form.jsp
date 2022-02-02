<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>


<jsp:useBean id="form" class="com.laetienda.frontend.repository.FormRepoImpl" type="com.laetienda.frontend.repository.FormRepository">
	<jsp:setProperty name="form" property="object" value="${param.classname}" />
	<jsp:setProperty name="form" property="action" value="${param.accion }" />
</jsp:useBean>


<form method="${form.method}">

	<c:forEach var="input" items="${form.inputs}">
		
		<c:if test="${input.type eq 'TEXT' || input.type eq 'TEXT' || input.type eq 'PASSWORD' || input.type eq 'DATE' || input.type eq 'EMAIL' || input.type eq 'TIME'}">
			<%-- <c:set var="val" value="${form.getValue(input.name)}" /> --%> 
			<div class="form-group">
				<label for="${input.id}">${input.label}</label>
				<input type="${input.type}" name="${input.name}" value="${param[input.name]}" class="form-control" id="${input.id}" placeholder="${input.placeholder}" />
			</div>
			<c:forEach var="mistake" items="${mistakes.getMistakeByName(input.name)}">
				<div><c:out value="${mistake.title}" /></div>
				<div><span class="small"><c:out value="${mistake.detail}" /></span></div>
			</c:forEach>
			
		</c:if>

		<c:if test="${input.type eq 'SELECT_MULTIPLE' }">
			<div class="form-group">
				<label for="${input.id}">${input.label}</label>
				<select multiple class="form-control" id="${input.id}" name="${input.name }">
					<c:forEach var="option" items="${form.getOptions(input.options)}">
						<option value="${option.value}" ${option.selected ? 'selected' : ''} ${option.disabled ? 'disabled' : '' } >
							${option.label}
						</option>
					</c:forEach>
				</select>
			</div>
			<c:forEach var="mistake" items="${mistakes.getMistakeByName(input.name)}">
				<div><c:out value="${mistake.title}" /></div>
				<div><span class="small"><c:out value="${mistake.detail}" /></span></div>
			</c:forEach>
		</c:if>

	</c:forEach>
	<input type="hidden" name="classname" value="${form.classname }" />
	<button type="submit" name="action" value="${form.action }" class="btn btn-primary">${form.action}</button>
</form>
<c:forEach var="mistake" items="${mistakes.getMistakeByName(form.classname) }">
	<div><c:out value="${mistake.title }" /></div>
	<div><span class="small"><c:out value="${mistake.detail }" /></span></div>
</c:forEach>
<c:forEach var="mistake" items="${mistakes.getMistakeByName('exception') }">
	<div><c:out value="${mistake.title }" /></div>
	<div><span class="small"><c:out value="${mistake.detail }" /></span></div>
</c:forEach>