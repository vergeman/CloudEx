<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<div id="userInfo">
<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user != null) {
%>
<ul>
	<!-- logout action (you are signed in) -->
	<li>
		<strong><%= user.getEmail() %></strong>
		|
	</li>
	<li>
		<a href="/account">My CloudEx Account</a>
		|
	</li>
	<li>
	 	<a href="<%=userService.createLogoutURL("/")%>">Logout</a>
	 </li>
</ul>
<%
	} 
	else {
%>
<ul>
	<!--  login action (you are signed out) -->
	<li>
		<a href="<%=userService.createLoginURL("/main")%>">Login</a>
	</li>
</ul>
<%
	}
%>
</div>