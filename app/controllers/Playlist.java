package controllers;

import static play.data.Form.form;
import static play.libs.Akka.future;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import models.beans.dataBinding.form.PlaylistForm;
import models.beans.dataBinding.form.PlaylistForm.Content;
import models.beans.dataObject.AsyncActionRecoverErrorObject;
import models.beans.dataObject.AsyncActionRecoverObject;

import org.codehaus.jackson.node.ObjectNode;

import play.data.Form;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.libs.F.Function;
import play.libs.F.Function0;
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
	public static Result save() throws Exception
	{
		Form<PlaylistForm> playlistForm = form(PlaylistForm.class).bindFromRequest();
		if(playlistForm.hasErrors())
		{
			ObjectNode result = Json.newObject();
			result.put("error", Utils.buildMessageFromValidationErrors(playlistForm, ctx().lang()));
			return badRequest(result);
		}
		final PlaylistForm playlist = playlistForm.get();
		final String username = session(SESSION.USERNAME.toString());
		
		return async(future(new Callable<AsyncActionRecoverObject<models.beans.dataObject.Playlist>>()
		{
			@Override
			public AsyncActionRecoverObject<models.beans.dataObject.Playlist> call() throws Exception
			{
				try
				{
					return JPA.withTransaction(new Function0<AsyncActionRecoverObject<models.beans.dataObject.Playlist>>()
					{
						@Override
						public AsyncActionRecoverObject<models.beans.dataObject.Playlist> apply() throws Throwable
						{
							models.beans.dataObject.Playlist returnData = new models.beans.dataObject.Playlist();
							AsyncActionRecoverObject<models.beans.dataObject.Playlist> returnObject = new AsyncActionRecoverObject<models.beans.dataObject.Playlist>();
							AsyncActionRecoverErrorObject responseContent = new AsyncActionRecoverErrorObject();
							returnObject.setActionError(responseContent);
							returnObject.setData(returnData);
							
							Utils.ITransform<PlaylistForm.Content, controllers.operations.persistence.dataObjects.Content> transform = 
									new Utils.ITransform<PlaylistForm.Content, controllers.operations.persistence.dataObjects.Content>()
							{
								@Override
								public controllers.operations.persistence.dataObjects.Content transform(Content elem)
								{
									return new controllers.operations.persistence.dataObjects.Content(elem.getIdx(), elem.getId(), elem.getProvider());
								}
							};
							
							long id = playlist.getId();
							if(id == 0)
							{
								id = PersistPlaylist.savePlaylist(username, playlist.getName(),
										Utils.transform(playlist.getContentsToAdd(), transform));
								
								responseContent.setMessage("user.playList.save.createdSuccessfully");
								responseContent.setResponseStatus(CREATED);
							}
							else
							{
								PersistPlaylist.updatePlaylist(id, Utils.transform(playlist.getContentsToAdd(), transform)
										, Utils.transform(playlist.getContentsToRemove(), transform));
								
								responseContent.setMessage("user.playList.save.updatedSuccessfully");
								responseContent.setResponseStatus(OK);
							}
							
							returnData.setId(id);
							returnData.setTitle(playlist.getName());
							
							return returnObject;
						}
					});
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					throw new Exception(e);
				}
			}
		}).map(new Function<AsyncActionRecoverObject<models.beans.dataObject.Playlist>, Result>()
		{
			@Override
			public Result apply(AsyncActionRecoverObject<models.beans.dataObject.Playlist> playlist) throws Throwable
			{
				// Return a json object with the result of the operation.
				ObjectNode result = Json.newObject();
				result.put("name", playlist.getData().getTitle());
				result.put("message", Messages.get(playlist.getActionError().getMessage()));
				result.put("id", playlist.getData().getId());
				
				return status(playlist.getActionError().getResponseStatus(), result);
			}
		}));
	}
	
	public static Result load(final int id) throws Exception
	{
		// Check the value of the id parameter to execute the correct operation:
		// * if == 0, return the user play lists: name & id
		// * if != 0, return the list of contents related to the user play list: name & contents (id & title & provider)
		final String username = session(SESSION.USERNAME.toString());
		
		return async(future(new Callable<List<models.beans.dataObject.Playlist>>()
		{
			@Override
			public List<models.beans.dataObject.Playlist> call() throws Exception
			{
				try
				{
					return JPA.withTransaction("default", true, new Function0<List<models.beans.dataObject.Playlist>>()
					{
						@Override
						public List<models.beans.dataObject.Playlist> apply() throws Throwable
						{
							List<models.beans.dataObject.Playlist> playLists = null;
							if(id == 0)
							{
								playLists = PersistPlaylist.loadPlaylists(username);
							}
							else
							{
								playLists = new LinkedList<models.beans.dataObject.Playlist>();
								playLists.add(PersistPlaylist.loadPlaylist(username, id));
							}
							return playLists;
						}
					});
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					throw new Exception(e);
				}
			}
		}).map(new Function<List<models.beans.dataObject.Playlist>, Result>()
		{
			@Override
			public Result apply(List<models.beans.dataObject.Playlist> playLists) throws Throwable
			{
				// Return a json object with the result of the operation.
				ObjectNode result = Json.newObject();
				result.put("playlists", Json.toJson(playLists));
				
				return ok(result);
			}
		}));
	}
	
	public static Result delete(final int id) throws Exception
	{
		final String username = session(SESSION.USERNAME.toString());
		return async(future(new Callable<String>()
		{
			@Override
			public String call() throws Exception
			{
				try
				{
					return JPA.withTransaction(new Function0<String>()
					{
						@Override
						public String apply() throws Throwable
						{
							return PersistPlaylist.deletePlaylist(username, id);
						}
					});
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					throw new Exception(e);
				}
			}
		}).map(new Function<String, Result>()
		{
			@Override
			public Result apply(String name) throws Throwable
			{
				// Return a json object with the result of the operation.
				ObjectNode result = Json.newObject();

				result.put("message", Messages.get("user.playList.delete.successMessage", name));
				return ok(result);
			}
		}));
	}
}
