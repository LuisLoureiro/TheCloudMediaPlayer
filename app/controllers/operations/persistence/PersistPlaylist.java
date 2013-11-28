package controllers.operations.persistence;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.PersistenceException;

import models.database.Content;
import models.database.Playlist;
import models.database.PlaylistContent;
import models.database.compositeKeys.ContentKey;
import models.database.compositeKeys.PlaylistContentKey;
import models.mapper.IMapper;
import models.mapper.PlaylistContentMapper;
import models.mapper.PlaylistMapper;
import models.mapper.UserMapper;
import utils.Utils;
import utils.Utils.IPredicate;
import utils.Utils.ITransform;
import controllers.operations.persistence.exceptions.UserPlaylistException;

public class PersistPlaylist
{
	private static final IMapper<Long, Playlist> MAPPER = new PlaylistMapper();
	
	public static long savePlaylist(String userId, String name, List<controllers.operations.persistence.dataObjects.Content> contents)
			throws Exception
	{
		try
		{
			final Playlist playlist = new Playlist(0, name, new UserMapper().findById(userId), null);
			updatePlaylistContents(playlist, null
					, Utils.transform(contents, new ITransform<controllers.operations.persistence.dataObjects.Content, PlaylistContent>()
					{
						@Override
						public PlaylistContent transform(controllers.operations.persistence.dataObjects.Content elem)
						{
							Content content = PersistContent.findIfNullCreate(elem.getId(), elem.getProvider());
							return new PlaylistContent(elem.getIdx(), content, playlist);
						}
					}));
			MAPPER.save(playlist);
			/*
			 * Java EE 6 tutorial : "The state of persistent entities is
			 * synchronized to the database when the transaction with which
			 * the entity is associated commits."
			 * 
			 * We need to force synchronization to get the play list id.
			 */
			MAPPER.sync();
			
			return playlist.getId();
		}
		catch(PersistenceException e)
		{
			if(e.getMessage().toLowerCase().contains("unique"))
			{
				throw new UserPlaylistException("user.playList.errors.uniqueConstraintViolation");
			}
			else
				throw new Exception(e);
		}
	}
	
	public static void updatePlaylist(long id,
			List<controllers.operations.persistence.dataObjects.Content> contentsToAdd,
			List<controllers.operations.persistence.dataObjects.Content> contentsToRemove)
	{
		final Playlist playlist = MAPPER.findById(id);
		
		updatePlaylistContents(playlist
				, Utils.transformWithPredicate(contentsToRemove, new ITransform<controllers.operations.persistence.dataObjects.Content, PlaylistContent>()
					{
						private final IMapper<PlaylistContentKey, PlaylistContent> MAPPER = new PlaylistContentMapper();
						
						@Override
						public PlaylistContent transform(controllers.operations.persistence.dataObjects.Content elem)
						{
							return MAPPER.findById(new PlaylistContentKey(playlist.getId(), elem.getId(), elem.getProvider(), elem.getIdx()));
						}
					}, new IPredicate<PlaylistContent>()
						{
							@Override
							public boolean evaluate(PlaylistContent elem)
							{
								return elem != null;
							}
						})
				, Utils.transform(contentsToAdd, new Utils.ITransform<controllers.operations.persistence.dataObjects.Content, PlaylistContent>()
						{
							@Override
							public PlaylistContent transform(controllers.operations.persistence.dataObjects.Content elem)
							{
								Content content = PersistContent.findIfNullCreate(elem.getId(), elem.getProvider());
								return new PlaylistContent(elem.getIdx(), content, playlist);
							}
						}));
		MAPPER.update(playlist);
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
	
	public static models.beans.dataObject.Playlist loadPlaylist(String userId, long playlistId)
			throws UserPlaylistException
	{
		Playlist playlist = MAPPER.findById(playlistId);
		verifyPlaylist(playlist, userId);
		
		models.beans.dataObject.Playlist returnPlaylist = new models.beans.dataObject.Playlist();
		returnPlaylist.setId(playlist.getId());
		returnPlaylist.setTitle(playlist.getName());
		returnPlaylist.setContents(Utils.transform(playlist.getContents(),
				new ITransform<PlaylistContent, models.beans.dataObject.Content>()
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
	
	public static String deletePlaylist(String userId, long playlistId) throws UserPlaylistException
	{
		Playlist playlist = MAPPER.findById(playlistId);
		verifyPlaylist(playlist, userId);
		
		playlist.setUser(null);
		
		updatePlaylistContents(playlist, new ArrayList<PlaylistContent>(playlist.getContents()), null);
		MAPPER.delete(playlist);
		
		return playlist.getName();
	}
	
	private static void verifyPlaylist(Playlist playlist, String userId) throws UserPlaylistException
	{
		if(playlist == null || !userId.equals(playlist.getUser().getId()))
			throw new UserPlaylistException("user.playList.errors.invalidIdOrUserId");
	}
	
	private static void updatePlaylistContents(Playlist playlist,
			List<PlaylistContent> contentsToRemove, List<PlaylistContent> contentsToAdd)
	{
		if(contentsToRemove != null && !contentsToRemove.isEmpty())
		{
			for(PlaylistContent contentToRemove : contentsToRemove)
			{
				playlist.removeContent(contentToRemove);
			}
			PersistContent.deleteContentsWithoutPlaylist(contentsToRemove);
		}
		if(contentsToAdd != null && !contentsToAdd.isEmpty())
		{
			for(PlaylistContent contentToAdd : contentsToAdd)
			{
				playlist.addContent(contentToAdd);
			}
		}
	}
}
