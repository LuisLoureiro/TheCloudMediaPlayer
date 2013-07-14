package controllers.operations.persistence;

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
}
