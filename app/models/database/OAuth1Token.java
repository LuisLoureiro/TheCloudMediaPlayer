package models.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import play.data.validation.Constraints.Required;

@Entity(name="oauth1_tokens")
public class OAuth1Token {

	@Id
	@Column(nullable=false)
	private String token;
	@Required
	@Column(nullable=false)
	private String secret;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getSecret() {
		return secret;
	}
	public void setSecret(String secret) {
		this.secret = secret;
	}
}
