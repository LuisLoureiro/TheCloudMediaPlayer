package controllers;

import static play.data.Form.form;

import java.util.List;

import org.codehaus.jackson.node.ObjectNode;

import models.beans.dataBinding.form.PlaylistForm;
import play.data.Form;
import play.data.validation.ValidationError;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

@Authenticated
public class Playlist extends Controller
{
	public static Result save()
	{
	    // Return a json object with the result of the operation.
		ObjectNode result = Json.newObject();
		
		// Grab the play list name from the request body.
	    // Using Form with Modal class. See: http://www.playframework.com/documentation/2.1.1/JavaForms
	    Form<PlaylistForm> playlistForm = form(PlaylistForm.class).bindFromRequest();
	    if(playlistForm.hasErrors())
	    {
	    	StringBuilder errorString = new StringBuilder();
	    	for(List<ValidationError> errors : playlistForm.errors().values())
	    	{
	    		for(ValidationError error : errors)
	    		{
	    			if(errorString.length() > 0)
	    				errorString.append(", ");
	    			errorString.append(error.message());
	    		}
	    	}
	    	result.put("error", errorString.toString());
	    	return badRequest(result);
	    }
	    PlaylistForm playlist = playlistForm.get();
	    
	    // ... or using dynamic form:
//	    DynamicForm requestData = form().bindFromRequest();
//	    String playListName = requestData.get("name");
	    
		result.put("error", playlist.getName());
		return status(NOT_IMPLEMENTED, result);
	}
	
	public static Result load(int id)
	{
		return status(NOT_IMPLEMENTED);
	}
	
	public static Result delete(int id)
	{
		return status(NOT_IMPLEMENTED);
	}
}
