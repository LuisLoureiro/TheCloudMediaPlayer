package controllers;

import static play.libs.Akka.future;

import java.util.concurrent.Callable;

import models.beans.dataObject.AccessToken;
import models.beans.dataObject.AsyncActionRecoverErrorObject;
import models.beans.dataObject.AsyncActionRecoverObject;
import models.database.OAuth1User;
import models.database.OAuth2User;
import models.database.notEntity.OAuthUser;

import org.codehaus.jackson.node.ObjectNode;

import play.db.jpa.JPA;
import play.i18n.Messages;
import play.libs.F.Function;
import play.libs.F.Function0;
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
	public static Result getStreamUrl(final String trackId) throws InstantiationException, OAuthException
	{
		// TODO check if the provider name exists in the providers enum.
		// TODO Use annotations
		final String providerName = ctx()._requestHeader().getQueryString("providerName").get();
		if(providerName == null)
			return badRequest(Messages.get("errors.parametersMissing", "providerName"));
		
		final String callbackUrl = routes.Authentication.connectToCallback(providerName).absoluteURL(request());
		final String username = session(SESSION.USERNAME.toString());
		
		return async(future(new Callable<AsyncActionRecoverObject<String>>()
		{
			@Override
			public AsyncActionRecoverObject<String> call() throws Exception
			{
				try
				{
					return JPA.withTransaction("default", true, new Function0<AsyncActionRecoverObject<String>>()
					{
						@Override
						public AsyncActionRecoverObject<String> apply() throws Throwable
						{
							AsyncActionRecoverObject<String> returnObject = new AsyncActionRecoverObject<String>();
							
							// Find access token
							final OAuthUser token = PersistOAuthUser.findByUserIdAndProviderName(username, providerName);
							if(token == null)
							{
								AsyncActionRecoverErrorObject error = new AsyncActionRecoverErrorObject();
								error.setResponseStatus(BAD_REQUEST);
								error.setMessage("user.track.errors.noAccessTokenMatchesUserAndService");
								returnObject.setError(true);
								returnObject.setActionError(error);
								return returnObject;
							}
							
							IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(providerName, callbackUrl);
							
							AccessToken accessToken = new AccessToken(token.getId(), token.getEmail(), null, null);
							if(token instanceof OAuth1User)
							{
								accessToken.setAccessToken(((OAuth1User) token).getOauthToken());
								accessToken.setRefreshToken(((OAuth1User) token).getOauthTokenSecret());
							}
							else if(token instanceof OAuth2User)
							{
								accessToken.setAccessToken(((OAuth2User) token).getAccessToken());
								accessToken.setRefreshToken(((OAuth2User) token).getRefreshToken());
							}
							else
							{
								throw new InstantiationException("errors.oauthInstantiationException");
							}
							returnObject.setData(oauthObject.getResourceStreamUrl(accessToken, trackId));
							return returnObject;
						}
					});
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					throw new Exception(e);
				}
			}
		}).map(new Function<AsyncActionRecoverObject<String>, Result>()
		{
			@Override
			public Result apply(AsyncActionRecoverObject<String> streamUrl) throws Throwable
			{
				if(streamUrl.isError())
				{
					return status(streamUrl.getActionError().getResponseStatus(), Messages.get(streamUrl.getActionError().getMessage()));
				}
				// Return a json object with the url to use to stream this track.
				ObjectNode result = Json.newObject();
				result.put("url", streamUrl.getData());
				return ok(result);
			}
		}));
	}
}
