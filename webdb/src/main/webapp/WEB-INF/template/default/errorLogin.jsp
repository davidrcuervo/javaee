<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="logintemplate" class="com.laetienda.lib.http.HttpTemplate" type="com.laetienda.lib.http.TemplateRepository" scope="request">
	<jsp:setProperty name="logintemplate" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

${logintemplate.setPostParameter("loginerror", "true")}

<c:set var="template" value="${settings.get('template') }" />
<jsp:include page="/WEB-INF/html/${template}/login.jsp"></jsp:include>