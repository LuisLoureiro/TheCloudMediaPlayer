package models.beans.dataObject;

public class AuthenticatedUser
{
	private String	userId, userName, userEmail, accessToken, refreshToken, providerName;

	public String getUserId()
	{
		return userId;
	}
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	public String getUserName()
	{
		return userName;
	}
	public void setUserName(String userName)
	{
		this.userName = userName;
	}
	public String getUserEmail()
	{
		return userEmail;
	}
	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}
	public String getAccessToken()
	{
		return accessToken;
	}
	public void setAccessToken(String accessToken)
	{
		this.accessToken = accessToken;
	}
	public String getRefreshToken()
	{
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken)
	{
		this.refreshToken = refreshToken;
	}
	public String getProviderName()
	{
		return providerName;
	}
	public void setProviderName(String providerName)
	{
		this.providerName = providerName;
	}
}
