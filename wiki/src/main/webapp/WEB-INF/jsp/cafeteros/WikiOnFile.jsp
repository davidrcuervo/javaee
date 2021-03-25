<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set scope="request" var="content">

	<c:if test="${wiki.file.isDirectory() }">

		<%-- 
			<h2>Directories</h2>
			<c:forEach var="directory" items="${wiki.directories}">
				<div>
					<c:set var="hrefLink" value="${pageContext.request.servletPath}${pageContext.request.pathInfo}/${directory.name}" />
					<a href="${web.href(hrefLink)}" >
						<c:out value="${directory.name}" />
					</a>			
				</div>
			</c:forEach>
		--%>

		<h2 style="margin: 1em 0em 1em 0em;">Tournaments</h2>
		<c:forEach var="file" items="${wiki.files}">
			<div>
				<c:set var="hrefLink" value="${pageContext.request.servletPath}${pageContext.request.pathInfo}/${file.name}" />
				<a href="${web.href(hrefLink)}"><c:out value="${file.name}" /></a>
			</div>
		</c:forEach>
	</c:if>
	
	<c:if test="${wiki.file.isFile()}">
		<c:set target="${web}" property="style">
			<!-- <link href="http://thomasf.github.io/solarized-css/solarized-light.min.css" rel="stylesheet"></link> -->
		</c:set>
		<div id="emacs"><c:out value="${wiki.body}" escapeXml="false"/></div>
	</c:if>
	
<%--
		<div>Path info: <c:out value="${pageContext.request.pathInfo}" /></div>
		<div>Context path: <c:out value="${pageContext.request.contextPath}" /></div>
		<div>Servlet path: <c:out value="${pageContext.request.servletPath}" /></div>
		<div>URI: <c:out value="${pageContext.request.requestURI}" /></div>
		<div>Init param: <%= getServletConfig().getInitParameter("public") %></div>
		<div>Init param: ${pageContext.servletConfig.getInitParameter(pageContext.request.servletPath)}</div>
		<div>Directories: ${wiki.file.absolutePath}</div>
 --%>
</c:set>

<jsp:include page="/WEB-INF/jsp/template/default.jsp"></jsp:include>