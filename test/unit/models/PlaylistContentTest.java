package unit.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import models.database.Content;
import models.database.Playlist;
import models.database.PlaylistContent;
import models.database.User;
import models.database.compositeKeys.ContentKey;

import org.junit.Test;

import unit.BaseTest;

public class PlaylistContentTest extends BaseTest
{
	@Test
	public final void testThatEveryRelationshipIsCorrectlyCreated()
	{
		PlaylistContent playlistContent = new PlaylistContent(1, new Content(new ContentKey("first", "dropbox"), null),
				new Playlist(0, "first", new User("first", null, null, null, null), null));
		
		assertNotNull(playlistContent.getContent());
		assertNotNull(playlistContent.getContent().getPlaylists());
		assertFalse(playlistContent.getContent().getPlaylists().isEmpty());
		assertNotNull(playlistContent.getPlaylist());
		assertNotNull(playlistContent.getPlaylist().getContents());
		assertFalse(playlistContent.getPlaylist().getContents().isEmpty());
		assertNotNull(playlistContent.getPlaylist().getUser());
		assertNotNull(playlistContent.getPlaylist().getUser().getPlaylists());
		assertFalse(playlistContent.getPlaylist().getUser().getPlaylists().isEmpty());
		
		assertSame(playlistContent, playlistContent.getContent().getPlaylists().iterator().next());
		assertSame(playlistContent, playlistContent.getPlaylist().getContents().iterator().next());
	}
}
