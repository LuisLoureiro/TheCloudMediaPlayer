package models.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import models.db.compositeKeys.ContentKey;

@Entity(name="contents")
public class Content
{
	@EmbeddedId
	private ContentKey key;
	
	@OneToMany(cascade={CascadeType.ALL}, mappedBy="content")
	private List<PlaylistContent> playlists;

	public Content(){}
	
	public Content(ContentKey key, List<PlaylistContent> playlists)
	{
		this.key = key;
		this.playlists = playlists;
	}
	
	public ContentKey getKey() {
		return key;
	}
	public void setKey(ContentKey key) {
		this.key = key;
	}
	public List<PlaylistContent> getPlaylists() {
		return playlists;
	}
	public void setPlaylists(List<PlaylistContent> playlists) {
		this.playlists = playlists;
	}
}
