package controllers.operations.authentication.exceptions;

public class OAuthMissingRequiredParametersException extends OAuthException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5216955392995215917L;

	public OAuthMissingRequiredParametersException(String message, Throwable throwable)
	{
		super(message, throwable);
	}

	public OAuthMissingRequiredParametersException(String message)
	{
		super(message);
	}
}
