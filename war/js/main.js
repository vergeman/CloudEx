/*our js file for bindings and what not*/

var contract_data
var dates_data;

$(document).ready(function() {

	$('#instancetype').change(function() {
		update_data();
	});

	
	$('#selection li').click(function() {
		
		//update "selected"
		if(!$(this).hasClass("selected")) {
			var classes = $(this).attr('class').split(' ');
			var sel_class = classes[0];
			var sel_region = classes[1];
						
			$('.' + sel_class).removeClass("selected");
			$(this).addClass("selected");

			//if we changed regions, we need to update availability zones
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
			update_data();
		}
	});
	

	
	$("#dialog").dialog({
		autoOpen: false,
		height: 300,
		width: 350,
		minHeight: 350,
		modal: true,
		buttons: {
			"Submit": function() {
					$(this).dialog("close");
					
					//prep message
					var channelkey = $('meta[name=channel_token]').attr("content");
					var key = $(this).attr('data');
					
					var action = $('#dialog_action').val();
					var qty = $('#dialog_qty').val();
					var price = $('#dialog_price').val();
					
					
					/*add validations for data here
					 * before sending
					 */

				    $.ajax({
				        url: '/message/',
				        type: 'POST',
				        data:{
				            msg:"update",
				            key:key,
				            action:action,
				            qty:qty,
				            price:price,
				            channelkey:channelkey
				        },
				        success: function(data){},
				        complete:function(){}
				    });
			},
			Cancel: function() {
				$(this).dialog("close");
			}
		},
		close: function() {}
	});
	
});


function render(oldKeys, newKeys) {

	//update dates
	$('.tbl_contract.date').each(function (i, e) {
		console.log(dates_data[i]);
	});
	
	//iterate through old keys
	$.each(oldKeys, function (i, day) {

		$('.' + day).each(function (j, contract) {

			//time/bid/offer
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
		
		$('.' + day).attr('class', 'tbl_contract_row hour ' + newKeys[i]);
		
	});
}


function update_data() {

	var data;
	var out = new Object();
	var oldk = Keys();
	
	out['region']= $('.selected.regions').attr('id');
	out['zone'] = $('.selected.zones').attr('id');
	out['os'] = $('.selected.os').attr('id');
	out['instance'] = $('select').val();

	$.ajax({
	        url: '/main',
	        type: 'POST',
	        data:{
	        	msg:"update",
	        	data: out
	        },
	        success: function(data, textStatus){
	        	var obj = data;

	        	contract_data = obj["contract_data"];
	        	dates_data = obj["dates_data"];
	        },
	        complete:function(data, textStatus){
	        	var newk = Keys();
	        	render(oldk, newk);
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