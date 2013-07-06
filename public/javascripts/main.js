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
function slideUpAlert(elem){
	// a.close <- div.alert.alert-X <- div.span# <- div.row-fluid
	$(elem).parent().parent().parent().slideUp('slow');
}
function appendSongToPlayList(elem){
	var name=$(elem).text();
	var trackId=$(elem).attr('data-track-id');
	var mimeType=$(elem).attr('data-track-mimetype');
	var providerName=$(elem).parent().prevAll().filter('.nav-header').first().text().toLowerCase();
	// TODO see if it's possible to just copy the anchor tag and add the class playlist-resource
	$('#playlist-table>tbody').append('<tr><td><a class="playlist-resource" data-track-id="'+trackId+'" data-provider-name="'+providerName
			+'" data-track-mimetype='+mimeType+' href="#">'+name+'</a><a href="#" class="remove pull-right">X</span></td></tr>');
}
function playContent(elem){
	var trackId=$(elem).attr('data-track-id');
	var mimeType=$(elem).attr('data-track-mimetype');
	var providerName=$(elem).attr('data-provider-name');
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
		error: defaultJsonErrorHandler
	});
}
function savePlaylist(elem){
	var func = function(contentsData){
		$.ajax({
			type: 'PUT',
		    url: '/playlist',
		    data: contentsData,
		    dataType: 'json',
		    success: function(data){
		    	$('#playlist-name').text(data.name+" ");
		    	appendSuccessAlert("The play list was successfully saved.");
		    },
		  	error: defaultJsonErrorHandler,
			complete: function(jqXHR, textStatus){
		    	$('#modalBox').modal('hide');
			}
		});
	}
	var name = $('#playlist-name').text().trim();
	// Ask for the name of the new play list if the present one is the default play list.
	if("default" == name){
		setModalBoxContents("Guardar lista de reprodução", '<form id="playlist-saveForm" class="form-horizontal"><fieldset><legend>Indique o nome da nova lista</legend>'+
				'<div class="control-group"><label class="control-label">Nome</label>'+
				'<div class="controls"><input class="span3" type="text" name="name" placeholder="Ex: First playlist, Best tracks, ..." required="required"></input>'+
				'<span class="help-inline">O nome tem que ser preenchido!</span></div></div>'+
				'<div class="control-group"><div class="controls"><button class="btn btn-primary">Save</button></div></div></fieldset></form>');
		$('#playlist-saveForm').submit(function(e){
			e.preventDefault();
			func($(this).serialize());
		});
		$('#modalBox').modal('show');
	} else{
		func('name='+name);
	}
}
function cleanPlaylist(){
	$('#playlist-table tbody').empty();
}
function removeTrack(elem){
	$(elem).remove();
}
function defaultJsonErrorHandler(jqXHR, textStatus, errorThrown){
	// Handle error
	appendErrorAlert($.parseJSON(jqXHR.responseText).error);
	console.log(textStatus);
	console.log(errorThrown);
	console.log(jqXHR.responseText);
}
function setModalBoxContents(header, body){
	$('#modalBox>.modal-header>h3').text(header);
	$('#modalBox>.modal-body').html(body);
}
$(document).ready(function(){
	$(document).on("click", 'a.close', function(){slideUpAlert(this);});
	// Append song to play list.
	$(document).on("click", "#resources-list>ul>li>a", function(){appendSongToPlayList(this);});
	// Play resource
	$(document).on("click", ".playlist-resource", function(){playContent(this);});
	// Save play list
	$('#playlist-save').click(function(){savePlaylist(this);});
	// Removing all the contents from the current play list.
	$('#playlist-clean').click(function(){cleanPlaylist();});
	// Remove the selected resource from the current play list.
	$(document).on("click", "td > a.playlist-resource + a.remove", function(){removeTrack($(this).parent());});
	
	// Modal box events
//	$('#modalBox').on('hidden', function(){});
    $('#modalBox').on('shown', function(){$(this).find('input').focus();});
});