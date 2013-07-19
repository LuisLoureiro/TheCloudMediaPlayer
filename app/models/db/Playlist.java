package models.db;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Constraints.Required;

@Entity(name="playlists")
@Table(uniqueConstraints=@UniqueConstraint(name="unique_name_user_id", columnNames={"name", "user_id"}))
public class Playlist
{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	@Column(nullable=false)
	@Required(message="The name of the play list must be defined!")
	private String name;
	
	@ManyToOne
	@JoinColumn(name="user_id", referencedColumnName="id")
	private User user;

	@OneToMany(cascade={CascadeType.ALL}, mappedBy="playlist")
	private List<PlaylistContent> contents;
	
	public Playlist(){}
	
	public Playlist(long id, String name, User user, List<PlaylistContent> contents)
	{
		this.id = id;
		this.name = name;
		this.user = user;
		this.contents = contents;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
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
	public List<PlaylistContent> getContents() {
		return contents;
	}
	public void setContents(List<PlaylistContent> contents) {
		this.contents = contents;
	}
}
