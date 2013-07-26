package controllers.operations.persistence;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.persistence.PersistenceException;

import models.db.Content;
import models.db.Playlist;
import models.db.PlaylistContent;
import models.db.compositeKeys.ContentKey;
import models.mapper.ContentMapper;
import models.mapper.IMapper;
import models.mapper.PlaylistMapper;
import models.mapper.UserMapper;
import play.i18n.Lang;
import play.i18n.Messages;
import utils.Utils;

public class PersistPlaylist
{
	public static long savePlaylist(long id, String userId, String name, List<Entry<String, String>> contents, Lang lang)
			throws Exception
	{
		IMapper<Long, Playlist> mapper = new PlaylistMapper();
		Playlist playlist = null;
		
		if(id == 0)
		{
			try
			{
				playlist = new Playlist(0, name, new UserMapper().findById(userId), getPlaylistContents(playlist,
						contents));
				mapper.save(playlist);
				/*
				 * Java EE 6 tutorial : "The state of persistent entities is
				 * synchronized to the database when the transaction with which
				 * the entity is associated commits."
				 * 
				 * We need to force synchronization to get the play list id.
				 */
				mapper.sync();
			}
			catch(PersistenceException e)
			{
				if(e.getMessage().contains("unique constraint"))
				{
					throw new Exception(Messages.get(lang, "user.playList.errors.uniqueConstraintViolation"));
				}
				else
					throw new Exception(e);
			}
		}
		else
		{
			playlist = mapper.findById(id);
			playlist.setName(name);
			playlist.setContents(getPlaylistContents(playlist, contents));
			
			mapper.update(playlist);
		}
		
		return playlist.getId();
	}
	
	/**
	 * 
	 * @param userId
	 * @return all the play lists of the specified user: id and name.
	 */
	public static List<models.beans.dataObject.Playlist> loadPlaylists(String userId)
	{
		List<models.beans.dataObject.Playlist> userPlayLists = new LinkedList<models.beans.dataObject.Playlist>();
		
		for(Playlist playlist : new UserMapper().findById(userId).getPlaylists())
		{
			models.beans.dataObject.Playlist returningPlaylist = new models.beans.dataObject.Playlist();
			returningPlaylist.setId(playlist.getId());
			returningPlaylist.setTitle(playlist.getName());
			
			userPlayLists.add(returningPlaylist);
		}
		
		return userPlayLists;
	}
	
	public static models.beans.dataObject.Playlist loadPlaylist(String userId, long playlistId, Lang lang)
			throws Exception
	{
		Playlist playlist = new PlaylistMapper().findById(playlistId);
		verifyPlaylist(playlist, userId, lang);
		
		models.beans.dataObject.Playlist returnPlaylist = new models.beans.dataObject.Playlist();
		returnPlaylist.setId(playlist.getId());
		returnPlaylist.setTitle(playlist.getName());
		returnPlaylist.setContents(Utils.transform(playlist.getContents(),
				new Utils.ITransform<PlaylistContent, models.beans.dataObject.Content>()
				{
					@Override
					public models.beans.dataObject.Content transform(PlaylistContent elem)
					{
						models.beans.dataObject.Content content = new models.beans.dataObject.Content();
						ContentKey contentKey = elem.getContent().getKey();
						content.setId(contentKey.getId());
						content.setProvider(contentKey.getProvider());
						return content;
					}
				}));
		
		return returnPlaylist;
	}
	
	public static String deletePlaylist(String userId, long playlistId, Lang lang) throws Exception
	{
		IMapper<Long, Playlist> mapper = new PlaylistMapper();
		Playlist playlist = mapper.findById(playlistId);
		verifyPlaylist(playlist, userId, lang);
		
		playlist.getUser().getPlaylists().remove(playlist);
		mapper.delete(playlist);
		// TODO beware with the contents! Just delete the contents that don't
		// belong to any play list.
		// See http://docs.oracle.com/javaee/6/tutorial/doc/bnbqa.html#giqxy
		
		return playlist.getName();
	}
	
	private static void verifyPlaylist(Playlist playlist, String userId, Lang lang) throws Exception
	{
		if(playlist == null || !userId.equals(playlist.getUser().getId()))
			throw new Exception(Messages.get(lang, "user.playList.errors.invalidIdOrUserId"));
	}
	
	private static List<PlaylistContent> getPlaylistContents(Playlist playlist, List<Entry<String, String>> contents)
	{
		List<PlaylistContent> playlistContents = new LinkedList<PlaylistContent>();
		if(contents != null)
		{
			for(int i = 0; i < contents.size(); i++)
			{
				// Find content in the database.
				ContentKey key = new ContentKey(contents.get(i).getKey(), contents.get(i).getValue());
				Content content = new ContentMapper().findById(key);
				if(content == null)
				{
					content = new Content(key, new LinkedList<PlaylistContent>());
				}
				//
				// PlaylistContent playlistContent = new PlaylistContent(i + 1,
				// content, playlist);
				// playlistContents.add(playlistContent);
				playlistContents.add(new PlaylistContent(i + 1, content, playlist));
			}
		}
		
		return playlistContents;
	}
}
