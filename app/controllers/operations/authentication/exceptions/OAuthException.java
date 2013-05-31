package controllers.operations.authentication.exceptions;

public class OAuthException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2856418340943822252L;

	public OAuthException(String message) {
		super(message);
	}
	
	public OAuthException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
