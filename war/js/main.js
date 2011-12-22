/*our js file for bindings and what not*/

var contract_data
var dates_data;
var spotprice_data;

$(document).ready(function() {

	//hook in save on accounts page to populate with some preference 
	//(prevent null crazy loop)
	$('#upload_submit').click(function() {
		e.preventDefault();
		$('#upload_submit').submit();
		$('#save_submit').submit();
	});
	
	//load current markets
	update_current();
	
	$('.arrow').click(function() {
		var id = $(this).parent().attr('id');
		
		if ($(this).hasClass('ui-icon-circle-triangle-s')) {
			$(this).removeClass('ui-icon-circle-triangle-s');
			$(this).addClass('ui-icon-circle-triangle-e');
			
			$(".tbl_contract_row.hour." + id).toggle();
		}
		else {
			$(this).removeClass('ui-icon-circle-triangle-e');
			$(this).addClass('ui-icon-circle-triangle-s');
			
			$(".tbl_contract_row.hour." + id).toggle();
		}
	});

	
	$('#instancetype').change(function() {
		refresh_data();
	});

	
	$('#selection li').click(function() {
		
		//refresh "selected"
		if(!$(this).hasClass("selected")) {
			var classes = $(this).attr('class').split(' ');
			var sel_class = classes[0];
			var sel_region = classes[1];
        	
			$('.' + sel_class).removeClass("selected");
			$(this).addClass("selected");

			//if we changed regions, we need to refresh availability zones
			if (sel_class == "regions") {
				var region = $(this).attr('id');
				$('.zones').css('display', 'none');
				$('.' + region).toggle();
				
				$('.zones').removeClass("selected");
				$('.' + region).first().addClass("selected");
				
				
				//regions --> instance types: Only East offers clusters
				if ($(".regions.selected").attr('id') != 'US_EAST') {
					$('#instancetype option[value*="CLUSTER"]').hide();
					$('#instancetype').val(1);
				}
				else {
					$('#instancetype option[value*="CLUSTER"]').show();
				}	
			}

			// make post with block in message
        	
			refresh_data();
		}
	});
	

	$(".hit, .lift").click(function() {
		var channelkey = $('meta[name=channel_token]').attr("content");
		var key;
		var action;
		var qty = 1;
		var price;
		
		if ($(this).attr('class').split(' ')[1] == 'hit') {
			price = $(this).next().next().text();
			key = $(this).next().next().attr('id');
			action = "SELL";
			
			
		}
		if ($(this).attr('class').split(' ')[1] == 'lift') {		
			price = $(this).prev().prev().text();
			key = $(this).prev().prev().attr('id');
			action = "BUY";
		}
		
		if (price.length <= 0) {
			return;
		}
		$.ajax({
		        url: '/message/',
		        type: 'POST',
		        data:{
		            msg:"CONFIRM",
		            key:key,
		            action:action,
		            qty:qty,
		            price:price,
		            channelkey:channelkey
		        },
		        success: function(){},
		        complete:function(){}
		    });
	
		
	});
	


	
	$("#dialog").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		minHeight: 350,
		modal: true,
		buttons: {	"Offer": function() {
				$(this).dialog("close");
				//prep message
				var channelkey = $('meta[name=channel_token]').attr("content");
				var key = $(this).attr('ask_data');
				
				var action;
				var qty = $('#dialog_qty').val();
				var price = $('#dialog_price').val();
				
				
				/*add validations for data here
				 * before sending
				 */
	
			    $.ajax({
			        url: '/message/',
			        type: 'POST',
			        data:{
			            msg:"UPDATE",
			            key:key,
			            action:"SELL",
			            qty:qty,
			            price:price,
			            channelkey:channelkey
			        },
			        success: function(data){},
			        complete:function(){}
			    });
			},
			"Bid": function() {
					$(this).dialog("close");
					//prep message
					var channelkey = $('meta[name=channel_token]').attr("content");
					var key = $(this).attr('bid_data');
					
					var action;
					var qty = $('#dialog_qty').val();
					var price = $('#dialog_price').val();
					
					/*add validations for data here
					 * before sending
					 */

				    $.ajax({
				        url: '/message/',
				        type: 'POST',
				        data:{
				            msg:"UPDATE",
				            key:key,
				            action:"BUY",
				            qty:qty,
				            price:price,
				            channelkey:channelkey
				        },
				        success: function(data){},
				        complete:function(){}
				    });
			},
		
		},
		close: function() {}
	});
	
	
	$('.ui-icon-circle-plus, .ui-icon-circle-minus').click(function() {
		//qty
		if ($(this).parent().prev().attr('id') == "dialog_qty_container") {
			var val = $(this).parent().prev().children("input").val();
			
			if ($(this).hasClass('ui-icon-circle-plus')) {
				$(this).parent().prev().children("input").val( Math.max( (parseFloat(val) + 1), 0) );
			}
			else {
				$(this).parent().prev().children("input").val( Math.max((parseFloat(val) - 1), 0) );
			}
		}
		//price
		if ($(this).parent().prev().attr('id') == "dialog_price_container") {
			var val = $(this).parent().prev().children("input").val();
			if (val.length <= 0) {
				val = 0.000;
			}
			if ($(this).hasClass('ui-icon-circle-plus')) {
				$(this).parent().prev().children("input").val( Math.max((parseFloat(val) + .001).toFixed(3),0.000) );
			}
			else {
				$(this).parent().prev().children("input").val( Math.max((parseFloat(val) - .001).toFixed(3), 0.000) );
			}
		}
	});
	
	
	
	$('#orderbook_tag').click(function() {
		var height = $('#orderbook_container').css('height');
		if (height == "10px") {
			$('#orderbook_container').animate( { height: '600px'});
		}
		else {
			$('#orderbook_container').animate( { height: '10px'});
		}
		
		
	}); 
	
	
});


