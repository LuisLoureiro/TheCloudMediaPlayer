function appendAlert(errorType, message) {
	$('#alerts').prepend('<div class="row-fluid"><div class="span12"><div class="alert alert-'+errorType+'"><a class="close">x</a>'+message+'</div></div></div>');
}
function appendErrorAlert(message) {
	appendAlert("error", message);
}
function appendInfoAlert(message) {
	appendAlert("info", message);
}
function appendSuccessAlert(message) {
	appendAlert("success", message);
}
function emptyAlerts() {
	$('#alerts').empty();
}
$(document).ready(function(){
	$(document).on("click", 'a.close', function(){
		// a.close <- div.alert.alert-X <- div.span# <- div.row-fluid
		$(this).parent().parent().parent().slideUp('slow');
	});
	// Append song to play list.
	// TODO see if it's possible to just copy the anchor tag and add the class playlist-resource
	$(document).on("click", "#resources-list>ul>li>a", function(){
		var name=$(this).text();
		var trackId=$(this).attr('data-track-id');
		var mimeType=$(this).attr('data-track-mimetype');
		var providerName=$(this).parent().prevAll().filter('.nav-header').first().text().toLowerCase();
		$('#playlist-table>tbody').append('<tr><td><a class="playlist-resource" data-track-id="'+trackId+'" data-provider-name="'+providerName
				+'" data-track-mimetype='+mimeType+' href="#">'+name+'</a></td></tr>');
	});
	// Play resource
	$(document).on("click", ".playlist-resource", function(){
		var trackId=$(this).attr('data-track-id');
		var mimeType=$(this).attr('data-track-mimetype');
		var providerName=$(this).attr('data-provider-name');
		$.ajax({
			type: 'GET',
			url: '/track/streamurl/'+encodeURIComponent(trackId),
			dataType: 'json',
			data: {
				'providerName': providerName
			},
			success: function(trackUrl) {
				console.log(trackUrl);
				console.log(mimeType);
				$('#playing').html((mimeType.indexOf('audio') != -1) ? '<audio autoplay="autoplay" controls="controls"><source src="'+trackUrl.url+'"></source></audio>'
						: '<video autoplay="autoplay" controls="controls"><source src="'+trackUrl.url+'" type="'+mimeType+'"></source></video>');
			},
			error: function(jqXHR, textStatus, errorThrown) {
				// Handle error
				appendErrorAlert(errorThrown);
				console.log(textStatus);
				console.log(jqXHR.responseText);
			}
		});
	});
});