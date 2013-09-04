package controllers;

import java.util.LinkedList;
import java.util.List;

import models.beans.dataObject.AccessToken;
import models.beans.dataObject.ServiceResources;
import models.db.OAuth1User;
import models.db.OAuth2User;
import models.db.notEntity.OAuthUser;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.user.index;
import controllers.enums.SESSION;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.factory.OAuthFactory;
import controllers.operations.persistence.PersistOAuthUser;

public class User extends Controller {

	@Authenticated
	@Transactional(readOnly=true)
	public static Result index()
	{
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
								routes.Authentication.connectToCallback(oauthToken.getProviderName()).absoluteURL(request()));
					if(oauthToken instanceof OAuth1User)
					{
						listServiceResources.add(
								oauthObject.getResources(
										new AccessToken(oauthToken.getId(), null, ((OAuth1User)oauthToken).getOauthToken(), ((OAuth1User)oauthToken).getOauthTokenSecret())));
					} else if(oauthToken instanceof OAuth2User)
					{
						listServiceResources.add(
								oauthObject.getResources(
										new AccessToken(oauthToken.getId(), null, ((OAuth2User)oauthToken).getAccessToken(), ((OAuth2User)oauthToken).getRefreshToken())));
					}
					
					// Save in the session cookie the information about the successful authentication with the resources provider.
					session(oauthToken.getProviderName(), "authenticated");
				} catch (InstantiationException | OAuthException e) {
					flash("error", e.getMessage());
//					return badRequest(index.render(listServiceResources));
				}
			}
		}
		
		return ok(index.render(listServiceResources));
	}
}
