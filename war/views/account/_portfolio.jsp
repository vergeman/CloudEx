<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>

<h2>Portfolio</h2>
<br>
<c:choose>
	<c:when test="${empty positions}">
	    	You have no positions in your portfolio
	</c:when>
	<c:otherwise>
		<table class="positionTable">
			<thead>
				<th><b>#</b></th>
				<th><b>Buy/Sell</b></th>
				<th><b>Date/Time</b></th>
				<th><b>Region</b></th>
				<th><b>Zone</b></th>
				<th><b>Instance Type</b></th>
				<th><b>Ami</b></th>
				<th><b>Contract price</b></th>
				<th></th>
			</thead>

			<form action="/account" name="ami"  method="post">

				<c:set var="j" value="1" />
				<c:forEach var="i" items="${positions}">
					<tr>
						<td>${j}</td>
						<td>${i.buyOrSell}</td>
						<td>${i.date}</td>
						<td>${i.region}</td>
						<td>${i.zone}</td>
						<td>${i.instance}</td>
						<td><select id="instancetype" name="ami${i.transactionKey}">
								<c:forEach var="k" items="${amis}">
									<c:choose>
										<c:when test="${k == i.ami}">
											<option selected value="${k}" class="instances">${k}</option>
										</c:when>
										<c:otherwise>
											<option value="${k}" class="instances">${k}</option>
										</c:otherwise>
									</c:choose>
								</c:forEach>
						</select></td>
						<td>${i.contractPrice}</td>
						<td><input name="save${i.transactionKey}" type="submit"
							value="Save"></td>
					</tr>
					<c:set var="j" value="${j+1}" />
				</c:forEach>


			</form>

		</table>
	</c:otherwise>
</c:choose>