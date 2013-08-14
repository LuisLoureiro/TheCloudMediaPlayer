package models.db;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Version;

@Entity(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(discriminatorType = DiscriminatorType.STRING, name = "type")
@DiscriminatorValue(value = "other")
public class User
{
	@Version
	private long version;
	@Id
	private String			id;
	private String			email;
	@ManyToOne
	@JoinColumn(name = "first_auth_id")
	private User			firstAuth;
	@OneToMany(mappedBy = "firstAuth")
	private List<User>		relatedUsers;
	@OneToMany(cascade = { CascadeType.ALL }, mappedBy = "user")
	private List<Playlist>	playlists;
	
	public User()
	{
		this(null, null, null, new LinkedList<User>(), new LinkedList<Playlist>());
	}
	
	public User(String id, String email, User firstAuth, List<User> relatedUsers, List<Playlist> playlists)
	{
		setId(id);
		setEmail(email);
		setFirstAuth(firstAuth);
		setRelatedUsers(relatedUsers);
		setPlaylists(playlists);
	}
	
	public long getVersion()
	{
		return version;
	}
	
	public String getId()
	{
		return id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
	
	public String getEmail()
	{
		return email;
	}
	
	public void setEmail(String email)
	{
		this.email = email;
	}
	
	public User getFirstAuth()
	{
		return firstAuth;
	}
	
	public void setFirstAuth(User firstAuth)
	{
		if(this.firstAuth != firstAuth)
		{
			if(this.firstAuth != null)
				this.firstAuth.removeUser(this);
			
			this.firstAuth = firstAuth;
			if(firstAuth != null)
				firstAuth.addUser(this);
		}
	}
	
	public List<User> getRelatedUsers()
	{
		return relatedUsers;
	}
	
	public void setRelatedUsers(List<User> relatedUsers)
	{
		if(this.relatedUsers != relatedUsers)
		{
			if(this.relatedUsers != null)
			{
				for(Iterator<User> itr = this.relatedUsers.iterator(); itr.hasNext();)
				{
					User user = itr.next();
					itr.remove();
					user.setFirstAuth(null);
				}
			}
			if(relatedUsers != null)
			{
				this.relatedUsers = relatedUsers;
				for(User user : relatedUsers)
				{
					user.setFirstAuth(this);
				}
			}
		}
		else if(this.relatedUsers == null)
			this.relatedUsers = new LinkedList<User>();
	}
	
	public List<Playlist> getPlaylists()
	{
		return playlists;
	}
	
	public void setPlaylists(List<Playlist> playlists)
	{
		if(this.playlists != playlists)
		{
			if(this.playlists != null)
			{
				for(Iterator<Playlist> itr = this.playlists.iterator(); itr.hasNext();)
				{
					Playlist playlist = itr.next();
					itr.remove();
					playlist.setUser(null);
				}
			}
			if(playlists != null)
			{
				this.playlists = playlists;
				for(Playlist playlist : playlists)
				{
					playlist.setUser(this);
				}
			}
		}
		else if(this.playlists == null)
			this.playlists = new LinkedList<Playlist>();
	}
	
	public void addUser(User user)
	{
		if(user != null && !this.relatedUsers.contains(user))
		{
			this.relatedUsers.add(user);
			user.setFirstAuth(this);
		}
	}
	
	public void removeUser(User user)
	{
		if(user != null && this.relatedUsers.contains(user))
		{
			this.relatedUsers.remove(user);
			user.setFirstAuth(null);
		}
	}
	
	public void addPlaylist(Playlist playlist)
	{
		if(playlist != null && !this.playlists.contains(playlist))
		{
			this.playlists.add(playlist);
			playlist.setUser(this);
		}
	}
	
	public void removePlaylist(Playlist playlist)
	{
		if(playlist != null && this.playlists.contains(playlist))
		{
			this.playlists.remove(playlist);
			playlist.setUser(null);
		}
	}
}
