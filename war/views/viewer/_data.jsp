<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>

<c:set var="i" value="0" />

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
				<td id="${keys[i]}${2*j}" class="tbl_contract_data bid">${contract[0][j]}
					-</td>
				<td id="${keys[i]}${2*j+1}" class="tbl_contract_data ask">${contract[1][j]}
					-</td>
			</tr>

		</c:forEach>


		<c:set var="i" value="${i+1}" />

	</c:forEach>
</table>

