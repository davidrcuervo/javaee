<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="loginTemplate" class="com.laetienda.lib.http.HttpTemplate" scope="request" type="com.laetienda.lib.http.TemplateRepository">
	<jsp:setProperty name="loginTemplate" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

<c:if test="${not empty loginTemplate }">
	${requestScope.loginTemplate.setPostParameter("loginerror", "true")}
</c:if>


<jsp:include page="/WEB-INF/html/${settings.get('template') }/user.login.jsp"></jsp:include>