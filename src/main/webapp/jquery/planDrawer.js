$(function() {

	var retrievalPlanId = null;
	
	drawPlan = function(planId) {
		retrievalPlanId = planId;
		
		$("#pushersBox").empty();
		$.ajax({
			dataType : "json",
			url : "progress?planId="+planId,
//			url : "progress",
			success : function(data) {
//				console.log(data);
				drawPushers(data.pulls);
			}
		});
	};

	refreshPlan = function(planId) {
		$.ajax({
			dataType : "json",
			url : "progress?planId="+planId,
//			url : "progress",
			success : function(data) {
//				console.log(data);
				refreshPushers(data.pulls);
			}
		});
	};
	
	window.setInterval(function() {
		refreshPlan(retrievalPlanId);
	}, 2000);


	drawPushers = function(pushers) {
		$("#pushersBox").empty();
		$("#pushersBox").html('<table class="table"><thead><tr><td>pusher</td><td>from</td><td>to</td><td>current</td><td>bandWidth</td><td>progress</td></tr></thead><tbody></tbody></table>');
		
		$.each(pushers, function(id, pusher) {
			drawPusher(pusher);
		});
	};

	refreshPushers = function(pushers) {
		$.each(pushers, function(id, pusher) {
			refreshPusher(pusher);
		});
	};

	refreshPusher = function(pusher) {
		// console.log($('#'+pusher.byteFrom+'-'+pusher.byteTo+' .current'));
		
		if(jQuery($('#'+pusher.byteFrom+'-'+pusher.byteTo+' .current')).length > 0){

			$('#'+pusher.byteFrom+'-'+pusher.byteTo+' .current').html(pusher.byteCurrent);
			$('#'+pusher.byteFrom+'-'+pusher.byteTo+' .bandwidth').html(parseFloat(pusher.bandWidth).toFixed(2));
			$('#'+pusher.byteFrom+'-'+pusher.byteTo+' .progress').html(pusher.progress+"%");

		} else {
			
			drawPusher(pusher);
		
		}
		
		
	};

	drawPusher = function(pusher) {
		$("#pushersBox tbody").append(
				'<tr id="'+pusher.byteFrom+'-'+pusher.byteTo+'"><td>'+pusher.ip+':'+pusher.port+'</td><td>'+pusher.byteFrom+'</td><td>'+pusher.byteTo+'</td><td class="current">'+pusher.byteCurrent+'</td><td class="bandwidth">'+parseFloat(pusher.bandWidth).toFixed(2)+'</td><td class="progress">'+pusher.progress+'%'+'</td></tr>');
	};

	$.bandWidth = function(pusher) {
		return '<span class="bandwidth">bw: ' + pusher.bandWidth + '</span>';
	};
	
	
	$.progressLeyend = function(id, progress) {
		return '<span class="'+id+' bold">' + $.progressText(progress)
				+ '</span>';
	};

	$.progressText = function(progress) {
		return progress + '%';
	};

	$.progress = function(p, id, extraClass) {
		return '<div class="progress" id="' + id
				+ '"><div class="progress-bar ' + extraClass
				+ '" role="progressbar" aria-valuenow="' + p
				+ '" aria-valuemin="0" aria-valuemax="100" style="width: ' + p
				+ '%;"><span class="sr-only">' + p
				+ '% Complete</span></div></div>';
	};
});
