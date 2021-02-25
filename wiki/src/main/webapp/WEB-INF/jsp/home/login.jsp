<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
<body>
	<h1>LogIn</h1>
	<form method="POST" action="j_security_check">
		<div>Username: </div>
		<div><input type="text" name="j_username" /></div>
		<div>Password: </div>
		<div><input type="password" name="j_password" /></div>
		<div><input type="submit" value="go" /></div>
	</form>
</body>
</html>