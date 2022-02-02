<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="content" scope="request">
	<h1>Add New Group</h1>

	<jsp:include page="/WEB-INF/html/${settings.get('frontend.template')}/form/form.jsp">
       	<jsp:param name="classname" value="com.laetienda.model.webdb.Group" />
       	<jsp:param name="accion" value="CREATE" />
	</jsp:include>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>