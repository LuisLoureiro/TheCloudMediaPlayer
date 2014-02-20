package controllers;

import static play.libs.Akka.future;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import models.beans.dataBinding.form.ExchangeCodeForm;
import models.beans.dataBinding.form.OpenIDUser;
import models.beans.dataObject.AccessToken;
import models.beans.dataObject.AsyncActionRecoverErrorObject;
import models.beans.dataObject.AsyncActionRecoverObject;
import models.beans.dataObject.AuthenticatedUser;

import org.codehaus.jackson.node.ObjectNode;

import play.api.libs.openid.Errors;
import play.data.Form;
import play.db.jpa.JPA;
import play.i18n.Messages;
import play.libs.F.Callback0;
import play.libs.F.Function;
import play.libs.F.Function0;
import play.libs.Json;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.authentication.index;

import com.google.api.client.auth.oauth2.TokenResponse;

import controllers.enums.SESSION;
import controllers.operations.authentication.GoogleOAuth2;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.enums.OPENID_ATTRIBUTES;
import controllers.operations.authentication.exceptions.OAuth2ValidationException;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.exceptions.OpenIDException;
import controllers.operations.authentication.factory.OAuthFactory;
import controllers.operations.persistence.PersistOAuth2User;
import controllers.operations.persistence.PersistOAuthUser;
import controllers.operations.persistence.PersistUser;

public class Authentication extends Controller
{
	
	public static Result index()
	{
    	Form<OpenIDUser> openIDForm = Form.form(OpenIDUser.class);
        return ok(index.render(openIDForm));
	}

	public static Result openID() throws OpenIDException
	{
    	final Form<OpenIDUser> openIDUserForm = Form.form(OpenIDUser.class).bindFromRequest();
    	if(openIDUserForm.hasErrors()) {
    		return badRequest(index.render(openIDUserForm));
    	}
    	final OpenIDUser user = openIDUserForm.get();
    	final String callbackUrl = routes.Authentication.openIDCallback().absoluteURL(request());
    	
    	return async(future(new Callable<AsyncActionRecoverObject<String>>()
    	{
			@Override
			public AsyncActionRecoverObject<String> call() throws Exception
			{
				AsyncActionRecoverObject<String> returnObject = new AsyncActionRecoverObject<String>();
				
				// Additional attributes
				Map<String, String> attributes = new HashMap<String, String>();
				attributes.put(OPENID_ATTRIBUTES.EMAIL.getName(), OPENID_ATTRIBUTES.EMAIL.getUri());
				attributes.put(OPENID_ATTRIBUTES.EMAIL_AX.getName(), OPENID_ATTRIBUTES.EMAIL_AX.getUri());
				attributes.put(OPENID_ATTRIBUTES.EMAIL_OP.getName(), OPENID_ATTRIBUTES.EMAIL_OP.getUri());
				attributes.put(OPENID_ATTRIBUTES.FULL_NAME_AX.getName(), OPENID_ATTRIBUTES.FULL_NAME_AX.getUri());
				attributes.put(OPENID_ATTRIBUTES.FRIENDLY_NAME.getName(), OPENID_ATTRIBUTES.FRIENDLY_NAME.getUri());
				attributes.put(OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getName(), OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getUri());
				
				// If the OpenID is invalid, an exception is thrown.
				returnObject.setData(OpenID.redirectURL(user.getOpenid_identifier(), callbackUrl, attributes).get());
				return returnObject;
			}
    	}).recover(new Function<Throwable, AsyncActionRecoverObject<String>>()
		{
			@Override
			public AsyncActionRecoverObject<String> apply(Throwable ex) throws Throwable
			{
				ex.printStackTrace();

				AsyncActionRecoverErrorObject recoverError = new AsyncActionRecoverErrorObject();
				
				AsyncActionRecoverObject<String> returnObject = new AsyncActionRecoverObject<String>();
				returnObject.setError(true);
				returnObject.setActionError(recoverError);
				
				try
				{
					throw ex;
				}
				catch(ConnectException | URISyntaxException e)
				{
					recoverError.setMessage("authentication.errors.openIdUriSyntax");
					recoverError.setResponseStatus(BAD_REQUEST);
				}
				catch(Errors.NETWORK_ERROR$ e)
				{
					recoverError.setMessage("authentication.errors.openIdDiscovery");
					recoverError.setResponseStatus(BAD_REQUEST);
				}
				catch(TimeoutException e)
				{
					recoverError.setMessage("authentication.errors.openIdVerificationTimeout");
					recoverError.setResponseStatus(GATEWAY_TIMEOUT);
				}
				catch(Throwable e)
				{
					throw new OpenIDException("authentication.errors.openIdUnexpected");
				}
				
				return returnObject;
			}
		}).map(new Function<AsyncActionRecoverObject<String>, Result>()
		{
			@Override
			public Result apply(AsyncActionRecoverObject<String> redirectUrl) throws Exception
			{
				if(redirectUrl.isError())
				{
					flash("error", Messages.get(redirectUrl.getActionError().getMessage()));
					return status(redirectUrl.getActionError().getResponseStatus(), index.render(openIDUserForm));
				}
				return redirect(redirectUrl.getData());
			}
		}));
	}

