package models.beans.dataBinding;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//can be used to indicate that certain properties are to be ignored for serialization and/or deserialization (handling differs a bit depending on which operation is affected):
@JsonIgnoreProperties(ignoreUnknown=true)
public class Track
{
	private long id;
	private String title
		, stream_url
		;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getStream_url() {
		return stream_url;
	}
	public void setStream_url(String stream_url) {
		this.stream_url = stream_url;
	}
}
