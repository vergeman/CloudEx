<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>

<!--  we may change this to a list if tables aren't friendly -->
<c:set var="i" value="0" />

<table id="tbl_contract">

	<thead>
	<tr class="tbl_contract_headers">
		<th>Date</th>
		<th>Hour</th>
		<th>Sell</th><!-- action status -->
		<th class="bid_qty">Qty</th>
		<th>Bid</th>
		<th>Ask</th>
		<th class="ask_qty">Qty</th>
		<th>Buy</th><!-- action status -->
		<th>Last</th>
	</tr>
	</thead>

	<c:forEach var="contract" items="${contracts}">
		
		<tr class="tbl_contract_date_row ${keys[i]}">
		
			<td id="${keys[i]}" class="tbl_contract_date">
				<span class="arrow ui-icon ${i ==0 ? 'ui-icon-circle-triangle-s' : 'ui-icon-circle-triangle-e'}" style="float:left;"></span>
				${dates[i]}
			</td>
		
		<td></td>
		<td></td>
		<td class="bid_qty"></td>
		<td></td>
		<td class="spot_price">${spotprices[i]}</td>
		<td class="ask_qty"></td>
		<td></td>
		<td></td>		
		</tr>
		
		<c:forEach var="j" begin="0" end="23" step="1">

				<tr class="tbl_contract_row hour ${keys[i]} ${j % 2 ==0 ? '' : 'odd'} ${i ==0 ? '' : '"style=display:none;"'}">
				<td></td>
				<td class="tbl_contract_data hour">${j}:00</td>
				<td class="tbl_contract_data hit">HIT</td>
				<td class="tbl_contract_data bid_qty"> </td>
				<td id="${keys[i]}${2*j}" class="tbl_contract_data bid">${contract[0][j]}</td>
				<td id="${keys[i]}${2*j+1}" class="tbl_contract_data ask">${contract[1][j]}</td>
				<td class="tbl_contract_data ask_qty"> </td>
				<td class="tbl_contract_data lift">LIFT</td>
				<td class="tbl_contract_data last"> </td>
			</tr>

		</c:forEach>


		<c:set var="i" value="${i+1}" />

	</c:forEach>
</table>

