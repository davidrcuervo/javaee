<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="loginTemplate" class="com.laetienda.lib.bean.HttpTemplate" scope="request">
	<jsp:setProperty name="loginTemplate" property="url" value="${settings.get('frontend.url') }" />
</jsp:useBean>

${requestScope.loginTemplate.setPostParameter("loginerror", "true")}

<jsp:include page="/WEB-INF/jsp/home/login.jsp"></jsp:include>