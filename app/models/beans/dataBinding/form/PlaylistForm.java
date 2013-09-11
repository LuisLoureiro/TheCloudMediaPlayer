package models.beans.dataBinding.form;

import java.util.List;

import play.data.validation.Constraints.Required;

public class PlaylistForm
{
	private long id;
	@Required(message="form.playlistForm.nameRequired")
	private String name;
	private List<Content> contentsToAdd;
	private List<Content> contentsToRemove;

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
	public List<Content> getContentsToAdd() {
		return contentsToAdd;
	}
	public void setContentsToAdd(List<Content> contentsToAdd) {
		this.contentsToAdd = contentsToAdd;
	}
	public List<Content> getContentsToRemove()
	{
		return contentsToRemove;
	}
	public void setContentsToRemove(List<Content> contentsToRemove)
	{
		this.contentsToRemove = contentsToRemove;
	}

	public static class Content
	{
		private int idx;
		private String id
				, provider;

		public int getIdx()
		{
			return idx;
		}
		public void setIdx(int idx)
		{
			this.idx = idx;
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
	}
}
