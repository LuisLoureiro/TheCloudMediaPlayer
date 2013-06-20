package controllers;

import org.codehaus.jackson.node.ObjectNode;

import models.authentication.AccessToken;
import models.db.OAuth1User;
import models.db.OAuth2User;
import models.db.notEntity.OAuthUser;
import controllers.enums.SESSION;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.factory.OAuthFactory;
import controllers.operations.persistence.PersistOAuthUser;
import play.db.jpa.Transactional;
import play.i18n.Lang;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;

public class Track extends Controller
{
	@Authenticated
	@Transactional
	public static Result getStreamUrl(String trackId)
	{
		// TODO check if the provider name exists in the providers enum.
		// TODO Use annotations
		String providerName = ctx()._requestHeader().getQueryString("providerName").get();
		if(providerName == null)
			return badRequest("The query string parameter 'providerName' is missing.");
		
		// Client preferred language
		// The accept languages are ordered by importance. The method returns the first language that matches an available language or the default application language.
		Lang lang = Lang.preferred(request().acceptLanguages());
		
		// Find access token
		OAuthUser token = PersistOAuthUser.findByUserIdAndProviderName(session(SESSION.USERNAME.toString()), providerName);
		if(token == null)
			return badRequest("There's no access token matching the track service the current user.");
		
		try {
			IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(providerName, routes.Authentication.connectToCallback(providerName).absoluteURL(request()), lang);
			
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
				throw new InstantiationException("OAuthUser object is not an instance of OAuth1User or OAuth2User.");
			}
			String location = oauthObject.getResourceStreamUrl(accessToken, trackId);
			
			// Return a json object with the url to use to stream this track.
			ObjectNode result = Json.newObject();
			result.put("url", location);
			return ok(result);
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError("Some unexpected error has occurred.");
		} catch (OAuthException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return internalServerError(e.getMessage());
		}
	}
}
