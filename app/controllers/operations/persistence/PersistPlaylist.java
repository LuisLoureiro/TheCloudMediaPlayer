package controllers.operations.persistence;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import models.db.Playlist;
import models.mapper.IMapper;
import models.mapper.PlaylistMapper;
import models.mapper.UserMapper;
import play.i18n.Lang;
import play.i18n.Messages;

public class PersistPlaylist
{
	public static long savePlaylist(String userId, long id, String name, Lang lang) throws Exception
	{
		IMapper<Long, Playlist> mapper = new PlaylistMapper();
		
		Playlist playlist = new Playlist();
		playlist.setName(name);
		playlist.setUser(new UserMapper().findById(userId));
		
		if(id == 0)
		{
			try
			{
				mapper.save(playlist);
				/* Java EE 6 tutorial : "The state of persistent entities is synchronized to the database
				 *  when the transaction with which the entity is associated commits."
				 *  
				 * We need to force synchronization to get the play list id.
				 */
				mapper.sync();
			} catch(PersistenceException e)
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
			playlist.setId(id);
			mapper.update(playlist);
		}
		
		return playlist.getId();
	}
	
	/**
	 * 
	 * @param userId
	 * @return all the play lists of the specified user: id and name.
	 */
	public static List<models.beans.dataBinding.Playlist> loadPlaylists(String userId)
	{
		List<models.beans.dataBinding.Playlist> userPlayLists = new LinkedList<models.beans.dataBinding.Playlist>();
		
		for(Playlist playlist : new UserMapper().findById(userId).getPlaylists())
		{
			models.beans.dataBinding.Playlist returningPlaylist = new models.beans.dataBinding.Playlist();
			returningPlaylist.setId(playlist.getId());
			returningPlaylist.setTitle(playlist.getName());
			
			userPlayLists.add(returningPlaylist);
		}
		
		return userPlayLists;
	}
	
	public static models.beans.dataBinding.Playlist loadPlaylist(String userId, long playlistId, Lang lang) throws Exception
	{
		Playlist playlist = new PlaylistMapper().findById(playlistId);
		if(playlist == null || !userId.equals(playlist.getUser().getId()))
			throw new Exception(Messages.get(lang, "user.playList.errors.invalidIdOrUserId"));

		models.beans.dataBinding.Playlist returnPlaylist = new models.beans.dataBinding.Playlist();
		returnPlaylist.setId(playlist.getId());
		returnPlaylist.setTitle(playlist.getName());
		// TODO contents!
		
		return returnPlaylist;
	}
}
