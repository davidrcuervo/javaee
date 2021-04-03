<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="formtemplate" scope="request" class="com.laetienda.lib.http.HttpTemplate" type="com.laetienda.lib.http.TemplateRepository" />
<jsp:setProperty name="formtemplate" property="url" value="${settings.get('frontend.url') }" />	


<c:set var="content" scope="request">
	<h1>Group</h1>
	<c:out value="${formtemplate.getHtmlTemplate('form/form.html')}" escapeXml="false" />
</c:set>

<jsp:include page="/WEB-INF/template/${settings.get('template')}/webdb.template.jsp"></jsp:include>