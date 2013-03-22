package models.form;

import play.data.validation.Constraints.Required;

public class OpenIDUser {

	@Required(message="The ID cannot be null or empty.")
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
