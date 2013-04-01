package controllers.operations.authentication.exceptions;

public class OAuth2ValidationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2869186162947024805L;
	private String message;

	public OAuth2ValidationException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
