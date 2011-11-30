<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<noscript>
	<div
		style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
		Your web browser must have JavaScript enabled in order for this
		application to display correctly.</div>
</noscript>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"
	type="text/javascript">
	
</script>

<script
	src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"
	type="text/javascript"></script>

<script src="/js/main.js" type="text/javascript"></script>

<c:if test="${not empty token}">
	<script src="/_ah/channel/jsapi" type="text/javascript"></script>
	<script src="/js/channel.js" type="text/javascript"></script>
</c:if>
