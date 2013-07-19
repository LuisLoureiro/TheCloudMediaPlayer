package models.beans.dataBinding.form;

import java.util.List;

import play.data.validation.Constraints.Required;

public class PlaylistForm
{
	private long id;
	@Required(message="form.playlistForm.nameRequired")
	private String name;
	@Required(message="form.playlistForm.contentsRequired")
	private List<Content> contents;

	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<Content> getContents() {
		return contents;
	}
	public void setContents(List<Content> contents) {
		this.contents = contents;
	}
	
	public static class Content
	{
		private String id;
		private String provider;
		
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
}
