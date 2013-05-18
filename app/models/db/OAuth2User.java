package models.db;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.validation.constraints.NotNull;

@Entity(name="oauth2_users")
@PrimaryKeyJoinColumn(name="id", referencedColumnName="id")
@DiscriminatorValue(value="oauth2")
//@DiscriminatorOptions(force=true)
public class OAuth2User extends User{

//	@Id
//	private String id;
	@Column(name="access_token", nullable=false)
	@NotNull(message="The access_token could not be null")
	private String accessToken;
	@Column(name="refresh_token"/*, nullable=false*/)
//	@NotNull(message="The refresh_token could not be null")
	private String refreshToken;
	
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
	
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
}
