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
		<div id="container">

			<div id="welcome">

				<h1>
					The Cloud Exchange
					<h1>
			</div>


			<div id="about">
				<p style="text-align: center; font-size: 1.2em">Welcome</p>
				<div id="summaries">
					<div id="a">

						<p><span style="font-weight:700;">The Cloud Exchange</span>
						<br><br> An automated exchange platform for
							trading Spot Instance futures.</p>
					</div>

					<div id="b">
						<p><span style="font-weight:700;">Spot Instance Futures</span>
						<br><br>A contract between two
							parties to exchange ownership over an EC2 Spot Instance at
							a specified future date.</p>
					</div>

					<div id="c">
						<p><span style="font-weight:700;">Trade!</span><br>
						<br>Login
						 <br>Setup your account
						 <br>Enjoy</p>
					</div>
				</div>
			</div>

		</div>


	</div>
	</div>

	<div id="footer">
		<jsp:include page="/views/_footer.jsp" />
	</div>


	<jsp:include page="/views/_scripts.jsp" />

</body>


</html>
