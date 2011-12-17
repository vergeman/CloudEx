<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>



<form id="form_dialog">
	<fieldset style="display: inline;">

		<label for="dialog_action">Side</label> <select id="dialog_action">
			<option selected value="buy">Buy</option>
			<option value="sell">Sell</option>
		</select> <label for="dialog_qty">Qty</label> <input type="text"
			id="dialog_qty" value="1" style="width: 10px;" /> <label
			for="dialog_price">Price</label> <input type="text" id="dialog_price"
			style="width: 40px;" />


		<div id="dialog_desc">
			<p>we'll space and align this horizontally and make buttons to
				increment & decrement prices</p>
			<p>instead of this babble we'll have a description of the
				contract</p>
		</div>


	</fieldset>
</form>
