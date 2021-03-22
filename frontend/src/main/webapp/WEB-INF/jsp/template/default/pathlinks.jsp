<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %> 

<c:set var="appname"><%= request.getParameter("context").substring(1) %></c:set>
<c:set var="srvtname"><%= request.getParameter("servlet").substring(1) %></c:set>
<c:set var="pparts" value="${fn:split(param.path, '/')}" />
<c:set var="pathLink" value="" />

<div>
	/ <a href="${web.href('/home')}">Home</a>
	/ <a href="${web.apphref('/home', appname)}"><c:out value="${appname}" /></a>
	/ <a href="${web.apphref(param.servlet, appname)}"><c:out value="${srvtname}" /></a>
	<c:forEach var="ppart" items="${pparts}">
		<c:set var="pathLink">${pathLink}/${ppart}</c:set>
		<c:set var="completeLink">${param.servlet}${pathLink}</c:set>
		/ <a href="${web.apphref(completeLink, appname)}"><c:out value="${ppart}" /></a>
	</c:forEach>
</div>
<%--
<div>	
	<c:out value="${param.context}" />
	<c:out value="${srvtname}" /> 
	<c:out value="${param.servlet }" />
	<c:out value="${param.path}" />
</div>
 --%>