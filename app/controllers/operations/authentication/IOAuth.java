package controllers.operations.authentication;

import java.util.Map;

import controllers.operations.authentication.exceptions.OAuth1TokenException;


/**
 * 
 */
public interface IOAuth {

	/**
	 * 
	 */
	public String getRequestToken(String callbackUrl);
	
	/**
	 * 
	 */
//	public void askUserAuthorisation();
	
	/**
	 * 
	 */
	public Map.Entry<String, String> exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuth1TokenException;
}
