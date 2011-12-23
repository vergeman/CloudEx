<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false"%>



<form id="form_dialog">
	<fieldset style="display: inline;">

	<div id=dialog_wrap">
	
			<div id="dialog_qty_container" style="display:none;">
				<label for="dialog_qty" style="display:none;">Qty</label> 
				<input type="text" id="dialog_qty" value="1" style="display:none;"/>
			</div>
			
			<div id ="dialog_inputs" style="display:none;">
				<span class="ui-icon ui-icon-circle-plus" style="display:none;"></span>
				<span class="ui-icon ui-icon-circle-minus" style="display:none;"></span>
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


	<div id="dialog_desc_container" style="float:left;">
	<hr>
		<label for="dialog_desc">Contract</label>
		<div id="dialog_desc"></div>
	</div>

	</fieldset>
</form>
