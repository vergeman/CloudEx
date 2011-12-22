var token = $('meta[name=channel_token]').attr("content");
var channel = new goog.appengine.Channel(token);
var socket = channel.open();

socket.onopen = function() {
	//alert("opened channel");
};

socket.onmessage = function(message) {
	var data = $.parseJSON(message.data);
	
	try {
		var msg = data['msg'][0];
		var action = data['action'][0];
		var key = data['key'][0];
		var price = data['price'][0];
		var qty = data['qty'][0];
	
		if ( $('#'+key).length > 0) {
			
			$('#' + key).html(price);
		}
	}
	catch(err) {
		console.log("channel data rcv err");
	}

};


socket.onclose = function() {
	//alert("closed channel");
};

	
socket.onerror = function() {
	//alert("Error");
};







$('.bid, .ask').click(function() {

	/*we'll leave bid/offer change for later*/
	$('#dialog').attr('data', $(this).attr('id'));
	var datastr = $('#dialog').attr('data');
	var id = parseInt(datastr.substring(16, datastr.length));
	var bid_id;
	var ask_id;
	
	if ( id % 2 == 0) {
		bid_id = datastr.substring(0, 16) + id;
		ask_id = datastr.substring(0, 16) + (id + 1);
	}
	if ( id % 2 == 1) {
		bid_id = datastr.substring(0, 16) + (id -1);
		ask_id = datastr.substring(0, 16) + id;
	}
	$('#dialog').attr('bid_data', bid_id);
	$('#dialog').attr('ask_data', ask_id);

	
	if ($(this).hasClass('bid')) {
		$('#dialog_action').val('buy');
		if ($(this).text().length <=0) {
			$('#dialog_price').val(0.001);
		}
		else {
			$('#dialog_price').val((parseFloat($(this).text()) + .001).toFixed(3));
		}
	}

	if ($(this).hasClass('ask')) {
		$('#dialog_action').val('sell');
		if ($(this).text().length <=0) {
			$('#dialog_price').val(0.001);
		}
		else {
			$('#dialog_price').val(Math.max(0, (parseFloat($(this).text()) - .001)).toFixed(3));
		}
	}
	
	//description
	var day = $.trim($('#' + $(this).attr('id').substring(0, 16)).text());
	var hour = $.trim($(this).parent().children('.hour').text());
	var zone = $.trim($('.selected.zones').text());
	var os = $.trim($('.selected.os').text());
	var instance = $('#instancetype').val();

	$('#dialog_desc').html("<p>" + day + " " + hour + 
			"</p><p>" + " " + zone + " " + os + " " + instance + "</p>");

	$('#dialog').dialog("open");
	
	
	$('.ui-dialog-buttonpane button:first').css("margin-left", "16px");
	$('.ui-dialog-buttonpane button:last').css("margin-left", "140px");
	
	$('.ui-dialog-buttonpane button:first').removeClass('ui-corner-all');
	$('.ui-dialog-buttonpane button:last').removeClass('ui-corner-all');
	
	$('.ui-button-text:first').css("background-color", "#8B0000");
	$('.ui-button-text:last').css("background-color", "green");


});



