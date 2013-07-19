package models.db.compositeKeys;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class ContentKey implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2925427129576860135L;
	private String id;
	private String provider;
	
	public ContentKey(){}
	
	public ContentKey(String id, String provider)
	{
		this.id = id;
		this.provider = provider;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((provider == null) ? 0 : provider.hashCode());
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
		ContentKey other = (ContentKey) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		return true;
	}
	
}
