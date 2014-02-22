package models.beans.dataBinding.form;


public class AuthorizationRequestObject
{
	private String	code, userId, userEmail;
	// Error
	private String	error, error_description/*, error_cause*/;
	
	public String getCode()
	{
		return code;
	}
	
	public void setCode(String code)
	{
		this.code = code;
	}

	public String getUserId()
	{
		return userId;
	}
	
	public void setUserId(String userId)
	{
		this.userId = userId;
	}
	
	public String getUserEmail()
	{
		return userEmail;
	}
	
	public void setUserEmail(String userEmail)
	{
		this.userEmail = userEmail;
	}

	public String getError()
	{
		return error;
	}

	public void setError(String error)
	{
		this.error = error;
	}

	public String getError_description()
	{
		return error_description;
	}

	public void setError_description(String error_description)
	{
		this.error_description = error_description;
	}
}
