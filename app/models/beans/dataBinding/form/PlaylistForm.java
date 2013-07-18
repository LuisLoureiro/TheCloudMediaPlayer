package models.beans.dataBinding.form;

import play.data.validation.Constraints.Required;

public class PlaylistForm
{
	private long id;
	@Required(message="form.playlistForm.nameRequired")
	private String name;

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
}
