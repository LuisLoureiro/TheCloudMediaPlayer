function appendAlert(errorType, message) {
	$('body>.container-fluid').prepend('<div class="row-fluid"><div class="span12"><div class="alert alert-'+errorType+'"><a class="close">x</a>'+message+'</div></div></div>');
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
$(document).ready(function(){
	$('a.close').click(function(){
		// a.close <- div.alert.alert-X <- div.span# <- div.row-fluid
		$(this).parent().parent().parent().slideUp('slow');
	});
});