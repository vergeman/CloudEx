 <%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>   
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<meta http-equiv="content-type" content="text/html; charset=UTF-8">

<c:if test="${not empty token}">
	<meta name="channel_token" content="${token}" />
</c:if>

<link type="text/css" rel="stylesheet" href="/stylesheets/reset.css">

<link type="text/css" rel="stylesheet"
	href="/stylesheets/TheCloudExchange.css">



<title>The Cloud Exchange</title>