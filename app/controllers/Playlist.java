package controllers;

import static play.data.Form.form;

import java.util.List;

import models.beans.dataBinding.form.PlaylistForm;

import org.codehaus.jackson.node.ObjectNode;

import controllers.enums.SESSION;
import controllers.operations.persistence.PersistPlaylist;

import play.data.Form;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated
public class Playlist extends Controller
{
	@Transactional
	public static Result save()
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
	    	result.put("error", buildMessageFromValidationErrors(playlistForm));
	    	return badRequest(result);
	    }
	    PlaylistForm playlist = playlistForm.get();
	    
	    int id = PersistPlaylist.savePlaylist(session(SESSION.USERNAME.toString()), playlist.getName());
	    
		result.put("name", playlist.getName());
		result.put("id", id);
		return created(result);
	}
	
	@Transactional(readOnly=true)
	public static Result load(int id)
	{
	    // Return a json object with the result of the operation.
		ObjectNode result = Json.newObject();
	    
		result.put("error", id);
		return status(NOT_IMPLEMENTED, result);
	}
	
	@Transactional
	public static Result delete(int id)
	{
	    // Return a json object with the result of the operation.
		ObjectNode result = Json.newObject();

		result.put("error", id);
		return status(NOT_IMPLEMENTED, result);
	}
	
	// TODO move to an Utils class!
	private static <T> String buildMessageFromValidationErrors(Form<T> form)
	{
		StringBuilder errorString = new StringBuilder();
    	for(List<ValidationError> errors : form.errors().values())
    	{
    		for(ValidationError error : errors)
    		{
    			if(errorString.length() > 0)
    				errorString.append(", ");
    			errorString.append(error.message());
    		}
    	}
    	return errorString.toString();
	}
}
