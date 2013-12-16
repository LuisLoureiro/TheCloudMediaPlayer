var theCloudMediaPlayer = (function () {
	var currentPlaylist = undefined,
        modifiedCurrentPlaylist = undefined;
	function Content(idx, id, provider) {
		// Fields
		var idx_f = idx,
            id_f = id,
            provider_f = provider;
		// Getters and Setters
		function setIdx(arg) { idx_f = arg; }
		function setId(arg) { id_f = arg; }
		function setProvider(arg) { provider_f = arg; }
		function getIdx() { return idx_f; }
		function getId() { return id_f; }
		function getProvider() { return provider_f; }
        function equals(other) {
            if (!other || typeof other !== typeof this) { return false; }
            if (this.getIdx() === other.getIdx()
                    && this.getId() === other.getId()
                    && this.getProvider() === other.getProvider()) {
                return true;
            }
            return false;
        }
        
		this.getIdx = getIdx;
		this.getId = getId;
		this.getProvider = getProvider;
		this.setIdx = setIdx;
		this.setId = setId;
		this.setProvider = setProvider;
        this.equals = equals;
	}
	return {
		playlistFuncs : {
            saveDifferences : function () {
                theCloudMediaPlayer.currentPlaylist = theCloudMediaPlayer.modifiedCurrentPlaylist;
                theCloudMediaPlayer.modifiedCurrentPlaylist = undefined;
            },
			getDifferences : function (tableRows) {
				var diffs = {toAdd : [], toRemove : []},
                    currentPlaylist = theCloudMediaPlayer.currentPlaylist,
                    modifiedCurrentPlaylist = theCloudMediaPlayer.modifiedCurrentPlaylist = $.extend(true, {}, currentPlaylist);
                
                if (!currentPlaylist) {
                    tableRows.each(function (idx) {
                        var $pContent = $(this).find('a:first');
                        
						diffs.toAdd.push(new Content(idx, $pContent.attr('data-track-id'), $pContent.attr('data-provider-name')));
                    });
                } else {
                    // More adds or the same number of adds and removes.
                    if (tableRows.length >= currentPlaylist.contents.length) {
				        tableRows.each(function (idx) {
                            var $pContent = $(this).find('a:first'),
                                content = new Content(idx, $pContent.attr('data-track-id'), $pContent.attr('data-provider-name')),
                                cpContent = currentPlaylist.contents[idx],
                                currentPlaylistContent;
                            
                            if (!cpContent) { // This only occurs on the last elements of the table rows.
                                diffs.toAdd.push(content);
                                modifiedCurrentPlaylist.contents.push({id: content.getId(), provider: content.getProvider()});
                            } else {
                                currentPlaylistContent = new Content(idx, cpContent.id, cpContent.provider);
                                
                                if (!content.equals(currentPlaylistContent)) {
                                    diffs.toRemove.push(currentPlaylistContent);
                                    diffs.toAdd.push(content);
                                    modifiedCurrentPlaylist.contents[idx] = {id: content.getId(), provider: content.getProvider()};
                                }
                            }
                        });
                    // More removes.
                    } else {
                        $.each(currentPlaylist.contents, function (idx, cpContent) {
                            var $pContent = tableRows[idx],
                                content,
                                currentPlaylistContent = new Content(idx, cpContent.id, cpContent.provider);
                            
                            if (!$pContent) { // This only occurs on the last elements of the current playlist.
                                diffs.toRemove.push(currentPlaylistContent);
                                // Using currentPlaylist.contents[idx] because the modifiedCurrentPlaylist 
                                // is being updated and the indexes might not correspond.
                                modifiedCurrentPlaylist.contents.pop(currentPlaylist.contents[idx]);
                            } else {
                                $pContent = $($pContent).find('a:first');
                                content = new Content(idx, $pContent.attr('data-track-id'), $pContent.attr('data-provider-name'));
                                
                                if (!content.equals(currentPlaylistContent)) {
                                    diffs.toRemove.push(currentPlaylistContent);
                                    diffs.toAdd.push(content);
                                    modifiedCurrentPlaylist.contents[idx] = {id: content.getId(), provider: content.getProvider()};
                                }
                            }
                        });
                    }
                }
				
				return diffs;
			}
		},
        userFuncs: {
            deleteAccount: function () {
                $.ajax({
                    type: 'DELETE',
                    url: '/user',
                    success: function () {
                        window.location.assign('/');
                    },
                    error: defaultJsonErrorHandler
                });
            }
        }
	};
}());

