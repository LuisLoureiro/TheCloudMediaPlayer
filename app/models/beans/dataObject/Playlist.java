package models.beans.dataObject;

import java.util.List;

/**
 * This class represents a play list used by templates.
 */
public class Playlist
{
	private long id;
	private String title;
	private List<Content> contents;

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
	public List<Content> getContents() {
		return contents;
	}
	public void setContents(List<Content> contents) {
		this.contents = contents;
	}
}
