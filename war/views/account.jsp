
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.columbia.e6998.cloudexchange.client.PositionEntry"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>



<!doctype html>
<html>
<head>
<jsp:include page="/views/_header.jsp" />
</head>

<body>
	<div id="header">
		<jsp:include page="/views/_topbar.jsp" />
	</div>

	<div id="main">
		<div id="container">

			<div id="user_setup">
				<div id="creds_container">
					<jsp:include page="/views/account/_creds.jsp" />
				</div>
				
				<div id="accounting_container">
					<jsp:include page="/views/account/_accounting.jsp" />
				</div>
			</div>

			<div id="portfolio_container">
				<jsp:include page="/views/account/_portfolio.jsp" />
			</div>

			<hr style="clear:both; visibility:hidden;">
		</div>

	</div>


	<div id="footer">
		<jsp:include page="/views/_footer.jsp" />
	</div>
	
	<jsp:include page="/views/_scripts.jsp" />
</body>
</html>

