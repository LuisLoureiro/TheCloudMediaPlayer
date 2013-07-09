package models.db.compositeKeys;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PlaylistPK implements Serializable
{
	private static final long serialVersionUID = 7137060435380861889L;
	
	private int id;
	private String userId;

	public PlaylistPK() {}
	
	public PlaylistPK(int id, String userId)
	{
		this.id = id;
		this.userId = userId;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlaylistPK other = (PlaylistPK) obj;
		if (id != other.id)
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
}