/*this is terrible and I apologize*/
function render(oldKeys, newKeys) {

	//update dates
	$('.tbl_contract.date').each(function (i, e) {
		console.log(dates_data[i]);
	});

	//iterate through old keys
	$.each(oldKeys, function (i, day) {

		//hourly
		$('.tbl_contract_row.hour.' + day).each(function (j, contract) {

			//time/bid/offer (columns for that hour)
			$(contract).children().each( function(k, e) {
				//time?

				//qty?
				
				//bid
				if($(e).hasClass('bid')) {
					$(e).attr('id', newKeys[i] + (2*j));
					
					
					$(e).html(contract_data[i][newKeys[i]][0][j]);
				}
				
				//offer
				if($(e).hasClass('ask')) {
					$(e).attr('id', newKeys[i] + (2*j+1));

					$(e).html(contract_data[i][newKeys[i]][1][j]);
					
				}
				//qty?
				
				//last?
			});
		});
		
		//update keys
		$('.tbl_contract_row.hour.' + day).attr('class', 'tbl_contract_row hour ' + newKeys[i]);
		$('.tbl_contract_date_row.' + day).attr('class', 'tbl_contract_date_row ' + newKeys[i]);
		$('#' + day).attr('id', newKeys[i]);
		
		$('.' + newKeys[i] +':even').addClass("odd")


	});

}


function refresh_data() {

	var data;
	var out = new Object();
	var oldk = Keys();
	
	out['region']= $('.selected.regions').attr('id');
	out['zone'] = $('.selected.zones').attr('id');
	out['os'] = $('.selected.os').attr('id');
	out['instance'] = $('select').val();
	$('body').css('cursor', 'wait');
	$.ajax({
	        url: '/main',
	        type: 'POST',
	        data:{
	        	msg:"REFRESH",
	        	data: out
	        },
	        success: function(data, textStatus){
	        	var obj = data;

	        	contract_data = obj["contract_data"];
	        	dates_data = obj["dates_data"];
	        	spotprice_data = obj["spotprice_data"];
	        },
	        complete:function(data, textStatus){
	        	var newk = Keys();
	        	$('.spot_price').text(spotprice_data[0]);
	        	render(oldk, newk);
	        	$('body').css('cursor', 'auto');
	        }
	    });
	

}

function Keys() {
	var k = new Array();
	
	for (i in contract_data) {

		for (v in contract_data[i]) {
			k.push(v);
		}
	}
	
	return k;
	
}