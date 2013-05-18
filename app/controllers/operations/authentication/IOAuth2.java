package controllers.operations.authentication;

import java.io.IOException;

import play.i18n.Lang;

import com.google.api.client.auth.oauth2.TokenResponse; // TODO temporary. Try to create a new class to be able to use other authentication providers.

import controllers.operations.authentication.exceptions.OAuth2ValidationException;

public interface IOAuth2 {

	/**
	 * Exchange the authorisation code for an access token and a refresh token.
	 * @throws IOException 
	 */
	public TokenResponse exchangeAuthCode(String authorizationCode) throws IOException;
	
	/**
	 * Check that the token is valid.
	 * <p>
	 * Make sure the token we got is for the intended user.
	 * <p>
	 * Make sure the token we got is for our app.
	 * <p>
	 * If the validation has errors an OAuth2ValidationException is thrown.
	 * @param token
	 * @param userId
	 * @throws OAuth2ValidationException
	 * @throws IOException 
	 */
	public void validateToken(TokenResponse token, String userId, Lang lang) throws OAuth2ValidationException, IOException;
}
