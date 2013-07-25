package models.db;

import java.util.Iterator;
import java.util.LinkedList;
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

@Entity(name = "playlists")
@Table(uniqueConstraints = @UniqueConstraint(name = "unique_name_user_id", columnNames = { "name", "user_id" }))
public class Playlist
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long					id;
	
	@Column(nullable = false)
	@Required(message = "The name of the play list must be defined!")
	private String					name;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User					user;
	
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "playlist", orphanRemoval = true)
	@Required(message = "user.playList.notNull")
	private List<PlaylistContent>	contents;
	
	public Playlist()
	{
		this(0, null, null, new LinkedList<PlaylistContent>());
	}
	
	public Playlist(long id, String name, User user, List<PlaylistContent> contents)
	{
		setId(id);
		setName(name);
		setUser(user);
		setContents(contents);
	}
	
	public long getId()
	{
		return id;
	}
	
	public void setId(long id)
	{
		this.id = id;
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User user)
	{
		if(this.user != user)
		{
			if(this.user != null)
				this.user.removePlaylist(this);
			
			this.user = user;
			if(user != null)
				user.addPlaylist(this);
		}
	}
	
	public List<PlaylistContent> getContents()
	{
		return contents;
	}
	
	public void setContents(List<PlaylistContent> contents)
	{
		if(this.contents != contents)
		{
			if(this.contents != null)
			{
				for(Iterator<PlaylistContent> itr = this.contents.iterator(); itr.hasNext();)
				{
					PlaylistContent playlistContent = itr.next();
					itr.remove();
					playlistContent.setPlaylist(null);
				}
			}
			if(contents != null)
			{
				this.contents = contents;
				for(PlaylistContent playlistContent : contents)
				{
					playlistContent.setPlaylist(this);
				}
			}
		}
		else if(this.contents == null)
			this.contents = new LinkedList<PlaylistContent>();
	}
	
	public void addContent(PlaylistContent content)
	{
		if(content != null && !this.contents.contains(content))
		{
			this.contents.add(content);
			content.setPlaylist(this);
		}
	}
	
	public void removeContent(PlaylistContent content)
	{
		if(content != null && this.contents.contains(content))
		{
			this.contents.remove(content);
			content.setPlaylist(null);
		}
	}
}
