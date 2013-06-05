package models.db;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import models.db.notEntity.OAuthUser;

import play.data.validation.Constraints.Required;

@Entity(name="oauth1_users")
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
@DiscriminatorValue(value="oauth1")
public class OAuth1User extends OAuthUser
{
	@Column(name="oauth_token", nullable=false)
	@NotNull(message="The oauth token could not be null")
	private String oauthToken;
	@Column(name="oauth_token_secret", nullable=false)
	@Required(message="The oauth token secret could not be null")
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
