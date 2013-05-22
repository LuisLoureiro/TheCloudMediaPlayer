package models.db;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import play.data.validation.Constraints.Required;

@Entity(name="oauth1_users")
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
@DiscriminatorValue(value="oauth1")
public class OAuth1User extends User
{
	@Column(name="oauth_token", nullable=false)
	@NotNull(message="The oauth token could not be null")
	private String oauthToken;
	@Required
	@Column(name="oauth_token_secret", nullable=false)
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
