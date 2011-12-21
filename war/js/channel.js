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
	
	if ($(this).hasClass('bid')) {
		$('#dialog_action').val('buy');
		$('#dialog_price').attr('value', $(this).val().replace("-", ""));
	}

	if ($(this).hasClass('ask')) {
		$('#dialog_action').val('sell');
		$('#dialog_price').attr('value', $(this).val().replace("-", ""));
	}
	
	$('#dialog').dialog("open");

	
});



