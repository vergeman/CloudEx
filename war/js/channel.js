var token = $('meta[name=channel_token]').attr("content");
var channel = new goog.appengine.Channel(token);
var socket = channel.open();

socket.onopen = function() {
	alert("opened channel");
};

socket.onmessage = function(message) {
	var data = $.parseJSON(message.data);
	/*will have to delineate what exactly is being replaced*/
	var msg = data['msg'][0];
    $('#message_result').html(msg);
    
};


socket.onclose = function() {
	alert("closed channel");
};

	
socket.onerror = function() {
	//alert("Error");
};


$('#send_message').click(function(){
    var msg = $('#input_message').val();
    var channelkey = $('meta[name=channel_token]').attr("content");
    
    $.ajax({
        url: '/message/',
        type: 'POST',
        data:{
            msg:msg,
            channelkey:channelkey,
        },
        success: function(data){},
        complete:function(){}
    });
});
