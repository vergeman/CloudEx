<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>



<form id="form_dialog">
	<fieldset style="display: inline;">

<!-- 
	<div id="dialog_action_container">
		<label for="dialog_action">Side</label> 
		<select id="dialog_action">
			<option selected value="buy">Buy</option>
			<option value="sell">Sell</option>
		</select> 
	</div>
-->
	<div id=dialog_wrap">
		<div id="dialog_qty_container">
			<label for="dialog_qty">Qty</label> 
			<input type="text" id="dialog_qty" value="1"/>

		</div>
			
			<div id ="dialog_inputs">
				<span class="ui-icon ui-icon-circle-plus"></span>
				<span class="ui-icon ui-icon-circle-minus"></span>
			</div>
		
		<div id="dialog_price_container">		
			<label for="dialog_price">Price</label> 
			<input type="text" id="dialog_price"/>
		</div>
		
			<div id ="dialog_inputs">
				<span class="ui-icon ui-icon-circle-plus"></span>
				<span class="ui-icon ui-icon-circle-minus"></span>
			</div>
	</div>

<hr>
	<div id="dialog_desc_container">
		<label for="dialog_desc">Contract</label>
		<div id="dialog_desc"></div>
	</div>

	</fieldset>
</form>
