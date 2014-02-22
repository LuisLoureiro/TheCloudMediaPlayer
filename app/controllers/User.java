package controllers;

import static play.libs.Akka.future;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.llorieruo.projects.oauth2Login.OAuth2Factory;
import com.llorieruo.projects.oauth2Login.providers.IOAuth2;

import models.beans.dataObject.AccessToken;
import models.beans.dataObject.ServiceResources;
import models.database.OAuth1User;
import models.database.OAuth2User;
import models.database.notEntity.OAuthUser;
import play.api.mvc.RequestHeader;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.user.index;
import controllers.enums.SESSION;
import controllers.operations.authentication.GoogleOAuth2;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.factory.OAuthFactory;
import controllers.operations.exceptions.ApplicationOperationException;
import controllers.operations.persistence.PersistOAuthUser;
import controllers.operations.persistence.PersistUser;

public class User extends Controller {

	@Authenticated
	public static Result index()
	{
		final String username = session(SESSION.USERNAME.toString());
		final RequestHeader requestHeader = ctx()._requestHeader();
		final StringBuffer errorMessages = new StringBuffer();
		return async(future(new Callable<List<ServiceResources>>()
		{
			@Override
			public List<ServiceResources> call() throws Exception
			{
				try
				{
					return JPA.withTransaction("default", true, new Function0<List<ServiceResources>>()
					{
						@Override
						public List<ServiceResources> apply() throws Throwable
						{
							// Get the username from the session object.
							// Ask the data access layer for all the access tokens for this user.
							// Refresh some, if necessary.
							// Get the contents from all the available services.
							List<ServiceResources> listServiceResources = new LinkedList<ServiceResources>();
							List<OAuthUser> tokens = PersistOAuthUser.findAllAccessTokens(username);
							if(tokens != null)
							{
								for(OAuthUser oauthToken : tokens)
								{
									try
									{
										IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(oauthToken.getProviderName(),
													routes.Authentication.connectToCallback(oauthToken.getProviderName()).absoluteURL(false, requestHeader));
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
									} catch (InstantiationException | OAuthException e)
									{
										errorMessages.append(e.getMessage());
									}
								}
							}
							
							return listServiceResources;
						}
					});
				}
				catch(Throwable e1)
				{
					e1.printStackTrace();
					throw new Exception(e1);
				}
			}
		}).map(new Function<List<ServiceResources>, Result>()
		{
			@Override
			public Result apply(List<ServiceResources> resources) throws Throwable
			{
				// Save in the session cookie the information about the successfully authentication with the resources provider.
				for(ServiceResources serviceResources : resources)
				{
					session(serviceResources.getServiceName(), "authenticated");
				}
				if(errorMessages.length() != 0)
				{
					flash("error", errorMessages.toString());
				}
				return ok(index.render(resources));
			}
		}));
	}
	
	@Authenticated
	public static Result delete() throws ApplicationOperationException, InstantiationException, IOException
	{
		final String provider = session(SESSION.PROVIDER.toString());
		final String accessToken = session(SESSION.ACCESS_TOKEN.toString());
		final String username = session(SESSION.USERNAME.toString());
		return async(future(new Callable<String>()
		{
			@Override
			public String call() throws Exception
			{
				try
				{
					return JPA.withTransaction(new Function0<String>()
					{
						@Override
						public String apply() throws Throwable
						{
							String errorMessage = null;
							try
							{
								// TODO use factory!
								// OAuthFactory.getInstanceFromProviderName(session(SESSION.PROVIDER.toString()), null);
								if("google".equals(provider))
								{
									GoogleOAuth2 oauth2Object = new GoogleOAuth2();
									oauth2Object.revokeToken(accessToken);
								}
								else if("facebook".equals(provider))
								{
									IOAuth2 oauth2Instance = OAuth2Factory.getInstance("facebook");
									oauth2Instance.revokeToken(accessToken); // TODO Check that Facebook returned true. If false, warn user.
								}
							}
							catch(OAuthException | IOException ex)
							{
								errorMessage = ex.getMessage();
							}
							PersistUser.deleteUser(username);
							
							return errorMessage;
						}
					});
				}
				catch(Throwable e)
				{
					e.printStackTrace();
					throw new Exception(e);
				}
			}
		}).map(new Function<String, Result>()
		{
			@Override
			public Result apply(String errorMessage) throws Throwable
			{
				String successMessage = "user.account.deleted";
				session().clear();

				if(errorMessage != null)
				{
					flash("error", Messages.get(errorMessage));
				}
				else
				{
					flash("success", Messages.get(successMessage));
				}
				return noContent();
			}
		}));
	}
}
