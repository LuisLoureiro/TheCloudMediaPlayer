package models.beans;

public class Resource
{
	private String name;

	public Resource(String name)
	{
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