	public static Result openIDCallback() throws OpenIDException
	{
		try
		{
			// It's not possible to return an AsyncResult because OpenID.verifiedId needs the current HTTP context which is null when the Future instance is executed.
			// If the information is not correct or if the server check is false (for example if the redirect URL has been forged), the returned Promise will be a Thrown.
			final UserInfo info = OpenID.verifiedId().get(10000L); // default is 5000. It may be too short.
			return async(future(new Callable<AuthenticatedUser>()
			{
				@Override
				public AuthenticatedUser call() throws Exception
				{
					try
					{
						return JPA.withTransaction(new Function0<AuthenticatedUser>()
						{
							@Override
							public AuthenticatedUser apply() throws Throwable
							{
								AuthenticatedUser user = new AuthenticatedUser();
								// Additional attributes
								Map<String, String> attributes = info.attributes;
								String name = 
										attributes.containsKey(OPENID_ATTRIBUTES.FULL_NAME_AX.getName()) ? attributes.get(OPENID_ATTRIBUTES.FULL_NAME_AX.getName())
											: attributes.containsKey(OPENID_ATTRIBUTES.FULL_NAME_AX.getNameWithIndex()) ? attributes.get(OPENID_ATTRIBUTES.FULL_NAME_AX.getNameWithIndex())
												: attributes.containsKey(OPENID_ATTRIBUTES.FRIENDLY_NAME.getName()) ? attributes.get(OPENID_ATTRIBUTES.FRIENDLY_NAME.getName())
													: attributes.containsKey(OPENID_ATTRIBUTES.FRIENDLY_NAME.getNameWithIndex()) ? attributes.get(OPENID_ATTRIBUTES.FRIENDLY_NAME.getNameWithIndex())
														: attributes.containsKey(OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getName()) ? attributes.get(OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getName())
															: attributes.containsKey(OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getNameWithIndex()) ? attributes.get(OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getNameWithIndex())
																: info.id;
								String email = attributes.containsKey(OPENID_ATTRIBUTES.EMAIL.getName()) ? attributes.get(OPENID_ATTRIBUTES.EMAIL.getName())
										: attributes.containsKey(OPENID_ATTRIBUTES.EMAIL.getNameWithIndex()) ? attributes.get(OPENID_ATTRIBUTES.EMAIL.getNameWithIndex()) : "";
								
								// Set object's properties with verified user information.
								user.setProviderName("openID");
								user.setUserEmail(email);
								user.setUserId(info.id);
								user.setUserName(name);
								
								// save the openid user if don't exists.
								PersistUser.saveUser(user.getUserId(), user.getUserEmail());
								
								return user;
							}
						});
					}
					catch(Throwable e)
					{
						throw new Exception(e);
					}
				}
			}).map(new Function<AuthenticatedUser, Result>()
			{
				@Override
				public Result apply(AuthenticatedUser user) throws Throwable
				{
					// Save user info in the session
					session(SESSION.USERNAME.toString(), user.getUserId());
					session(SESSION.FULL_NAME.toString(), user.getUserName());
					session(SESSION.EMAIL.toString(), user.getUserEmail());
					// Store the authentication provider. See app/views/user/index.scala.html
					session(SESSION.PROVIDER.toString(), user.getProviderName());
					
					// redirect to the user home page
					flash("success", Messages.get("authentication.signedIn"));
					return redirect(routes.User.index().absoluteURL(request()));
				}
			}));
		}
		catch(Throwable ex)
		{
			ex.printStackTrace();
			
			Form<OpenIDUser> form = Form.form(OpenIDUser.class);
			if(ex.getClass().equals(TimeoutException.class))
			{
				flash("error", Messages.get("authentication.errors.openIdVerificationTimeout"));
				return status(GATEWAY_TIMEOUT, index.render(form));
			}
			if(ex instanceof Errors.AUTH_CANCEL$)
			{
				flash("error", Messages.get("authentication.errors.openIdProcessCanceled"));
				return badRequest(index.render(form));
			}
			throw new OpenIDException("authentication.errors.openIdVerificationUnexpected");
		}
	}

