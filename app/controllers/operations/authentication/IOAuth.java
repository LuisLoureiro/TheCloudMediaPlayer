package controllers.operations.authentication;

import java.util.Map;

import models.beans.dataObject.AccessToken;
import models.beans.dataObject.ServiceResources;
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
	 * Verifies the HTTP request made as a response to the request token.<br/>
	 * This verification tries to find some information about a failed request token operation:<br/>
	 * <li>If the resource owner denied the access request or
	 * <li>if the request failed for reasons other than a missing or invalid redirection URI.
	 * 
	 * @param queryString the queryString to check for errors occurred during the request token operation.
	 * @return the expected request token if there're no errors in the queryString.
	 * @throws OAuthException if there's was an error during the request token operation.
	 */
	public String verifyCallbackRequest(Map<String, String[]> queryString) throws OAuthException;
	
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

	/**
	 * 
	 * @param trackId The id of the track to get the stream url.
	 * @return the stream url.
	 * @throws OAuthException if there was an error asking the OAuth provider for the stream url.
	 */
	public String getResourceStreamUrl(AccessToken accessToken, String trackId) throws OAuthException;
}
