/*our js file for bindings and what not*/

var contract_data
var dates_data;

$(document).ready(function() {

	$('select').change(function() {
		update_data();
	});

	
	$('#view li').click(function() {
		
		//update "selected"
		if(!$(this).hasClass("selected")) {
		
			$('.' + $(this).attr('class')).removeClass("selected");
			$(this).addClass("selected");

			// make post with block in message
			update_data();
		}
	});
	
	
	
	
});


function render(oldKeys, newKeys) {

	//update dates
	$('.tbl_contract.date').each(function (i, e) {
		console.log(dates_data[i]);
	});
	

	//update values

	//$('.' + e).attr('class', 'tbl_contract_row hour ' + newKeys[i]);
	console.log(contract_data);
	//iterate through old keys
	$.each(oldKeys, function (i, day) {

		//j - 23
		$('.' + day).each(function (j, contract) {

			//time/bid/offer
			$(contract).children().each( function(k, e) {
				//time
				//console.log(contract_data[i]);
				
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
	        	var obj = $.parseJSON(data);

	        	contract_data = obj['contract_data'];
	        	dates_data = obj['dates_data'];
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