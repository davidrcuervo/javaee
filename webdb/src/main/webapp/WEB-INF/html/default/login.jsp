<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<jsp:useBean id="web" scope="request" class="com.laetienda.lib.tomcat.WebEngine">
	<jsp:setProperty name="web" property="httpServletRequest" value="<%=request%>"/>
</jsp:useBean>

<jsp:useBean id="logintemplate" class="com.laetienda.lib.http.HttpTemplate" type="com.laetienda.lib.http.TemplateRepository" scope="request">
	<jsp:setProperty name="logintemplate" property="url" value="${settings.get('frontend.template.url') }" />
</jsp:useBean>

<c:set var="action" value="${web.href('/j_security_check')}" />
${logintemplate.setPostParameter("action", action)}

<c:set var="hidePathLinksBar" scope="request" value="yes" />
<c:set scope="request" var="content">
	<c:out value="${logintemplate.getHtmlTemplate('/login')}" escapeXml="false" />
</c:set>

<jsp:include page="/WEB-INF/template/default/webdb.template.jsp"></jsp:include>