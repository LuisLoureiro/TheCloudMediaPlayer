package controllers.operations.authentication.exceptions;

public class OAuth1TokenException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6060634860648187181L;

	private String message;

	public OAuth1TokenException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return this.message;
	}
}
