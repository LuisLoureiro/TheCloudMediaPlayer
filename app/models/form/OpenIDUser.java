package models.form;

import play.data.validation.Constraints.Required;

public class OpenIDUser {

	@Required(message="The OpenID identifier cannot be null or empty.")
	private String openid_identifier;

	public String getOpenid_identifier() {
		return openid_identifier;
	}

	public void setOpenid_identifier(String openid_identifier) {
		this.openid_identifier = openid_identifier;
	}
}
