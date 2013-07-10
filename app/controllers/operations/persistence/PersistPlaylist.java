package controllers.operations.persistence;

import java.util.Calendar;

import models.db.Playlist;
import models.db.compositeKeys.PlaylistPK;
import models.mapper.IMapper;
import models.mapper.PlaylistMapper;
import models.mapper.UserMapper;

public class PersistPlaylist
{
	public static int savePlaylist(String userId, String name)
	{
		IMapper<PlaylistPK, Playlist> mapper = new PlaylistMapper();
		
		// TODO check if userId and name are already registered!
		
		PlaylistPK pk = new PlaylistPK();
//		pk.setUserId(userId);
		/**
		 * The JPA2.0 API says that the @GeneratedValue annotation can only be used in simple primary keys which is not the case.
		 * So we need to generate an unique value to associate with this new play list object.
		 * Solution 1:
		 *  Create an entity that will held these values. Different values for different users.
		 *  When asking for a value the database operation should be: create a new table entry with the new being +1 than the last one.
		 *  This operation should be atomic to avoid concurrency problems.
		 *  
		 * The current solution is just a workaround to verify the functionality of the controller operations!
		 */
		Calendar cal = Calendar.getInstance();
		pk.setId(cal.get(Calendar.SECOND) + 
				cal.get(Calendar.MINUTE)*100 + 
				cal.get(Calendar.HOUR_OF_DAY)*10000 +
				cal.get(Calendar.MONTH)*1000000 +
				cal.get(Calendar.DAY_OF_MONTH)*100000000);
		
		Playlist playlist = new Playlist();
		playlist.setKey(pk);
		playlist.setName(name);
		playlist.setUser(new UserMapper().findById(userId)); // Didn't find another solution to resolve the database exception!
		
		mapper.save(playlist);
		
		return playlist.getKey().getId();
	}
}
