package controllers.operations.authentication;

import models.authentication.AccessToken;
import models.beans.ServiceResources;
import controllers.operations.authentication.exceptions.OAuthException;


/**
 * 
 */
public interface IOAuth {

	/**
	 * @throws OAuthException if there was an error asking the OAuth provider for the authentication url.
	 * 
	 */
	public String getRequestToken(String callbackUrl) throws OAuthException;
	
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
	 * @throws OAuthException if there was an error asking the OAuth provider for the protected resources.
	 */
	public ServiceResources getResources(AccessToken accessToken) throws OAuthException;
}
