package controllers.operations.authentication;

import models.authentication.AccessToken;
import controllers.operations.authentication.exceptions.OAuth1TokenException;
import controllers.operations.authentication.exceptions.OAuthException;


/**
 * The OAuth v1 flow has three parts:
 *
 * Get a request token.
 *  Step 1 of authentication. Obtain an OAuth request token to be used for the rest of the authentication process.
 *  This method corresponds to Obtaining an Unauthorised Request Token (http://oauth.net/core/1.0/#auth_step1) in the OAuth Core 1.0 specification.
 *  
 * Ask the user to authorise linking your app to their Dropbox account.
 *  Step 2 of authentication. Applications should direct the user to /oauth/authorize.
 *  This isn't an API call per se, but rather a web endpoint that lets the user sign in to Dropbox and choose whether to grant the application the ability to access files on their behalf.
 *  The page served by /oauth/authorize should be presented to the user through their web browser. Without the user's authorisation in this step, 
 *   it isn't possible for your application to obtain an access token from /oauth/access_token.
 *  This method corresponds to Obtaining User (http://oauth.net/core/1.0/#auth_step2) in the OAuth Core 1.0 specification.
 *  
 * Once authorised, exchange the request token for an access token, which will be used for calling the Core API.
 *  Step 3 of authentication. After the /oauth/authorize step is complete, the application can call /oauth/access_token to acquire an access token.
 *  This method corresponds to Obtaining an Access Token (http://oauth.net/core/1.0/#auth_step3) in the OAuth Core 1.0 specification.
 * 
 * Following the Core API - REST Reference.
 */
public interface IOAuth1 extends IOAuth {

	/**
	 * Get a request token.<p>
	 *  Step 1 of authentication. Obtain an OAuth request token to be used for the rest of the authentication process.<p>
	 *  This method corresponds to Obtaining an Unauthorised Request Token (http://oauth.net/core/1.0/#auth_step1) in the OAuth Core 1.0 specification.<p>
	 *  <p>
	 * The request token pair must be saved in the database.
	 *  
	 *  @param callbackUrl the URL of the callback action to handle the authorisation process.
	 *  @return the URL to redirect the user to. 
	 * @throws OAuthException if there was an error asking the OAuth1 provider for the authentication url.
	 */
	public String getRequestToken(String callbackUrl) throws OAuthException;
	
	/**
	 * Ask the user to authorise linking your app to their Dropbox account.<p>
	 *  Step 2 of authentication. Applications should direct the user to /oauth/authorize.<p>
	 *  This isn't an API call per se, but rather a web endpoint that lets the user sign in to Dropbox 
	 *   and choose whether to grant the application the ability to access files on their behalf.<p>
	 *  The page served by /oauth/authorize should be presented to the user through their web browser. Without the user's authorisation in this step, 
	 *   it isn't possible for your application to obtain an access token from /oauth/access_token.<p>
	 *  This method corresponds to Obtaining User (http://oauth.net/core/1.0/#auth_step2) in the OAuth Core 1.0 specification.
	 */
//	public void askUserAuthorisation();
	
	/**
	 * Once authorised, exchange the request token for an access token, which will be used for calling the Core API.<p>
	 *  Step 3 of authentication. After the /oauth/authorize step is complete, the application can call /oauth/access_token to acquire an access token.<p>
	 *  This method corresponds to Obtaining an Access Token (http://oauth.net/core/1.0/#auth_step3) in the OAuth Core 1.0 specification.
	 *  
	 * @param requestToken The token used to initiate the authorisation process.
	 * @return the service user id, the oauth token used to access protected resources and the oauth token secret. Both must be persisted.
	 * @throws OAuth1TokenException if the provided token is inexistent.
	 */
	public AccessToken exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuth1TokenException;
}