function appendAlert(errorType, message) {
	$('#alerts').prepend('<div class="row-fluid"><div class="span12"><div class="alert alert-' + errorType + '"><a class="close">x</a>' + message + '</div></div></div>');
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
function slideUpAlert(elem) {
	// a.close <- div.alert.alert-X <- div.span# <- div.row-fluid
	$(elem).parent().parent().parent().slideUp('slow');
}
function defaultJsonErrorHandler(jqXHR, textStatus, errorThrown) {
	// Handle error
	try {
		appendErrorAlert($.parseJSON(jqXHR.responseText).error);
	} catch (err) {
		appendErrorAlert(errorThrown + ": " + jqXHR.responseText);
	}
	console.log(textStatus);
	console.log(errorThrown);
	console.log(jqXHR.responseText);
}
function setModalBoxContents(header, body) {
	$('#modalBox>.modal-header>h3').text(header);
	$('#modalBox>.modal-body').html(body);
}
function appendSongToPlayList(elem) {
	var name = $(elem).text(),
        trackId = $(elem).attr('data-track-id'),
        mimeType = $(elem).attr('data-track-mimetype'),
        providerName = $(elem).parent().prevAll().filter('.nav-header').first().text().toLowerCase();
	// TODO see if it's possible to just copy the anchor tag and add the class playlist-resource
	$('#playlist-table>tbody').append('<tr><td><a class="playlist-resource" data-track-id="' + trackId + '" data-provider-name="' + providerName + '" data-track-mimetype=' + mimeType + ' href="#">' + name + '</a><a href="#" class="remove pull-right">X</span></td></tr>');
}
function playContent(elem) {
	var trackId = $(elem).attr('data-track-id'),
        mimeType = $(elem).attr('data-track-mimetype'),
        providerName = $(elem).attr('data-provider-name');
	$.ajax({
		type: 'GET',
		url: '/track/streamurl/' + encodeURIComponent(trackId),
		dataType: 'json',
		data: {
			'providerName': providerName
		},
		success: function (trackUrl) {
			console.log(trackUrl);
			console.log(mimeType);
			$('#playing').html((mimeType.indexOf('audio') !== -1) ? '<audio autoplay="autoplay" controls="controls"><source src="' + trackUrl.url + '"></source></audio>'
					: '<video autoplay="autoplay" controls="controls"><source src="' + trackUrl.url + '" type="' + mimeType + '"></source></video>');
		},
		error: defaultJsonErrorHandler
	});
}
function newPlaylist() {
	// Check the state of the current play list and if it's modified confirm if he wants to forget the changes
	//  or if he wants to save the play list before creating a new one
	
	// TODO replace with implementation of previous comment.
	setModalBoxContents(I18nJS["playlist.create.modalBox.title"],
            '<p>' + I18nJS["playlist.create.modalBox.warning"] + '</p><p>' + I18nJS["playlist.create.modalBox.confirmation"]
			+ '</p><form id="playlist-newForm" class="form-horizontal">'
			+ '<div class="control-group"><div class="controls"><button type="submit" class="btn btn-primary">'
            + I18nJS.Yes + '</button></div></div></form>'
			);
	$('#playlist-newForm').submit(function (e) {
		e.preventDefault();
        // Clean play list contents
        $('#playlist-clean').click();
        $('#playlist-name').html('default <b class="caret"></b>').attr('data-playlist-id', '');
        $('#modalBox').modal('hide');
        theCloudMediaPlayer.currentPlaylist = undefined;
	});
	$('#modalBox').modal('show');
}
function serializePlaylistContents(tableRows) {
	var serializedString = '',
        differences = theCloudMediaPlayer.playlistFuncs.getDifferences(tableRows);
	$.each(differences.toRemove, function (idx, elem) {
		serializedString += '&contentsToRemove[' + idx + '].idx=' + elem.getIdx() + '&contentsToRemove[' + idx + '].id=' + encodeURIComponent(elem.getId()) + '&contentsToRemove[' + idx + '].provider=' + encodeURIComponent(elem.getProvider());
	});
	$.each(differences.toAdd, function (idx, elem) {
		serializedString += '&contentsToAdd[' + idx + '].idx=' + elem.getIdx() + '&contentsToAdd[' + idx + '].id=' + encodeURIComponent(elem.getId()) + '&contentsToAdd[' + idx + '].provider=' + encodeURIComponent(elem.getProvider());
	});
	return serializedString;
}
function savePlaylist() {
	// Empty play lists aren't allowed to be saved..
	var rows = $('#playlist-table>tbody>tr'),
        func,
        elem,
        id,
        serializedPlaylistContents;
	if (rows.length === 0) {
		appendErrorAlert(I18nJS["playlist.save.errorEmptyPlaylist"]);
		return;
	}
	func = function (update, contentsData) {
		$.ajax({
			type: 'PUT',
		    url: '/playlist',
		    data: contentsData,
		    dataType: 'json',
		    success: function (data) {
                if (!update) {
                    // Register the play list id in a tag attribute!
                    $('#playlist-name').html(data.name + ' <b class="caret"></b>').attr('data-playlist-id', data.id);
                    // Add list to load play lists.
                    $('#playlist-load').parent().parent()
                        .append('<li><a class="playlist-load-item" href="#" data-playlist-id="' + data.id + '">' + data.name + '</a></li>');
                    // Hide the 'empty' item
                    $('.playlist-load-empty').hide();
                }
                // update current playlist data.
                theCloudMediaPlayer.playlistFuncs.saveDifferences();
                appendSuccessAlert(data.message);
		    },
            error: defaultJsonErrorHandler,
            complete: function (jqXHR, textStatus) {
                $('#modalBox').modal('hide');
			}
		});
	};
	elem = $('#playlist-name');
	id = elem.attr('data-playlist-id');
	// Ask for the name of the new play list if the present one is the default play list.
	if (!id || id === "0") {
		setModalBoxContents(I18nJS["playlist.save.modalBox.title"],
				'<form id="playlist-saveForm" class="form-horizontal"><fieldset><legend>' + I18nJS["playlist.save.modalBox.legend"]
				+ '</legend><div class="control-group"><label class="control-label">' + I18nJS.Name
				+ '</label><div class="controls"><input class="span3" type="text" name="name" placeholder="'
                + I18nJS["playlist.save.modalBox.placeholder"] + '" required="required"></input><span class="help-inline">'
				+ I18nJS["playlist.save.modalBox.helpInline"]
				+ '</span></div></div><div class="control-group"><div class="controls"><button type="submit" class="btn btn-primary">'
                + I18nJS.Save + '</button></div></div></fieldset></form>');
		$('#playlist-saveForm').submit(function (e) {
			e.preventDefault();
			func(false, $(this).serialize() + serializePlaylistContents(rows));
		});
		$('#modalBox').modal('show');
	} else {
		serializedPlaylistContents = serializePlaylistContents(rows);
		if (serializedPlaylistContents.length !== 0) {
			func(true, 'name=' + elem.text().trim() + '&id=' + id + serializedPlaylistContents);
        } else {
            appendErrorAlert(I18nJS["playlist.create.errorNoChanges"]);
        }
	}
}
function cleanPlaylist() {
	$('#playlist-table tbody').empty();
}
function removeTrack(elem) {
	$(elem).remove();
}
function deletePlaylist() {
	var elem = $('#playlist-name'),
        id = elem.attr('data-playlist-id'),
        func = function (contentsData) {
            $.ajax({
                type: 'DELETE',
                url: '/playlist/' + contentsData,
                dataType: 'json',
                success: function (data) {
                    // Clean play list contents
                    $('#playlist-clean').click();
                    elem.html('default <b class="caret"></b>').attr('data-playlist-id', '');
                    // Remove it from the list of play lists to load.
                    $('[data-playlist-id=' + id + ']').not('#playlist-name').parent().remove();
                    // Show the 'empty' item if "none" is available!
                    if ($('#playlist-load').parent().nextAll(':not(.playlist-loading,.playlist-load-empty)').length === 0) {
                        $('.playlist-load-empty').show();
                    }
                    theCloudMediaPlayer.currentPlaylist = undefined;
                    appendSuccessAlert(data.message);
                },
                error: defaultJsonErrorHandler,
                complete: function (jqXHR, textStatus) {
                    $('#modalBox').modal('hide');
                }
            });
        };
	// Only enable the delete action if the current play list exists (it's not the default one), ie. it has an id attribute.
	if (id) {
		// Show a confirmation window before Ajax call
		setModalBoxContents(I18nJS["playlist.delete.modalBox.title"],
				'<p>' + I18nJS["playlist.delete.modalBox.confirmationWithoutQuestionMark"] + elem.text().trim()
				+ '"?</p><p>' + I18nJS["playlist.delete.modalBox.warning"]
				+ '</p><form id="playlist-deleteForm" class="form-horizontal">'
				+ '<input type="hidden" name="id" value="' + id + '"></input>'
				+ '<div class="form-actions"><button type="submit" class="btn btn-primary">' + I18nJS.Confirm
				+ '</button></div></form>');
		$('#playlist-deleteForm').submit(function (e) {
			e.preventDefault();
			func(id);
		});
	} else {
		setModalBoxContents(I18nJS.Error, '<p>' + I18nJS["playlist.delete.errorDefaultPlaylist"] + '</p>');
	}
	$('#modalBox').modal('show');
}
function loadPlaylists(ulElem) {
	// Show the 'loading' item.
	var loading = $('.playlist-loading');
	loading.show();
	$.ajax({
		type: 'GET',
	    url: '/playlist',
	    dataType: 'json',
	    success: function (data) {
            if (data.playlists && data.playlists.length !== 0) {
                // Clean all the items after the first before adding the new items.
                $('.playlist-load-empty').hide();
                $(ulElem).children().not(':first,.playlist-loading,.playlist-load-empty').remove();
                $.each(data.playlists, function (idx, playlist) {
                    $(ulElem).append('<li><a class="playlist-load-item" href="#" data-playlist-id="' + playlist.id + '">' + playlist.title + '</a></li>');
                });
            }
        },
        error: defaultJsonErrorHandler,
        complete: function () {
            loading.hide();
        }
    });
}
function appendLoadedContentsToPlayList(playlistBody, contents) {
	var notConnectedProviders = [];
	$.each(contents, function (idx, elem) {
		// find the element in the user contents.
		// Don't show if not present, i.e. user is not connected to Soundcloud and playlist has soundcloud tracks.
		var htmlStringArray = $('#resources-list').find('li.nav-header').filter(
				function () {
                    return $(this).text().toLowerCase() === elem.provider;
                }
            ).nextUntil('.nav-header').find('[data-track-id="' + elem.id + '"]').each(
                function () {
                    var $this = $(this);
                    playlistBody.append('<tr><td><a class="playlist-resource" data-track-id="' + $this.attr('data-track-id') + '" data-provider-name="' + elem.provider + '" data-track-mimetype="' + $this.attr('data-track-mimetype') + '" href="#">' + $this.text() + '</a><a href="#" class="remove pull-right">X</a></td></tr>');
                }
            );
		// Show a warning message to the user.
		if (htmlStringArray.length === 0) {
			notConnectedProviders.push(elem.provider);
		}
	});
	if (notConnectedProviders.length !== 0) {
		// TODO Show the contents associating the provided.
		// "Ensure that these contents are still present in their services or if they were not removed or renamed."
		// Show a Modal window!
		return I18nJS["playlist.load.infoReconnectAndRefresh"].format(notConnectedProviders.join(', '));
	}
}
function loadPlaylist(elem) {
	// Get the id of the selected play list.
	var id = $(elem).attr('data-playlist-id');
	if (id && id !== '') {
		$.ajax({
			type: 'GET',
			url: '/playlist/' + id,
			dataType: 'json',
			success: function (data) {
				// Clean all the items of the current play list
				// Update the play list name.
				$('#playlist-clean').click();
                $('#playlist-name').html(data.title + ' <b class="caret"></b>').attr('data-playlist-id', data.id);
                theCloudMediaPlayer.currentPlaylist = data;
                // Place all the contents in the list.
                var notConnectedProvidersInfoMessage = appendLoadedContentsToPlayList($('#playlist-table>tbody'), data.contents);
                if (notConnectedProvidersInfoMessage) {
                    appendInfoAlert(notConnectedProvidersInfoMessage);
                } else {
					// Show a success message.
                    appendSuccessAlert(data.message);
                }
			},
			error: defaultJsonErrorHandler
		});
	} else {
        appendErrorAlert(I18nJS["playlist.load.errorSelectedPlaylistHasNoIdAttribute"]);
    }
}
$(document).ready(function () {
	$(document).on("click", 'a.close', function () {slideUpAlert(this); });
	// Append song to play list.
	$(document).on("click", "#resources-list>ul>li>a", function () {appendSongToPlayList(this); });
	// Play resource
	$(document).on("click", ".playlist-resource", function () {playContent(this); });
	// New empty play list
	$('#playlist-new').click(function () {newPlaylist(); });
	// Save play list
	$('#playlist-save').click(function () {savePlaylist(); });
	// Removing all the contents from the current play list.
	$('#playlist-clean').click(function () {cleanPlaylist(); });
	// Remove the selected resource from the current play list.
	$(document).on("click", "td > a.playlist-resource + a.remove", function () {removeTrack($(this).parent().parent()); });
	// Delete the current play list.
	$('#playlist-delete').click(function () {deletePlaylist(); });
	// Get the list of user's play lists.
	$('#playlist-load').click(function (e) {
		e.preventDefault(); // prevent url change with href.
		e.stopPropagation(); // to maintain the visibility of the menu.
		loadPlaylists($(this).parent().parent());
    });
	$(document).on("click", ".playlist-load-item", function (e) {loadPlaylist(this); });
    $('#deleteAccount').click(function (e) {
		e.stopPropagation(); // to maintain the visibility of the menu.
        theCloudMediaPlayer.userFuncs.deleteAccount();
    });
	
	// Modal box events
//	$('#modalBox').on('hidden', function(){});
    $('#modalBox').on('shown', function () {$(this).find('input').focus(); });
});