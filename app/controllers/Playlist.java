package controllers;

import static play.data.Form.form;

import java.util.List;

import models.beans.dataBinding.form.PlaylistForm;

import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import controllers.enums.SESSION;
import controllers.operations.persistence.PersistPlaylist;

@Authenticated
public class Playlist extends Controller
{
	@Transactional
	public static Result save() throws Exception
	{
	    // Return a json object with the result of the operation.
		ObjectNode result = Json.newObject();
		
		// Grab the play list name from the request body.
	    // Using Form with Modal class. See: http://www.playframework.com/documentation/2.1.1/JavaForms
	    // ... or using dynamic form:
//	    DynamicForm requestData = form().bindFromRequest();
//	    String playListName = requestData.get("name");
	    Form<PlaylistForm> playlistForm = form(PlaylistForm.class).bindFromRequest();
	    if(playlistForm.hasErrors())
	    {
	    	result.put("error", buildMessageFromValidationErrors(playlistForm, ctx().lang()));
	    	return badRequest(result);
	    }
	    PlaylistForm playlist = playlistForm.get();
	    
	    long id = PersistPlaylist.savePlaylist(session(SESSION.USERNAME.toString()), playlist.getId(), playlist.getName(), ctx().lang());
	    
		result.put("name", playlist.getName());
		result.put("id", id);
		result.put("message", Messages.get(playlist.getId() == 0 ? "user.playList.save.createdSuccessfully" : "user.playList.save.updatedSuccessfully"));
		return created(result);
	}
	
	@Transactional(readOnly=true)
	public static Result load(int id) throws Exception
	{
		// Check the value of the id parameter to execute the correct operation:
		// * if == 0, return the user play lists: name & id
		// * if != 0, return the list of contents related to the user play list: name & contents (id & title & provider)
		
	    // Return a json object with the result of the operation.
		ObjectNode result = Json.newObject();
		
		if(id == 0)
		{
			List<models.beans.dataBinding.Playlist> playLists = PersistPlaylist.loadPlaylists(session(SESSION.USERNAME.toString()));
			result.put("playlists", Json.toJson(playLists));
		}
		else
		{
			models.beans.dataBinding.Playlist playList = PersistPlaylist.loadPlaylist(session(SESSION.USERNAME.toString()), id, ctx().lang());
			result.put("id", playList.getId());
			result.put("title", playList.getTitle());
			result.put("contents", Json.toJson(playList.getTracks()));
			result.put("message", Messages.get("user.playList.load.successMessage", playList.getTitle()));
		}
	    
		return ok(result);
	}
	
	@Transactional
	public static Result delete(int id) throws Exception
	{
	    // Return a json object with the result of the operation.
		ObjectNode result = Json.newObject();
		
		String name = PersistPlaylist.deletePlaylist(session(SESSION.USERNAME.toString()), id, ctx().lang());

		result.put("message", Messages.get("user.playList.delete.successMessage", name));
		return ok(result);
	}
	
	// TODO move to an Utils class!
	private static <T> String buildMessageFromValidationErrors(Form<T> form, Lang lang)
	{
		StringBuilder errorString = new StringBuilder();
    	for(List<ValidationError> errors : form.errors().values())
    	{
    		for(ValidationError error : errors)
    		{
    			if(errorString.length() > 0)
    				errorString.append(", ");
    			errorString.append(Messages.get(lang, error.message()));
    		}
    	}
    	return errorString.toString();
	}
}
