<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>
 
<c:set target="${web}" property="title" value="Home" />
<c:set target="${web}" property="active" value="home" />
<c:set scope="request" var="content">
	<h1><i class="fas fa-home align-middle"></i><span class="align-middle">Welcome!!</span></h1>
	<div class="row">
		<div class="col-md-3" align="center">
			<a href="${web.apphref('/home.html', 'wiki') }">
				<div><i class="fas fa-book fa-5x"></i></div>
				<div class="text-align-center">Wiki</div>
			</a>
		</div>
	</div>
	 
<%-- 	
	<div><c:out value="${request.getSession(false) == null ? 'null' : 'not null'}" /></div>
	<div>Session id: <c:out value="${pageContext.session.id }" /></div>
	<c:if test="${not empty pageContext.request.userPrincipal}">
		<div>User is Logged In</div>
		
		<div>testid: <c:out value="${sessionScope.idtest}" /></div>
	</c:if>
	<c:if test="${empty pageContext.request.userPrincipal}">
		<div>User is not Logged In</div>
	</c:if>
	<div>username: <c:out value="${pageContext.request.userPrincipal.name}" /></div>
	<c:if test="${pageContext.request.isUserInRole('users')}">
		<div>User is in users group.</div>
	</c:if>
	<div>
		<div> Request server protocol: </div><c:out value="<%= request.getScheme() %>" />
		<div> Request forwarded protocol: <c:out value="${pageContext.request.getHeader('x-forwarded-proto')}"/>
		<div> Request port: <c:out value="${pageContext.request.getHeader('X-Forwarded-Port')}" /></div>
	</div>
--%>	
</c:set>

<jsp:include page="/WEB-INF/jsp/template/frontendTemplate.jsp"></jsp:include>
