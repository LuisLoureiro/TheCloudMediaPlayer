package controllers.operations.authentication;

import java.io.IOException;

import com.google.api.client.auth.oauth2.TokenResponse; // TODO temporary. Try to create a new class to be able to use other authentication providers.

import controllers.operations.authentication.exceptions.OAuth2ValidationException;

public interface IOAuth2 {

	/**
	 * Exchange the authorization code for an access token and a refresh token.
	 * @throws IOException 
	 */
	public TokenResponse exchangeAuthCode(String authorizationCode) throws IOException;
	
	/**
	 * Check that the token is valid.
	 * Make sure the token we got is for the intended user. ??
	 * Make sure the token we got is for our app.
	 * 
	 * If the validation has errors an OAuth2ValidationException is thrown.
	 * @throws IOException 
	 */
	public void validateToken(TokenResponse token, String userId) throws OAuth2ValidationException, IOException;
}
