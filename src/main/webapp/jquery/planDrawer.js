$(function() {

	var retrievalPlanId = null;
	
	drawPlan = function(planId) {
//		retrievalPlanId = planId;
		
		$("#pullerBox").empty();
		$("#pushersBox").empty();
		$.ajax({
			dataType : "json",
//			url : "progress?planId="+planId,
			url : "progress",
			// data: data,
			success : function(data) {
				console.log(data);
//				drawPuller(data.puller);
				drawPushers(data.pulls);
			}
		});
	};

	refreshPlan = function(planId) {
		$.ajax({
			dataType : "json",
//			url : "progress?planId="+planId,
			url : "progress",
			// data: data,
			success : function(data) {
				console.log(data);
//				refreshPuller(data.puller);
				refreshPushers(data.pulls);
			}
		});
	};
	
	window.setInterval(function() {
		refreshPlan(retrievalPlanId);
	}, 2000);

	drawPuller = function(puller) {
		progress = $.progress(puller.progress, "pullerProgressBar",
				"progress-bar-success");
		$("#pullerBox").empty();
		$("#pullerBox").append(
				'<div id="pullerBoxInner">puller '
						+ puller.ip
						+ ":"
						+ puller.port
						+ '<br />'
						+ $.progressLeyend("puller-" + puller.clientId,
								puller.progress) + progress + '</div>');

	};

	refreshPuller = function(puller) {
		p = puller.progress;
		$("span#puller-" + puller.clientId).html($.progressText(puller.progress));
		$('#pullerProgressBar .progress-bar').attr('aria-valuenow', p);
		$('#pullerProgressBar .progress-bar').attr('style',
				'width: ' + p + '%;');
		$('#pullerProgressBar .progress-bar span.sr-only').html(
				p + '% Complete');
	};

	drawPushers = function(pushers) {
		$("#pushersBox").empty();
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
		p = pusher.progress;
		console.log(pusher.progress);
		console.log($('.progress#pusher-' + pusher.clientId + ' .progress-bar'));
		$("span.pusher-" + pusher.clientId).html($.progressText(pusher.progress));
		$('.progress#pusher-' + pusher.clientId + ' .progress-bar').attr('aria-valuenow', p);
		$('.progress#pusher-' + pusher.clientId + ' .progress-bar').attr('style', 'width: ' + p + '%;');
		$('.progress#pusher-' + pusher.clientId + ' .progress-bar span.sr-only').html(p + '% Complete');

	};

	drawPusher = function(pusher) {
		progress = $.progress(pusher.progress, "pusher-"+pusher.clientId);
		return $("#pushersBox").append(
				'<div id="pusher-'
						+ pusher.clientId
						+ '" class="pusherBox">'
						+ pusher.ip
						+ ":"
						+ pusher.port
						+ '<br />'
						+ $.progressLeyend("pusher-" + pusher.clientId,
								pusher.progress) + ' - ' + $.bandWidth(pusher) + progress +  '</div>');
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