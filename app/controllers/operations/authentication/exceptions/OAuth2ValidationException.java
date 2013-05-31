package controllers.operations.authentication.exceptions;

public class OAuth2ValidationException extends OAuthException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2869186162947024805L;

	public OAuth2ValidationException(String message) {
		super(message);
	}
	
	public OAuth2ValidationException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
