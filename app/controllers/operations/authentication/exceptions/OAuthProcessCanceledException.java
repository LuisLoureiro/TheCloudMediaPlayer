package controllers.operations.authentication.exceptions;

public class OAuthProcessCanceledException extends OAuthException
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2950404326323185196L;

	/**
	 * Creates a new instance of {@link OAuthProcessCanceledException} with the translatable message 'authentication.errors.oauthProcessCanceled'.<br>
	 * Who catches this exception should translate this message before returning to the end-user.
	 */
	public OAuthProcessCanceledException()
	{
		super("authentication.errors.oauthProcessCanceled");
	}
	
	public OAuthProcessCanceledException(String message)
	{
		super(message);
	}

	public OAuthProcessCanceledException(String message, Throwable throwable)
	{
		super(message, throwable);
	}
}
