package controllers.operations.authentication.exceptions;

public class OAuthMissingRequiredParametersException extends OAuthException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -5216955392995215917L;
    
    /**
	 * Creates a new instance of {@link OAuthMissingRequiredParametersException} with the translatable message 'authentication.errors.oauthMissingRequiredParam'.<br>
	 * Who catches this exception should translate this message before returning to the end-user.
	 */
	public OAuthMissingRequiredParametersException()
	{
		super("authentication.errors.oauthMissingRequiredParam");
	}
	
	public OAuthMissingRequiredParametersException(String message, Throwable throwable)
	{
		super(message, throwable);
	}

	public OAuthMissingRequiredParametersException(String message)
	{
		super(message);
	}
}
