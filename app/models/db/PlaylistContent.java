package models.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;

import models.db.compositeKeys.PlaylistContentKey;

@Entity(name="playlists_contents")
@IdClass(PlaylistContentKey.class)
public class PlaylistContent
{
	@Id
	@Column(name="playlist_id")
	private long playlistId;
	@Id
	@Column(name="content_id")
	private String contentId;
	@Id
	@Column(name="content_provider")
	private String contentProvider;
	@Id
	@Column(name="position")
	private int position;
	
	@ManyToOne
	@PrimaryKeyJoinColumns({
		@PrimaryKeyJoinColumn(name="content_id", referencedColumnName="id"),
		@PrimaryKeyJoinColumn(name="content_provider", referencedColumnName="provider")
	})
	private Content content;
	
	@ManyToOne
	@PrimaryKeyJoinColumn(name="playlist_id", referencedColumnName="id")
	private Playlist playlist;
	
	public PlaylistContent(){}
	
	public PlaylistContent(int position, Content content, Playlist playlist)
	{
		this.position = position;
		setContent(content);
		setPlaylist(playlist);
	}
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
		this.contentId = content.getKey().getId();
		this.contentProvider = content.getKey().getProvider();
	}
	public Playlist getPlaylist() {
		return playlist;
	}
	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
		this.playlistId = playlist.getId();
	}
	public int getPosition(){
		return position;
	}
	public void setPosition(int position){
		this.position = position;
	}
}
