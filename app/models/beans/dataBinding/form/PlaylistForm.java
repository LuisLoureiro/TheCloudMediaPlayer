package models.beans.dataBinding.form;

import play.data.validation.Constraints.Required;

public class PlaylistForm
{
	@Required(message="The name of the playlist cannot be null.")
	private String name;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
