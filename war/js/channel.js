var channel = new goog.appengine.Channel($('meta[name=channel_token]').attr("content"));
var socket = channel.open();

socket.onopen = function() {
	alert("opened channel");
};

socket.onmessage = function(message) {
	alert("Message:" + message.data);
}


socket.onclose = function() {
	alert("closed channel");
};

	
socket.onerror = function(err) {
	alert("Error");
};
