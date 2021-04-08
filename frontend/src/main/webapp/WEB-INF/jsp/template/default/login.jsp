<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<c:set var="accion" value="${param.action == null ? action : param.action}" />

<div class="row justify-content-md-center">
<div class="col-sm-4">
	<h2>Please login:</h2>
	<form method="POST" action="${accion}">
  		<div class="form-group">
    		<label for="usernameInput">Username</label>
    		<input type="text" class="form-control" id="usernameInput" name="j_username">
  		</div>
  		<div class="form-group">
		<label for="inputPassword">Password</label>
    		<input type="password" class="form-control" id="inputPassword" name="j_password">
  		</div>
  		<button type="submit" class="btn btn-primary btn-block">Submit</button>
	</form>
	<%-- <div>loginerror: <c:out value="${param.loginerror}" escapeXml="false"/></div> --%>
	<c:if test="${loginError eq 'true' || param.loginerror eq 'true'}">
		<div class="alert alert-danger" role="alert">
		  	Wrong password or username!!!.
		</div>
	</c:if>
	<%-- 
	<div>
		<div>action: <c:out value="${action}"/></div>
		<div>post action: <c:out value="${param.action}" /></div>
		<div>X-Forwarded-For: <c:out value="${pageContext.request.getHeader('X-Forwarded-For')}" /></div>
	</div>
	--%>
</div>
</div>