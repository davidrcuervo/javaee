<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="grepo" class="com.laetienda.webdb.repository.GroupRepoImpl" type="com.laetienda.webdb.repository.GroupRepository">
	<jsp:setProperty name="grepo" property="user" value="${pageContext.request.userPrincipal.name}"/>
	<jsp:setProperty name="grepo" property="entityManagerFactory" value="${emf}" />
</jsp:useBean>

<c:set var="content" scope="request">
	<h1>Groups</h1>
	<div>User: <c:out value="${grepo.username}" /></div>
	
	<table class="table table-striped">
		<c:forEach var="group" items="${grepo.allGroups}">
			<c:set var="editLink">/group/edit/${group.name}</c:set>
			<c:set var="showLink">/group/show/${group.name}</c:set>
			<c:set var="deleteLink">/group/delete/${group.name}</c:set>
			<tr>
				<td>
					<a class="" href="${web.href(showLink) }"><c:out value="${group.name}" /></a><br />
					<span class="small"><c:out value="${group.description}" /></span>
				</td>
				<td>
					<a class="btn btn-outline-primary btn-sm" role="button" href="${web.href(editLink) }">Edit</a>
					<a class="btn btn-outline-primary btn-sm" role="button" href="${web.href(deleteLink)}">Remove</a>
				</td>
			</tr>
		</c:forEach>
	</table>
	<a class="btn btn-primary btn-lg" href="${web.href('/group/add') }" role="button">New Group</a>
</c:set>

<jsp:include page="/WEB-INF/template/${settings.get('template')}/webdb.template.jsp"></jsp:include>