<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="loginTemplate" class="com.laetienda.lib.http.HttpTemplate" scope="request">
	<jsp:setProperty name="loginTemplate" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

${requestScope.loginTemplate.setPostParameter("loginerror", "true")}

<jsp:include page="/WEB-INF/html/default/login.jsp"></jsp:include>