<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="edu.columbia.e6998.cloudexchange.aws.AWSCodes"%>
<%@ page import="edu.columbia.e6998.cloudexchange.aws.AWSCodes.*"%>
<%@ page import="com.google.appengine.api.datastore.Entity" %>

<%@ page import="java.util.Calendar" %>
<%@ page import= "java.util.Date" %>
<%@ page import="edu.columbia.e6998.cloudexchange.toolkit.GenericToolkit" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isELIgnored="false" %>

<%
	pageContext.setAttribute("regions", AWSCodes.Region.values());
	pageContext.setAttribute("zones", AWSCodes.Zones.values());
	pageContext.setAttribute("zone", AWSCodes.Zone.values());
	pageContext.setAttribute("os", AWSCodes.OS.values());
	pageContext.setAttribute("instancetype",AWSCodes.InstanceType.values());
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
    	<h1>Main View</h1>

	</div>

<div id="main">
	<div id="side">

	</div>
	<div id="container">
		<p>This is our main client page.</p>
		<p>styling can come later, working on mechanics for now</p>
	</div>
</div>

<!-- 
	<div id="message">
		<input id="input_message" type="text" />
	</div>
	
	<button id="send_message"></button>
	
	<div id="message_result">
	
	</div>
 -->



	<div id="view">

		<ul id="regions">
		
			<c:forEach var="r" items="${regions}">
				<c:choose>

					<c:when test="${r == defaults[0]}">
						<li id="${r}" class="regions selected">
					</c:when>

					<c:otherwise>
						<li id="${r}" class="regions">
					</c:otherwise>

				</c:choose>
					${r.view_name}
				</li>

			</c:forEach>
		</ul>

		<ul id="zones">
			<c:forEach var="z" items="${zone}">

				<c:choose>
					<c:when test="${z == defaults[1]}">
						<li id="${z}" class="zones selected">
					</c:when>
					<c:otherwise>
						<li id="${z}" class="zones">
					</c:otherwise>
				</c:choose>
				
					${z.zone}
				</li>
			</c:forEach>
		</ul>


		<ul id="os">
			<c:forEach var="o" items="${os}">
				<c:choose>
					<c:when test="${o == defaults[2]}">
						<li id="${o}" class="os selected">
					</c:when>
					<c:otherwise>
						<li id="${o}" class="os">
					</c:otherwise>
				</c:choose>
					${o.description}
				</li>
			</c:forEach>
		</ul>

		<select id="instancetype">
			<c:forEach var="i" items="${instancetype}">
				<c:choose>
					<c:when test="${i == defaults[3] }">
						<option selected value="${i}" class="instances">${i.description}</option>
					</c:when>
					<c:otherwise>
						<option value="${i}" class="instances">${i.description}</option>
					</c:otherwise>
				</c:choose>
			</c:forEach>

		</select>


	</div>

	<hr>

	<div id="data">
		<c:set var="i" value = "0"/>

		<table id="tbl_contract">

			<tr class="tbl_contract headers">
				<th>Date</th>
				<th>Hour</th>
				<th>Bid</th>
				<th>Ask</th>
			</tr>

			<c:forEach var="contract" items="${contracts}">
					
					<td id="${date[i]}" class="tbl_contract date">${dates[i]}</td>
					<td></td>
					<td></td>
					<td></td>
				
				<c:forEach var="j" begin="0" end="23" step="1">

					<tr class="tbl_contract_row hour ${keys[i]}">
						<td></td>
						<td class="tbl_contract_data hour">${j}:00</td>
						<td id="${keys[i]}${2*j}" class="tbl_contract_data bid">${contract[0][j]} - </td>
						<td id="${keys[i]}${2*j+1}"class="tbl_contract_data ask">${contract[1][j]} - </td>
					</tr>
					
				</c:forEach>


				<c:set var="i" value="${i+1}" />

		</c:forEach>
		</table>

	<div id="dialog" title="Trade Execution">

		<form id = "form_dialog">
			<fieldset style="display:inline;">
				
				<label for="dialog_action">Side</label>
				<select id="dialog_action">
					<option selected value="buy">Buy</option>
					<option value="sell">Sell</option>
				</select>
		
				<label for="dialog_qty">Qty</label>
				<input type="text" id="dialog_qty" value="1" style="width:10px;"/>
			
				<label for="dialog_price">Price</label>
				<input type="text" id="dialog_price" style="width:40px;"/>
				
				
			<div id = "dialog_desc">
				<p>we'll space and align this horizontally and make buttons to increment & decrement prices</p>
				<p>instead of this  babble we'll have a description of the contract</p>
			</div>
			
			
			</fieldset>
		</form>
	</div>
	
</div>


	<div id="footer">
		<jsp:include page="/views/_footer.jsp" /> 
	</div>
	
<jsp:include page="/views/_scripts.jsp" />

<script type="text/javascript">
	 contract_data = ${contracts_json};
	 dates_data = ${dates_json};
</script>

	
	
  </body>
  


</html>
