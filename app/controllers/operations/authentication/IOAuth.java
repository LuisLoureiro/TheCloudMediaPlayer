package controllers.operations.authentication;

import models.authentication.AccessToken;
import models.beans.ServiceResources;
import controllers.operations.authentication.exceptions.OAuthException;


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
	public AccessToken exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuthException;
	
	/**
	 * 
	 * @return The resources obtained from the oauth service.
	 */
	public ServiceResources getResources();
}
