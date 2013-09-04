package models.beans.dataObject;

public class Resource
{
	private String id
		, name
		, mimeType;

	public Resource(String id, String name, String mimeType)
	{
		setId(id);
		setName(name);
		setMimeType(mimeType);
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
}
