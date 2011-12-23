<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>

<fieldset>
<legend><h2>Accounting</h2></legend>


<a>Total Charges: <span style="float:right;">${totalCharge}</span></a>
<br>
<br>
<ul id="os">
	<c:forEach var="i" items="${charges}">
		<li class="os">${i}</li>
	</c:forEach>
</ul>

</fieldset>