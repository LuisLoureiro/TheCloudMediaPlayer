package models.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

import models.db.compositeKeys.PlaylistContentKey;
import models.db.listeners.PlaylistContentListener;

@Entity(name="playlists_contents")
@IdClass(PlaylistContentKey.class)
public class PlaylistContent
{
	@Version
	private long version;
	@Id
	@Column(name="playlist_id", insertable=false, updatable=false, nullable=false) // This value will be set by the Playlist entity.
	private long playlistId;
	@Id
	@Column(name="content_id", insertable=false, updatable=false, nullable=false) // This value will be set by the Content entity.
	private String contentId;
	@Id
	@Column(name="content_provider", insertable=false, updatable=false, nullable=false) // This value will be set by the Content entity.
	private String contentProvider;
	@Id
	@Column(name="position")
	private int position;
	
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="content_id", referencedColumnName="id"),
		@JoinColumn(name="content_provider", referencedColumnName="provider")
	})
	private Content content;
	
	@ManyToOne
	@JoinColumn(name="playlist_id", referencedColumnName="id")
	private Playlist playlist;
	
	public PlaylistContent(){}
	
	public PlaylistContent(int position, Content content, Playlist playlist)
	{
		setPosition(position);
		setContent(content);
		setPlaylist(playlist);
	}
	
	public long getVersion()
	{
		return version;
	}
	public Content getContent()
	{
		return content;
	}
	public void setContent(Content content)
	{
		if(this.content != content)
		{
			if(this.content != null)
				this.content.removePlaylist(this);

			this.content = content;
			if(content != null)
			{
				this.contentId = content.getKey().getId();
				this.contentProvider = content.getKey().getProvider();
				content.addPlaylist(this);
			}
			else
			{
				this.contentId = this.contentProvider = null;
				// Delete relationship
				setPlaylist(null);
			}
		}
	}
	public Playlist getPlaylist()
	{
		return playlist;
	}
	public void setPlaylist(Playlist playlist)
	{
		if(this.playlist != playlist)
		{
			if(this.playlist != null)
				this.playlist.removeContent(this);

			this.playlist = playlist;
			if(playlist != null)
			{
				this.playlistId = playlist.getId();
				playlist.addContent(this);
			}
			else
			{
				this.playlistId = 0;
				// Delete relationship
				setContent(null);
			}
		}
	}
	public int getPosition()
	{
		return position;
	}
	public void setPosition(int position)
	{
		this.position = position;
	}
}
