package controllers;

import static play.data.Form.form;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;

import models.beans.dataBinding.form.PlaylistForm;
import models.beans.dataBinding.form.PlaylistForm.Content;

import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import utils.Utils;
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
	    	result.put("error", Utils.buildMessageFromValidationErrors(playlistForm, ctx().lang()));
	    	return badRequest(result);
	    }
	    PlaylistForm playlist = playlistForm.get();
	    
	    long id = PersistPlaylist.savePlaylist(
	    		session(SESSION.USERNAME.toString()), playlist.getId(), playlist.getName(),
	    		Utils.transform(playlist.getContents(), new Utils.ITransform<PlaylistForm.Content, Entry<String, String>>()
	    				{
							@Override
							public Entry<String, String> transform(Content elem)
							{
								return new SimpleEntry<String, String>(elem.getId(), elem.getProvider());
							}
						}
	    		), ctx().lang());
	    
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
			List<models.beans.dataObject.Playlist> playLists = PersistPlaylist.loadPlaylists(session(SESSION.USERNAME.toString()));
			result.put("playlists", Json.toJson(playLists));
		}
		else
		{
			models.beans.dataObject.Playlist playList = PersistPlaylist.loadPlaylist(session(SESSION.USERNAME.toString()), id, ctx().lang());
			result.put("id", playList.getId());
			result.put("title", playList.getTitle());
			result.put("contents", Json.toJson(playList.getContents()));
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
}
