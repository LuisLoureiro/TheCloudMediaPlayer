package unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import models.database.Content;
import models.database.Playlist;
import models.database.PlaylistContent;
import models.database.User;
import models.mapper.ContentMapper;
import models.mapper.PlaylistContentMapper;
import models.mapper.PlaylistMapper;
import models.mapper.UserMapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import play.i18n.Messages;
import play.libs.F.Callback0;
import play.libs.F.Function0;
import controllers.operations.persistence.PersistPlaylist;

public class PersistencePlaylistTest extends BaseTest
{
	@Rule
	public ExpectedException	exception	= ExpectedException.none();
	
	@Test
	public final void testLoadTestData()
	{
		runFakeAppWithReadOnlyTransaction(new Function0<Void>()
		{
			@Override
			public Void apply() throws Throwable
			{
				Collection<PlaylistContent> playlistsContents = new PlaylistContentMapper().getAll();
				assertNotNull(playlistsContents);
				assertEquals(3, playlistsContents.size());
				
				for(PlaylistContent playlistContent : playlistsContents)
				{
					Content content = playlistContent.getContent();
					Playlist playlist = playlistContent.getPlaylist();
					User user = playlist.getUser();
					assertNotNull(content);
					assertNotNull(content.getKey());
					assertNotNull(playlist);
					assertNotNull(user);
					
					System.out.println(String.format("Data: position=%s, playlistId=%s, contentId=%s",
							playlistContent.getPosition(), playlist.getId(), content.getKey().getId()));
					
					if(playlistContent.getPosition() == 1) // Playlist 1 and Content 2 or Playlist 2 and Content 1
					{
						if(playlist.getId() == 1)
						{
							System.out.println("Position 1 = Playlist 1 and Content 2");
							assertEquals(firstPlaylistName, playlist.getName());
							assertNotNull(playlist.getContents());
							assertEquals(2, playlist.getContents().size());
							
							assertEquals(firstUserId, user.getId());
							assertEquals(firstUserEmail, user.getEmail());
							assertNotNull(user.getPlaylists());
							assertEquals(1, user.getPlaylists().size());
							assertSame(playlist, user.getPlaylists().iterator().next());
							
							assertEquals(secondContentId, content.getKey().getId());
							assertEquals(secondContentProvider, content.getKey().getProvider());
							assertNotNull(content.getPlaylists());
							assertEquals(1, content.getPlaylists().size());
							assertSame(playlist, content.getPlaylists().iterator().next().getPlaylist());
						}
						else if(playlist.getId() == 2)
						{
							System.out.println("Position 1 = Playlist 2 and Content 1");
							assertEquals(secondPlaylistName, playlist.getName());
							assertNotNull(playlist.getContents());
							assertEquals(1, playlist.getContents().size());
							assertSame(content, playlist.getContents().iterator().next().getContent());
							
							assertEquals(secondUserId, user.getId());
							assertNull(user.getEmail());
							assertNotNull(user.getPlaylists());
							assertEquals(1, user.getPlaylists().size());
							assertSame(playlist, user.getPlaylists().iterator().next());
							
							assertEquals(firstContentId, content.getKey().getId());
							assertEquals(firstContentProvider, content.getKey().getProvider());
							assertNotNull(content.getPlaylists());
							assertEquals(2, content.getPlaylists().size());
						}
						else
							fail("Inexistent playlist id.");
					}
					else if(playlistContent.getPosition() == 2)// Playlist 1 and Content 1
					{
						System.out.println("Position 2 = Playlist 1 and Content 1");
						assertEquals(firstPlaylistId, playlist.getId());
						assertEquals(firstPlaylistName, playlist.getName());
						assertNotNull(playlist.getContents());
						assertEquals(2, playlist.getContents().size());
						
						assertEquals(firstContentId, content.getKey().getId());
						assertEquals(firstContentProvider, content.getKey().getProvider());
						assertNotNull(content.getPlaylists());
						assertEquals(2, content.getPlaylists().size());
						
						assertEquals(firstUserId, user.getId());
						assertEquals(firstUserEmail, user.getEmail());
						assertNotNull(user.getPlaylists());
						assertEquals(1, user.getPlaylists().size());
						assertSame(playlist, user.getPlaylists().iterator().next());
					}
					else
						fail("Position not tested.");
				}
				return null;
			}
		});
	}
	
	@Test
	public final void testLoadPlaylists()
	{
		runFakeAppWithReadOnlyTransaction(new Function0<Void>()
		{
			@Override
			public Void apply() throws Throwable
			{
				List<models.beans.dataObject.Playlist> firstUserPlaylists = PersistPlaylist.loadPlaylists(firstUserId);
				assertEquals(1, firstUserPlaylists.size());
				
				models.beans.dataObject.Playlist firstUserPlaylist = firstUserPlaylists.iterator().next();
				assertEquals(firstPlaylistId, firstUserPlaylist.getId());
				assertEquals(firstPlaylistName, firstUserPlaylist.getTitle());
				assertNull(firstUserPlaylist.getContents());
				
				List<models.beans.dataObject.Playlist> secondUserPlaylists = PersistPlaylist
						.loadPlaylists(secondUserId);
				assertEquals(1, secondUserPlaylists.size());
				
				models.beans.dataObject.Playlist secondUserPlaylist = secondUserPlaylists.iterator().next();
				assertEquals(secondPlaylistId, secondUserPlaylist.getId());
				assertEquals(secondPlaylistName, secondUserPlaylist.getTitle());
				assertNull(secondUserPlaylist.getContents());
				
				return null;
			}
		});
	}
	
