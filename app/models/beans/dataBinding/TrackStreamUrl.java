package models.beans.dataBinding;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//can be used to indicate that certain properties are to be ignored for serialization and/or deserialization (handling differs a bit depending on which operation is affected):
@JsonIgnoreProperties(ignoreUnknown=true)
public class TrackStreamUrl
{
	private String location;

	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
}
