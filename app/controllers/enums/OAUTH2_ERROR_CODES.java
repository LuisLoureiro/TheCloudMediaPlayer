package controllers.enums;

public enum OAUTH2_ERROR_CODES
{
	invalid_request("authentication.errors.oauthInvalidRequest")
	, unauthorized_client("authentication.errors.oauthUnauthorizedClient")
	, access_denied("authentication.errors.oauthAccessDenied")
	, unsupported_response_type("authentication.errors.oauthUnsupportedResponseType")
	, invalid_scope("authentication.errors.oauthInvalidScope")
	, server_error("authentication.errors.oauthServerError")
	, temporarily_unavailable("authentication.errors.oauthTemporarilyUnavailable");
	
	private String message;
	private OAUTH2_ERROR_CODES(String message)
	{
		this.message = message;
	}
	public String getMessage()
	{
		return message;
	}
}