	@Test
	public final void testLoadPlaylist()
	{
		runFakeAppWithReadOnlyTransaction(new Function0<Void>()
		{
			@Override
			public Void apply() throws Throwable
			{
				models.beans.dataObject.Playlist playlist = PersistPlaylist.loadPlaylist(secondUserId, 2);
				assertNotNull(playlist);
				assertEquals(secondPlaylistId, playlist.getId());
				assertEquals(secondPlaylistName, playlist.getTitle());
				assertNotNull(playlist.getContents());
				assertEquals(1, playlist.getContents().size());
				
				models.beans.dataObject.Content content = playlist.getContents().iterator().next();
				assertNotNull(content);
				assertEquals(firstContentId, content.getId());
				assertEquals(firstContentProvider, content.getProvider());
				
				return null;
			}
		});
	}
	
	@Test
	public final void dontSavePlaylistWithTheSameNameAsAnExistingOneForTheSameUser()
	{
		runFakeAppWithTransaction(new Callback0()
		{
			@Override
			public void invoke() throws Throwable
			{
				try
				{
					PersistPlaylist.savePlaylist(firstUserId, firstPlaylistName, Arrays
							.asList(new controllers.operations.persistence.dataObjects.Content(2, firstContentId,
									firstContentProvider)));
					fail("Expected an UniqueConstraintException to be thrown.");
				}
				catch(Exception e)
				{
					assertEquals(Messages.get("user.playList.errors.uniqueConstraintViolation"), e.getMessage());
				}
			}
		});
	}
	
	@Test
	public final void testSavePlaylist()
	{
		runFakeAppWithTransaction(new Callback0()
		{
			@Override
			public void invoke() throws Throwable
			{
				String newPlaylistName = "thirdPlaylist";
				long id = PersistPlaylist.savePlaylist(firstUserId, newPlaylistName, Arrays
						.asList(new controllers.operations.persistence.dataObjects.Content(1, firstContentId,
								firstContentProvider)));
				assertEquals(3, id);
				
				// Check that every relationship was correctly created.
				models.beans.dataObject.Playlist loadPlaylist = PersistPlaylist.loadPlaylist(firstUserId, id);
				
				assertEquals(id, loadPlaylist.getId());
				assertEquals(newPlaylistName, loadPlaylist.getTitle());
				assertNotNull(loadPlaylist.getContents());
				assertEquals(1, loadPlaylist.getContents().size());
				
				models.beans.dataObject.Content content = loadPlaylist.getContents().iterator().next();
				assertEquals(firstContentId, content.getId());
				assertEquals(firstContentProvider, content.getProvider());
			}
		});
	}
	
	@Test
	public final void testUpdatePlaylist()
	{
		runFakeAppWithTransaction(new Callback0()
		{
			@Override
			public void invoke() throws Throwable
			{
				String newContentId = "thirdContent", newContentProvider = "youtube";
				
				PersistPlaylist.updatePlaylist(firstPlaylistId, Arrays
						.asList(new controllers.operations.persistence.dataObjects.Content(1, newContentId,
								newContentProvider)), Arrays
						.asList(new controllers.operations.persistence.dataObjects.Content(1, secondContentId,
								secondContentProvider)));
				
				models.beans.dataObject.Playlist loadPlaylist = PersistPlaylist.loadPlaylist(firstUserId,
						firstPlaylistId);
				
				assertNotNull(loadPlaylist.getContents());
				assertEquals(2, loadPlaylist.getContents().size());
				
				models.beans.dataObject.Content firstContent = loadPlaylist.getContents().iterator().next();
				assertEquals(newContentId, firstContent.getId());
				assertEquals(newContentProvider, firstContent.getProvider());
				
				models.beans.dataObject.Content secondContent = loadPlaylist.getContents().iterator().next();
				assertEquals(firstContentId, secondContent.getId());
				assertEquals(firstContentProvider, secondContent.getProvider());
			}
		});
	}
	
	@Test
	public final void testDeletePlaylist()
	{
		runFakeAppWithTransaction(new Callback0()
		{
			@Override
			public void invoke() throws Throwable
			{
				String name = PersistPlaylist.deletePlaylist(firstUserId, 1);
				assertEquals(firstPlaylistName, name);
				
				Collection<PlaylistContent> playlistsContents = new PlaylistContentMapper().getAll();
				assertEquals(1, playlistsContents.size());
				
				PlaylistContent playlistContent = playlistsContents.iterator().next();
				Playlist secondPlaylist = playlistContent.getPlaylist();
				Content firstContent = playlistContent.getContent();
				User secondUser = secondPlaylist.getUser();
				assertEquals(1, playlistContent.getPosition());
				assertEquals(2, secondPlaylist.getId());
				assertEquals(secondPlaylistName, secondPlaylist.getName());
				assertEquals(firstContentId, firstContent.getKey().getId());
				assertEquals(firstContentProvider, firstContent.getKey().getProvider());
				assertEquals(secondUserId, secondUser.getId());
				
				Collection<Playlist> playlists = new PlaylistMapper().getAll();
				Collection<Content> contents = new ContentMapper().getAll();
				Collection<User> users = new UserMapper().getAll();
				assertEquals(1, playlists.size());
				assertEquals(2, users.size());
				assertEquals(1, contents.size());
				
				for(User user : users)
				{
					if(firstUserId.equals(user.getId()))
					{
						assertTrue(user.getPlaylists().isEmpty());
					}
					else if(secondUserId.equals(user.getId()))
					{
						assertEquals(1, user.getPlaylists().size());
						assertSame(secondUser, user);
					}
					else
						fail("Inexistent userId.");
				}
				
				Playlist playlist = playlists.iterator().next();
				Content content = contents.iterator().next();
				assertSame(secondPlaylist, playlist);
				assertSame(firstContent, content);
			}
		});
	}
}
