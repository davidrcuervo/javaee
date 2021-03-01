<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>

<div class="row justify-content-md-center">
<div class="col-sm-4">
	<h2>Please login:</h2>
	<form method="POST" action="j_security_check">
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
</div>
</div>
<c:out value="${loginError}" escapeXml="false"/>