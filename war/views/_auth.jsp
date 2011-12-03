<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user != null) {
%>

<!-- logout action (you are signed in) -->
<p>
	 <a href="<%=userService.createLogoutURL("/")%>">
	 	Logout
	 </a>
	 <a>&nbsp<%= user.getEmail() %></a>
	 
</p>


<%
	} 
	
	else {
%>
<!--  login action (you are signed out) -->
<p>
	<a href="<%=userService.createLoginURL("/main")%>">
		Login
	</a>
</p>

<%
	}
%>