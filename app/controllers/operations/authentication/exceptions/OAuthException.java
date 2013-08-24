package controllers.operations.authentication.exceptions;

import controllers.operations.exceptions.ApplicationOperationException;

public class OAuthException extends ApplicationOperationException {

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
