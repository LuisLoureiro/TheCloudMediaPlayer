package controllers;

import java.util.LinkedList;
import java.util.List;

import controllers.enums.SESSION;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.factory.OAuthFactory;
import controllers.operations.persistence.PersistOAuthUser;

import models.authentication.AccessToken;
import models.beans.ServiceResources;
import models.db.OAuth1User;
import models.db.OAuth2User;
import models.db.notEntity.OAuthUser;
import play.db.jpa.Transactional;
import play.i18n.Lang;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.user.index;

public class User extends Controller {

	@Authenticated
	@Transactional
	public static Result index()
	{
		// Client preferred language
		// The accept languages are ordered by importance. The method returns the first language that matches an available language or the default application language.
		Lang lang = Lang.preferred(request().acceptLanguages());
		
		// Get the username from the session object.
		// Ask the data access layer for all the access tokens for this user.
		// Refresh some, if necessary.
		// Get the contents from all the available services.
		List<ServiceResources> listServiceResources = new LinkedList<ServiceResources>();
		
		List<OAuthUser> tokens = PersistOAuthUser.findAllAccessTokens(session(SESSION.USERNAME.toString()));
		if(tokens != null)
		{
			for(OAuthUser oauthToken : tokens)
			{
				try
				{
					IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(oauthToken.getProviderName(),
								routes.Authentication.connectToCallback(oauthToken.getProviderName()).absoluteURL(request()), lang);
					if(oauthToken instanceof OAuth1User)
					{
						listServiceResources.add(oauthObject.getResources(
								new AccessToken(oauthToken.getId(), null, ((OAuth1User)oauthToken).getOauthToken(), ((OAuth1User)oauthToken).getOauthTokenSecret())));
					} else if(oauthToken instanceof OAuth2User)
					{
						listServiceResources.add(oauthObject.getResources(
								new AccessToken(oauthToken.getId(), null, ((OAuth2User)oauthToken).getAccessToken(), ((OAuth2User)oauthToken).getRefreshToken())));
					}
				} catch (InstantiationException | OAuthException e) {
					flash("error", e.getMessage());
					return badRequest(index.render(null));
				}
			}
		}
		
		return ok(index.render(listServiceResources));
	}
}
