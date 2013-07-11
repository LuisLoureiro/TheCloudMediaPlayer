package controllers.operations.persistence;

import javax.persistence.EntityExistsException;

import models.db.Playlist;
import models.mapper.IMapper;
import models.mapper.PlaylistMapper;
import models.mapper.UserMapper;

public class PersistPlaylist
{
	public static long savePlaylist(String userId, String name)
	{
		IMapper<Long, Playlist> mapper = new PlaylistMapper();
		
		Playlist playlist = new Playlist();
		playlist.setName(name);
		playlist.setUser(new UserMapper().findById(userId));
		try
		{
			mapper.save(playlist);
		} catch(EntityExistsException e)
		{
			mapper.update(playlist);
		}
		
		return playlist.getId();
	}
}
