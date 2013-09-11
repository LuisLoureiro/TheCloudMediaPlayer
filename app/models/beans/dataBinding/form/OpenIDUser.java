package models.beans.dataBinding.form;

import play.data.validation.Constraints.Required;

public class OpenIDUser {

	@Required(message="form.openIdUser.identifierRequired")
	private String openid_identifier;

	public String getOpenid_identifier() {
		return openid_identifier;
	}

	public void setOpenid_identifier(String openid_identifier) {
		this.openid_identifier = openid_identifier;
	}
}
