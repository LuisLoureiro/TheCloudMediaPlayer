package controllers;

import models.beans.dataObject.AccessToken;
import models.database.OAuth1User;
import models.database.OAuth2User;
import models.database.notEntity.OAuthUser;

import org.codehaus.jackson.node.ObjectNode;

import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import controllers.enums.SESSION;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.factory.OAuthFactory;
import controllers.operations.persistence.PersistOAuthUser;

public class Track extends Controller
{
	@Authenticated
	@Transactional
	public static Result getStreamUrl(String trackId) throws InstantiationException, OAuthException
	{
		// TODO check if the provider name exists in the providers enum.
		// TODO Use annotations
		String providerName = ctx()._requestHeader().getQueryString("providerName").get();
		if(providerName == null)
			return badRequest(Messages.get("errors.parametersMissing", "providerName"));
		
		// Find access token
		OAuthUser token = PersistOAuthUser.findByUserIdAndProviderName(session(SESSION.USERNAME.toString()), providerName);
		if(token == null)
			return badRequest(Messages.get("user.track.errors.noAccessTokenMatchesUserAndService"));
		
		IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(providerName, routes.Authentication.connectToCallback(providerName).absoluteURL(request()));
		
		AccessToken accessToken = new AccessToken(token.getId(), token.getEmail(), null, null);
		if(token instanceof OAuth1User)
		{
			accessToken.setAccessToken(((OAuth1User) token).getOauthToken());
			accessToken.setRefreshToken(((OAuth1User) token).getOauthTokenSecret());
		} else if(token instanceof OAuth2User)
		{
			accessToken.setAccessToken(((OAuth2User) token).getAccessToken());
			accessToken.setRefreshToken(((OAuth2User) token).getRefreshToken());
		} else
		{
			throw new InstantiationException("errors.oauthInstantiationException");
		}
		String location = oauthObject.getResourceStreamUrl(accessToken, trackId);
		
		// Return a json object with the url to use to stream this track.
		ObjectNode result = Json.newObject();
		result.put("url", location);
		return ok(result);
	}
}
