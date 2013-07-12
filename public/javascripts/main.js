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
function savePlaylist(){
	var func = function(contentsData){
		$.ajax({
			type: 'PUT',
		    url: '/playlist',
		    data: contentsData,
		    dataType: 'json',
		    success: function(data){
		    	$('#playlist-name').html(data.name+' <b class="caret"></b>');
		    	// Register the play list id in a tag attribute!
		    	$('#playlist-name').attr('data-playlist-id', data.id);
		    	// Add list to load play lists.
		    	$('#playlist-load').parent().parent()
		    		.append('<li><a class="playlist-load-item" href="#" data-playlist-id="'+
		    				data.id+'">'+data.name+'</a></li>');
		    	// Hide the 'empty' item
		    	$('.playlist-load-empty').parent().hide();
		    	appendSuccessAlert(data.successMessage);
		    },
		  	error: defaultJsonErrorHandler,
			complete: function(jqXHR, textStatus){
		    	$('#modalBox').modal('hide');
			}
		});
	}
	var id = $('#playlist-name').attr('data-playlist-id');
	var name = $('#playlist-name').text().trim();
	// Ask for the name of the new play list if the present one is the default play list.
	if(!id || id == "0"){//"default" == name){
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
		func('name='+name+'&id='+id);
	}
}
function cleanPlaylist(){
	$('#playlist-table tbody').empty();
}
function removeTrack(elem){
	$(elem).remove();
}
function deletePlaylist(elem){
	var name = $('#playlist-name').text().trim();
	var id = $('#playlist-name').attr('data-playlist-id');
	var func = function(contentsData){
		$.ajax({
			type: 'DELETE',
		    url: '/playlist/'+contentsData,
		    dataType: 'json',
		    success: function(){
		    	// Clean play list contents
		    	$('#playlist-clean').click();
		    	$('#playlist-name').html('default <b class="caret"></b>');
		    	$('#playlist-name').attr('data-playlist-id', '');
		    	// Remove from the list of play lists to load.
		    	$('[data-playlist-id='+id+']').parent().remove();
		    	// Show the 'empty' item if none is available!
		    	if($('#playlist-load').parent().nextAll().length == 0) // TODO if necessary add a selector to the nextAll to avoid the hidden empty item!
		    		$('.playlist-load-empty').parent().show();
		    	
		    	appendSuccessAlert("The play list was successfully deleted.");
		    },
		  	error: defaultJsonErrorHandler,
			complete: function(jqXHR, textStatus){
		    	$('#modalBox').modal('hide');
			}
		});
	};
	// Only enable the delete action if the current play list exists, ie. it has an id attribute.
	if(id){
		// Show a confirmation window before Ajax call
		setModalBoxContents("Confirmação da eliminação da lista de reprodução",
				'<p>Tem a certeza que deseja eliminar permanentemente a lista de reprodução "'+name+'"?</p>'+
				'<p>Esta eliminação não poderá ser revogada mais tarde!</p>'+
				'<form id="playlist-deleteForm" class="form-horizontal">'+
				'<input type="hidden" name="id" value="'+id+'"></input>'+
				'<div class="form-actions"><button type="submit" class="btn btn-primary">Confirm</button></div>'+
				'</form>');
		$('#playlist-deleteForm').submit(function(e){
			e.preventDefault();
			func(id);
		});
	} else{
		setModalBoxContents("Erro", '<p>The default play list cannot be deleted!</p>');
	}
	$('#modalBox').modal('show');
}
function loadPlaylists(ulElem){
	// Add a new item saying 'loading'.
	$(ulElem).children().first().after('<li class="loading"><p>Loading...</p></li>');
	$.ajax({
		type: 'GET',
	    url: '/playlist',
	    dataType: 'json',
	    success: function(data){
	    	if(data.playlists && data.playlists.length != 0){
		    	// Clean all the items after the first before adding the new items.
		    	$(ulElem).children().not(':first').remove();
		    	for(var playlist in data.playlists){
		    		$(ulElem).append('<li><a class="playlist-load-item" href="#" data-playlist-id="'+
		    				playlist.id+'">'+playlist.name+'</a></li>');
		    	}
	    	}
	    },
	  	error: defaultJsonErrorHandler,
	  	complete: function(){
	  		$(ulElem).children().filter('.loading').remove();
	  	}
	});
}
function defaultJsonErrorHandler(jqXHR, textStatus, errorThrown){
	// Handle error
	try{
		appendErrorAlert($.parseJSON(jqXHR.responseText).error);
	}catch(err){
		appendErrorAlert(errorThrown + ": " + jqXHR.responseText);
	}
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
	$('#playlist-save').click(function(){savePlaylist();});
	// Removing all the contents from the current play list.
	$('#playlist-clean').click(function(){cleanPlaylist();});
	// Remove the selected resource from the current play list.
	$(document).on("click", "td > a.playlist-resource + a.remove", function(){removeTrack($(this).parent());});
	// Delete the current play list.
	$('#playlist-delete').click(function(){deletePlaylist(this);});
	// Get the list of user's play lists.
	$('#playlist-load').click(function(e){
		e.preventDefault(); // prevent url change with href.
		e.stopPropagation(); // to maintain visible the menu.
		loadPlaylists($(this).parent().parent());});
	
	// Modal box events
//	$('#modalBox').on('hidden', function(){});
    $('#modalBox').on('shown', function(){$(this).find('input').focus();});
});