<%@page import="java.text.SimpleDateFormat"%>
<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory"%>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService"%>
<%@ page import="java.util.*"%>
<%@ page import="edu.columbia.e6998.cloudexchange.client.PositionEntry"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>

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
<div id="header">
		<jsp:include page="/views/_topbar.jsp" />
</div>

<div id="main">
	<table>
		<tr>
			<td id="mainTableCell">
				<div id="left">
				<table id="settingsTable">
					<tr><td>
					<b>My Amazon Credentials</b>
					<br><br>
					<a>Current File:&nbsp ${fileName}</a>
					<br>
					<br>
					
					<form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
     					<input type="file" name="myFile">
     					<br>
     					<br>
     					<input id="upload_submit" type="submit" value="Submit">
     				</form>
     				
     				<br><br>
     				</td></tr>
     				
     				<form action="/account" method="post">
     				<tr><td>
     					<b>Default AMI</b> 
     					<br>
     					<select id="instancetype" name="defaultAmi">
							<c:forEach var="i" items="${amis}">
								<c:choose>
									<c:when test="${i == defaultAmi}">
										<option selected value="${i}" class="instances">${i}</option>
									</c:when>
									<c:otherwise>
										<option value="${i}" class="instances">${i}</option>
									</c:otherwise>
								</c:choose>
							</c:forEach>
						</select>
     				</td></tr>
     				<tr><td>
     					<b>Default Key Pair</b>
     					<br>
     					<input type="text" name="keyPair" value="${keyPair}">
     					<br>
     				</td></tr>
     				<tr><td>
     					<b>Default Security Group</b>
     					<br>
     					<input type="text" name="securityGroup" value="${securityGroup}">
     					<br>
     				</td></tr>
     				<tr><td>
     					<input id="save_submit" type="submit" value="Save">
     				</td></tr>
     				</form>
     			</table>	
				</div>
			</td>
			
			<td id="mainTableCell">
				<div id="center">
					<b>My Portfolio</b>
					<br><br>
		
				<!-- 
				<c:if test="${empty positions}">
    				You have no positions in your portfolio
				</c:if>
				 -->
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
						<td align=center><b>Spot price</b></td>
					</tr>
					<c:set var="j" value="1"/>
					<c:forEach var="i" items="${positions}">
					<tr>
						<td>${j}</td>
						<td>${i.buyOrSell}</td>
						<td>${i.date}</td>
						<td>${i.region}</td>
						<td>${i.zone}</td>
						<td>${i.instance}</td>
						<td>${i.ami}</td>
						<td>${i.contractPrice}</td>
						<td>${i.spotPrice}</td>
					</tr>
					<c:set var="j" value="${j+1}"/>
					</c:forEach>
					</table>
				</div>
			</td>
			<td>
				<div class="right">
					<b>My Accounting</b>
					<br><br>
					<a>Total Charges: ${totalCharge}</a>
					<br><br>
					<ul id="os">
						<c:forEach var="i" items="${charges}">
						<li class="os">
							${i}
						</li>
						</c:forEach>
				</ul>
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

