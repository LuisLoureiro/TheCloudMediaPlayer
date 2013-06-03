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
	$(document).on("click", "#resources-list>ul>li>a", function(){
		var name=$(this).text(); $('#playlist-table>tbody').append('<tr><td><a href="#">'+name+'</a></td></tr>');
	})
});