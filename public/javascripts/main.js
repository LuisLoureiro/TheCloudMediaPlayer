function appendAlert(errorType, message) {
	$('body>.container-fluid').prepend('<div class="row-fluid"><div class="span12"><div class="alert alert-'+errorType+'">'+message+'</div></div></div>');
}
function appendErrorAlert(message) {
	funcAlert("error", message);
}
function appendInfoAlert(message) {
	funcAlert("info", message);
}
function appendSuccessAlert(message) {
	funcAlert("success", message);
}