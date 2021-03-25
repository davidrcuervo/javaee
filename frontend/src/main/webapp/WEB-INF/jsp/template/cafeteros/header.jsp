<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>   

<jsp:useBean id="web" scope="request" class="com.laetienda.lib.tomcat.WebEngine" >
	<jsp:setProperty name="web" property="httpServletRequest" value="<%=request%>" />
</jsp:useBean>

<c:set target="${web}" property="icon" value="${web.href('/assets//icons/other/supercampeones.png')}" />

<jsp:include page="/WEB-INF/jsp/template/default/header.jsp"></jsp:include>