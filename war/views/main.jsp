<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>

<!doctype html>

<html>
  <head>
	<jsp:include page="/views/_header.jsp" />
  </head>


  <body>

	<div id="auth">
		<jsp:include page="/views/_auth.jsp" />
	</div>


	<div id="header">
    	<h1>Main View</h1>

	</div>

	<div id="container">
		This is our main client page.
	</div>


	<div id="footer">
		Project footnotes
	</div>



	<jsp:include page="/views/_scripts.jsp" />
	
  </body>
   

</html>
