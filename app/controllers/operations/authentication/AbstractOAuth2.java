package controllers.operations.authentication;

import java.io.IOException;

import play.i18n.Lang;
import play.i18n.Messages;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.oauth2.model.Tokeninfo;

import controllers.operations.authentication.exceptions.OAuth2ValidationException;

public abstract class AbstractOAuth2 implements IOAuth2 {

	@Override
	public void validateToken(TokenResponse token, String userId, Lang lang) throws OAuth2ValidationException, IOException {
		Tokeninfo tokenInfo = isTokenValid(token);
		if(!isTokenForTheIntendedUser(tokenInfo, userId)) throw new OAuth2ValidationException(Messages.get(lang, "authentication.errors.oauthTokenIntendedUser"));
		if(!isTokenForOurApp(tokenInfo)) throw new OAuth2ValidationException(Messages.get(lang, "authentication.errors.oauthTokenIntendedApp"));
	}

	// ABSTRACT METHODS
	public abstract Tokeninfo isTokenValid(TokenResponse tokenResponse) throws OAuth2ValidationException, IOException;
	public abstract boolean isTokenForTheIntendedUser(Tokeninfo tokenInfo, String userId);
	public abstract boolean isTokenForOurApp(Tokeninfo tokenInfo);
}
