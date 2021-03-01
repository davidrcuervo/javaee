<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>   

<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <a class="navbar-brand" href="${web.href('/home')}"><i class="fas fa-home"></i> Home</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarText" aria-controls="navbarText" aria-expanded="false" aria-label="Toggle navigation">
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarText">
    <ul class="navbar-nav mr-auto">
      <li class="nav-item ${web.active == 'store' ? 'active' : '' }">
        <a class="nav-link" href="${web.href('/app/store')}"> Store</a>
      </li>
      <li class="nav-item ${web.active == 'wiki' ? 'active' : '' }">
        <a class="nav-link" href="${web.apphref('/home', 'wiki')}">Wiki</a>
      </li>
      <li class="nav-item dropdown ${web.active == 'settings' ? 'active' : '' }">
        <a class="nav-link dropdown-toggle" href="#" id="navbarDropdown" title="Settings" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
          <i class="fas fa-user-cog"></i> Settings
        </a>
        <div class="dropdown-menu" aria-labelledby="navbarDropdown">
          <a class="dropdown-item" href="${web.href('/settings/user')}">User Settings</a>
          <a class="dropdown-item" href="${web.apphref('/secure', 'wiki')}">Wiki Settings</a>
          <div class="dropdown-divider"></div>
          <a class="dropdown-item" href="#">Something else here</a>
          <c:if test="${not empty pageContext.request.userPrincipal}">
          	<a class="dropdown-item" href="${web.href('/logout')}">LogOut</a>
          </c:if>
        </div>
      </li>
    </ul>

  </div>
</nav>