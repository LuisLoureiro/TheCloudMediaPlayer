package models.db;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import play.data.validation.Constraints.Required;

import models.db.compositeKeys.PlaylistPK;

@Entity(name="playlists")
public class Playlist
{
	@AttributeOverride(name="userId", column=@Column(name="user_id",
			nullable=false, insertable=false, updatable=false))
	@EmbeddedId
	private PlaylistPK key;
	
	@Column(nullable=false)
	@Required(message="The name of the play list must be defined!")
	private String name;

	@MapsId("userId")
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;
	
	public PlaylistPK getKey() {
		return key;
	}
	public void setKey(PlaylistPK key) {
		this.key = key;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
