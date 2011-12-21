<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.columbia.e6998.cloudexchange.client.PositionEntry"%>

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
					<br>
					<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
     					<input type="file" name="myFile">
     					<br>
     					<br>
     					<input type="submit" value="Submit">
     				</form>
				</div>
			</td>
			
			<td id="mainTableCell">
				<div id="center">
					<b>My Portfolio</b>
					<br><br>
					
<%
 	ArrayList<PositionEntry> entries = null;
 	try {
 		entries = (ArrayList<PositionEntry>) request.getAttribute("positions");
 	} catch (Exception e) {
 		// oops
 	}
 	if (entries == null) {
 		out.println("<a> You have no positions in your portfolio </a>");
 	} else if (entries.size() == 0) {
 		out.println("<a> You have no positions in your portfolio </a>");
 	} else {
 		
 %>
				<table class="positionTable">
					<tr>
						<td align=center><b>#</b></td>
						<td align=center><b>Buy/Sell</b></td>
						<td align=center><b>Date/Time</b></td>
						<td align=center><b>Region</b></td>
						<td align=center><b>Zone</b></td>
						<td align=center><b>Instance Type</b></td>
						<td align=center><b>Ami</b></td>
						<td align=center><b>Contract price</b></td>
						<td align=center><b>Bid/Ask price</b></td>
						<td align=center><b>Spot price</b></td>
					</tr>
		<%
		int i = 1;
		SimpleDateFormat sfd = new SimpleDateFormat("MM/dd/yyyy hh a");
		for (PositionEntry e : entries) {
			out.println("<tr>");
			out.println("<td>" + i + "</td>");
			out.println("<td>" + e.buyOrSell + "</td>");
			out.println("<td>" + sfd.format(e.date) + "</td>");
			out.println("<td>" + e.region + "</td>");
			out.println("<td>" + e.zone + "</td>");
			out.println("<td>" + e.instance + "</td>");
			out.println("<td>" + e.ami + "</td>");
			out.println("<td>" + e.contractPrice + "</td>");
			out.println("<td>" + e.bidAskPrice + "</td>");
			out.println("<td>" + e.spotPrice + "</td>");
			out.println("</tr>");
		}
		%>
					</table>
				</div>
<%
	// end of big else
 	}
%>
			</td>
			<td>
				<div class="right">
					<b>My Accounting</b>
					<br><br>
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

