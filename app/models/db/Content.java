package models.db;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import models.db.compositeKeys.ContentKey;
import play.data.validation.Constraints.Required;

@Entity(name="contents")
public class Content
{
	@EmbeddedId
	private ContentKey key;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="content")
	@Required(message="user.playList.notNull")
	private List<PlaylistContent> playlists;

	public Content()
	{
		this(null, new LinkedList<PlaylistContent>());
	}
	
	public Content(ContentKey key, List<PlaylistContent> playlists)
	{
		setKey(key);
		setPlaylists(playlists);
	}
	
	public ContentKey getKey()
	{
		return key;
	}
	public void setKey(ContentKey key)
	{
		this.key = key;
	}
	public List<PlaylistContent> getPlaylists()
	{
		return playlists;
	}
	public void setPlaylists(List<PlaylistContent> playlists)
	{
		if(this.playlists != playlists)
		{
			if(this.playlists != null)
			{
				for(Iterator<PlaylistContent> itr = this.playlists.iterator(); itr.hasNext();)
				{
					PlaylistContent playlistContent = itr.next();
					itr.remove();
					playlistContent.setPlaylist(null);
				}
			}
			if(playlists != null)
			{
				this.playlists = playlists;
				for(PlaylistContent playlistContent : playlists)
				{
					playlistContent.setContent(this);
				}
			}
		}
		else if(this.playlists == null)
			this.playlists = new LinkedList<PlaylistContent>();
	}
	
	public void addPlaylist(PlaylistContent playlist)
	{
		if(playlist != null && !this.playlists.contains(playlist))
		{
			this.playlists.add(playlist);
			playlist.setContent(this);
		}
	}
	
	public void removePlaylist(PlaylistContent playlist)
	{
		if(playlist != null && this.playlists.contains(playlist))
		{
			this.playlists.remove(playlist);
			playlist.setContent(null);
		}
	}
}