	public static Result exchangeCodeWithAccessToken() throws OAuthException
	{
		Form<ExchangeCodeForm> exchangeCodeform = Form.form(ExchangeCodeForm.class).bindFromRequest();
    	if(exchangeCodeform.hasErrors())
			throw new IllegalStateException("authentication.errors.oauthMissingParams");
    	
    	final ExchangeCodeForm exchangeCode = exchangeCodeform.get();
		// Capture the name of the provider used to authenticate.
		final String providerName = request().getQueryString("provider");
		
		return async(future(new Callable<AsyncActionRecoverObject<AuthenticatedUser>>()
		{
			@Override
			public AsyncActionRecoverObject<AuthenticatedUser> call() throws Exception
			{
				try
				{
					return JPA.withTransaction(new Function0<AsyncActionRecoverObject<AuthenticatedUser>>()
					{
						@Override
						public AsyncActionRecoverObject<AuthenticatedUser> apply() throws Throwable
						{
							AuthenticatedUser authenticatedUser = new AuthenticatedUser();
							AsyncActionRecoverObject<AuthenticatedUser> returnObject = new AsyncActionRecoverObject<AuthenticatedUser>();
							returnObject.setData(authenticatedUser);

							// Create an OAuth2 object based on the OAuth2 authentication provider used by the user using the factory method pattern.
							GoogleOAuth2 oauth2Object = new GoogleOAuth2();
							// exchange the code with an access token
							TokenResponse token = oauth2Object.exchangeAuthCode(exchangeCode.getCode());
							// validate token
							oauth2Object.validateToken(token, exchangeCode.getUserId());
							
							// save the access token and the refresh token
							PersistOAuth2User.saveUser(providerName,
									new AccessToken(exchangeCode.getUserId(), exchangeCode.getUserEmail(), token.getAccessToken(), token.getRefreshToken()),
									"email", exchangeCode.getUserEmail());
							
							authenticatedUser.setAccessToken(token.getAccessToken());
							authenticatedUser.setProviderName(providerName);
							authenticatedUser.setUserEmail(exchangeCode.getUserEmail());
							authenticatedUser.setUserId(exchangeCode.getUserId());
							authenticatedUser.setUserName(exchangeCode.getUserName());
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
		}).recover(new Function<Throwable, AsyncActionRecoverObject<AuthenticatedUser>>()
		{
			@Override
			public AsyncActionRecoverObject<AuthenticatedUser> apply(Throwable ex) throws Throwable
			{
				ex.printStackTrace();

				AsyncActionRecoverErrorObject recoverError = new AsyncActionRecoverErrorObject();
				
				AsyncActionRecoverObject<AuthenticatedUser> returnObject = new AsyncActionRecoverObject<AuthenticatedUser>();
				returnObject.setError(true);
				returnObject.setActionError(recoverError);
				
				try
				{
					throw ex;
				}
				catch(OAuth2ValidationException e)
				{
					recoverError.setMessage(e.getMessage());
					recoverError.setResponseStatus(FORBIDDEN);
				}
				catch(IOException e)
				{
					recoverError.setMessage(e.getMessage());
					recoverError.setResponseStatus(BAD_GATEWAY);
				}
				catch(IllegalStateException e)
				{
					recoverError.setMessage(e.getMessage());
					recoverError.setResponseStatus(BAD_REQUEST);
				}
				catch(Throwable e)
				{
					throw new OAuthException("authentication.errors.oauthExchangeCodeUnexpected", ex);
				}
				return returnObject;
			}
		}).map(new Function<AsyncActionRecoverObject<AuthenticatedUser>, Result>()
		{
			@Override
			public Result apply(AsyncActionRecoverObject<AuthenticatedUser> user) throws Throwable
			{
				if(user.isError())
				{
					Form<OpenIDUser> form = Form.form(OpenIDUser.class);
					flash("error", Messages.get(user.getActionError().getMessage()));
					return status(user.getActionError().getResponseStatus(), index.render(form));
				}
				
				AuthenticatedUser userData = user.getData();
				
				// Save user info in the session
				session(SESSION.USERNAME.toString(), userData.getUserId());
				session(SESSION.FULL_NAME.toString(), userData.getUserName());
				session(SESSION.EMAIL.toString(), userData.getUserEmail());
			    // Store the token in the session for later use.
				session(SESSION.ACCESS_TOKEN.toString(), userData.getAccessToken());
				// Store the authentication provider. See app/views/user/index.scala.html
				session(SESSION.PROVIDER.toString(), userData.getProviderName());
				
				// redirect to the user home page
				flash("success", Messages.get("authentication.signedIn"));
				
				// Content negotiation
				String returnURL = routes.User.index().absoluteURL(request());
				if (request().accepts("text/json") || request().accepts("application/json"))
				{
					// Return a json object with the url to redirect to.
					ObjectNode result = Json.newObject();
					result.put("url", returnURL);
					return ok(result);
				}
				return redirect(returnURL);
			}
		}));
	}
	
	@Authenticated
	// As the routes file doesn't define the parameter as optional the provider is always required. An exception is thrown before reaching this action.
	public static Result connectTo(final String provider) throws InstantiationException, OAuthException
	{
		final String callbackUrl = routes.Authentication.connectToCallback(provider).absoluteURL(request());
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
							IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(provider, callbackUrl);
							
							// Get request token
							return oauthObject.getRequestToken(callbackUrl); // TODO remove this parameter. Use the one provided to the constructor.
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
			public Result apply(String redirectUrl) throws Throwable
			{
				// Redirect to the authentication url
				return redirect(redirectUrl);
			}
		}));
	}
	
	@Authenticated
	// As the routes file doesn't define the parameter as optional the provider is always required. An exception is thrown before reaching this action.
	public static Result connectToCallback(final String provider) throws InstantiationException, OAuthException
	{
		final String callbackUrl = routes.Authentication.connectToCallback(provider).absoluteURL(request());
		final Map<String, String[]> queryString = request().queryString();
		final String username = session(SESSION.USERNAME.toString());
		
		return async(future(new Callable<Void>()
		{
			@Override
			public Void call() throws Exception
			{
				JPA.withTransaction(new Callback0()
				{
					@Override
					public void invoke() throws Throwable
					{
						// Create the object that represents the service
						IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(provider, callbackUrl);
						
						// Verify parameters
						String requestToken = oauthObject.verifyCallbackRequest(queryString);
						
						// Get oauth_token_secret and oauth_token
						AccessToken token = oauthObject.exchangeRequestTokenForAnAccessToken(requestToken);
						
						// If the user doesn't exists, insert in the database and create relationship
						PersistOAuthUser.saveUser(provider, token, "id", username);
					}
				});
				
				return null;
			}
		}).map(new Function<Void, Result>()
		{
			@Override
			public Result apply(Void arg) throws Throwable
			{
				return redirect(routes.User.index());
			}
		}));
	}
	
	@Authenticated
	public static Result signOut()
	{
		session().clear();
		
		// Redirect to the index page
		flash("success", Messages.get("authentication.signedOut"));
		return redirect(routes.Application.index());
	}
}
