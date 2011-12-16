<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>


<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>



<!doctype html>

<html>
<head>
	<jsp:include page="/views/_header.jsp" />
	<link type="text/css" rel="stylesheet" href="/stylesheets/viewer.css">
</head>


<body>

	<div id="header">
		<jsp:include page="/views/_topbar.jsp" />
	</div>

	<div id="main">
		<div id="container">
		
			<div id="selection_container">
				<div id="selection">
					<jsp:include page="/views/viewer/_selection.jsp" />
				</div>
			</div>
			<hr>
			
			<div id="spotprice_container">
				<div id="spotprice">
					<jsp:include page="/views/viewer/_spotprice.jsp" />
				</div>
			</div>
			
			<div id="data_container">
				<div id="data">
					<jsp:include page="/views/viewer/_data.jsp" />
				</div>
			</div>
		</div>
	</div>

	<!--  this is our trade submission modal dialog box -->
	<div id="dialog" title="Trade Execution">
		<jsp:include page="/views/viewer/_submit.jsp" />
	</div>
	
	
	<div id="footer">
		<jsp:include page="/views/_footer.jsp" />
	</div>


	<jsp:include page="/views/_scripts.jsp" />

	<!-- initial data containers -->
	<script type="text/javascript">
		contract_data = ${contracts_json};
		dates_data = ${dates_json};
	</script>



</body>



</html>
