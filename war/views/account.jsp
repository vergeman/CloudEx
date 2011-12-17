<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page
	import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>

<%
	BlobstoreService blobstoreService = BlobstoreServiceFactory
			.getBlobstoreService();
%>

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
     <h1>My CloudEx Account</h1>
</div>

<div id="main">
<table>
<tr>
<td id="mainTableCell">
<div id="left">
<b>My Amazon Credentials</b>
<br><br>
<a>Current File:&nbsp ${fileName}</a>
<br>
<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
     <input type="file" name="myFile">
     <br>
     <input type="submit" value="Submit">
     </form>
</div>
</td>

<td id="mainTableCell">
<div id="center">
<b>My Portfolio</b>
<table class="positionTable">
		<tr>
			<td align=center><b>#</b></td>
			<td align=center><b>Buy/Sell</b></td>
			<td align=center><b>Date</b></td>
			<td align=center><b>Time</b></td>
			<td align=center><b>Ami</b></td>
			<td align=center><b>Instance</b></td>
			<td align=center><b>Region</b></td>
			<td align=center><b>Zone</b></td>
			<td align=center><b>Contract price</b></td>
			<td align=center><b>Spot price</b></td>
		</tr>
</table>
</div>
</td>
</tr>
</table>
</div>
<div id="footer">
<jsp:include page="/views/_footer.jsp" />
</div>
<jsp:include page="/views/_scripts.jsp" />
  </body>
</html>

