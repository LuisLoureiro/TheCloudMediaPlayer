package controllers.operations.authentication.exceptions;

public class OAuth1TokenException extends OAuthException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6060634860648187181L;

	public OAuth1TokenException(String message) {
		super(message);
	}
	
	public OAuth1TokenException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
