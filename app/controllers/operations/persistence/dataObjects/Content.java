package controllers.operations.persistence.dataObjects;

public class Content
{
	private int idx;
	private String id
		, provider;
	
	public Content() {}
	
	public Content(int idx, String id, String provider)
	{
		this.idx = idx;
		this.id = id;
		this.provider = provider;
	}
	
	public int getIdx()
	{
		return idx;
	}
	public void setIdx(int idx)
	{
		this.idx = idx;
	}
	public String getId()
	{
		return id;
	}
	public void setId(String id)
	{
		this.id = id;
	}
	public String getProvider()
	{
		return provider;
	}
	public void setProvider(String provider)
	{
		this.provider = provider;
	}
}
