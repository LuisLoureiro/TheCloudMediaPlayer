package models.beans.dataObject;

/**
 * This class represents an audio or video content used by templates.
 */
public class Content
{
	private String id
		, provider
		;

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
}
