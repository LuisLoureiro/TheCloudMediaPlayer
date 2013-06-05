package models.db;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

import models.db.notEntity.OAuthUser;

import play.data.validation.Constraints.Required;

@Entity(name="oauth2_users")
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
@DiscriminatorValue(value="oauth2")
public class OAuth2User extends OAuthUser
{
	@Column(name="access_token", nullable=false)
	@NotNull(message="The access_token could not be null")
	private String accessToken;
	@Column(name="refresh_token", nullable=false)
	@Required(message="The refresh_token could not be null")
	private String refreshToken;
	@Column(name="expires_in")
	private long expiresIn;

	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	public long getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
