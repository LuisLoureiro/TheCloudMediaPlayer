package models.database;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import models.database.notEntity.OAuthUser;
import play.data.validation.Constraints.Required;

@Entity(name="oauth1_users")
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
@DiscriminatorValue(value="oauth1")
public class OAuth1User extends OAuthUser
{
	@Column(name="oauth_token", nullable=false)
	@NotNull(message="oauth1User.oauthTokenRequired")
	private String oauthToken;
	@Column(name="oauth_token_secret", nullable=false)
	@Required(message="oauth1User.oauthTokenSecretRequired")
	private String oauthTokenSecret;
	
	public String getOauthToken() {
		return oauthToken;
	}
	public void setOauthToken(String oauthToken) {
		this.oauthToken = oauthToken;
	}
	public String getOauthTokenSecret() {
		return oauthTokenSecret;
	}
	public void setOauthTokenSecret(String oauthTokenSecret) {
		this.oauthTokenSecret = oauthTokenSecret;
	}
}
