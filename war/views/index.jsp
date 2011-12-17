<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

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
		<div id="container">This is our landing page.</div>
	</div>

	<div id="footer">
		<jsp:include page="/views/_footer.jsp" />
	</div>


	<jsp:include page="/views/_scripts.jsp" />

</body>


</html>
