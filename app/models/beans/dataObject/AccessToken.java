package models.beans.dataObject;

/**
 * This class is used to combine the necessary information obtained from the OAuth provider after the exchange of the authorisation code/request token for an access token.<p>
 * Includes service provider user id, access_token or oauth_token, refresh_token or oauth_token_secret.<p>
 * TODO include the expires_in, scope, ...
 * 
 */
public class AccessToken
{
	private String uid
		, email
		, accessToken
		, refreshToken
		;
	private long expiresIn;

	public AccessToken(){}
	
	public AccessToken(String uid, String email, String accessToken, String refreshToken)
	{
		setUid(uid);
		setEmail(email);
		setAccessToken(accessToken);
		setRefreshToken(refreshToken);
	}

	/**
	 * 
	 * @return the user identifier of the OAuth provider.
	 */
	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * 
	 * @return the user email registered in the OAuth provider.
	 */
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * This value represents an access token when using an OAuth2 provider or an oauth token when using an OAuth1 provider.
	 * @return
	 */
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * This value represents a refresh token when using an OAuth2 provider or an oauth token secret when using an OAuth1 provider.
	 * @return
	 */
	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
	/**
	 * This value must represent the concrete expiration date based on the server date.
	 * @return
	 */
	public long getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(long expiresIn) {
		this.expiresIn = expiresIn;
	}
}
