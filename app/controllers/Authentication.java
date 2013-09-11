package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import models.beans.dataBinding.form.OpenIDUser;
import models.beans.dataObject.AccessToken;

import org.codehaus.jackson.node.ObjectNode;

import play.api.libs.openid.Errors;
import play.data.Form;
import play.db.jpa.Transactional;
import play.i18n.Messages;
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

public class Authentication extends Controller {
	
	public static Result index()
	{
    	Form<OpenIDUser> openIDForm = Form.form(OpenIDUser.class);
        return ok(index.render(openIDForm));
	}

	public static Result openID() throws OpenIDException
	{
    	Form<OpenIDUser> openIDUserForm = Form.form(OpenIDUser.class).bindFromRequest();
    	if(openIDUserForm.hasErrors()) {
    		return badRequest(index.render(openIDUserForm));
    	}
    	OpenIDUser user = openIDUserForm.get();
    	
		try
		{
			// Additional attributes
			Map<String, String> attributes = new HashMap<String, String>();
			attributes.put(OPENID_ATTRIBUTES.EMAIL.getName(), OPENID_ATTRIBUTES.EMAIL.getUri());
			attributes.put(OPENID_ATTRIBUTES.EMAIL_AX.getName(), OPENID_ATTRIBUTES.EMAIL_AX.getUri());
			attributes.put(OPENID_ATTRIBUTES.EMAIL_OP.getName(), OPENID_ATTRIBUTES.EMAIL_OP.getUri());
			attributes.put(OPENID_ATTRIBUTES.FULL_NAME_AX.getName(), OPENID_ATTRIBUTES.FULL_NAME_AX.getUri());
			attributes.put(OPENID_ATTRIBUTES.FRIENDLY_NAME.getName(), OPENID_ATTRIBUTES.FRIENDLY_NAME.getUri());
			attributes.put(OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getName(), OPENID_ATTRIBUTES.FRIENDLY_NAME_OP.getUri());
			// TODO with too many attributes there's a need to handle POST and GET requests.
			// Let's ask only for the friendly/full name to avoid Errors.BAD_REQUEST$ when verifying the response.
			// Handle this flaw later.
//			attributes.put(OPENID_ATTRIBUTES.FIRST_NAME.getName(), OPENID_ATTRIBUTES.FIRST_NAME.getUri());
//			attributes.put(OPENID_ATTRIBUTES.FIRST_NAME_AX.getName(), OPENID_ATTRIBUTES.FIRST_NAME_AX.getUri());
//			attributes.put(OPENID_ATTRIBUTES.FIRST_NAME_OP.getName(), OPENID_ATTRIBUTES.FIRST_NAME_OP.getUri());
//			attributes.put(OPENID_ATTRIBUTES.LAST_NAME.getName(), OPENID_ATTRIBUTES.LAST_NAME.getUri());
//			attributes.put(OPENID_ATTRIBUTES.LAST_NAME_AX.getName(), OPENID_ATTRIBUTES.LAST_NAME_AX.getUri());
//			attributes.put(OPENID_ATTRIBUTES.LAST_NAME_OP.getName(), OPENID_ATTRIBUTES.LAST_NAME_OP.getUri());
			
			// If the OpenID is invalid, an exception is thrown.
			return redirect(
					OpenID.redirectURL(user.getOpenid_identifier(), routes.Authentication.openIDCallback().absoluteURL(request()), attributes)
					.get());
		} catch(Throwable ex) {
			ex.printStackTrace();
			
			Throwable exCause = ex.getCause();
			if(exCause != null && exCause.getClass().equals(URISyntaxException.class)) {
				flash("error", Messages.get("authentication.errors.openIdUriSyntax"));
				return badRequest(index.render(openIDUserForm));
			}
			if(ex instanceof Errors.NETWORK_ERROR$) {
				flash("error", Messages.get("authentication.errors.openIdDiscovery"));
				return badRequest(index.render(openIDUserForm));
			}
			if(ex.getClass().equals(TimeoutException.class)) {
				flash("error", Messages.get("authentication.errors.openIdVerificationTimeout"));
				return status(GATEWAY_TIMEOUT, index.render(openIDUserForm));
			}
			throw new OpenIDException("authentication.errors.openIdUnexpected");
		}
	}

	@Transactional
	public static Result openIDCallback() throws OpenIDException {
		// If the information is not correct or if the server check is false (for example if the redirect URL has been forged), the returned Promise will be a Thrown.
		try
		{
			UserInfo info = OpenID.verifiedId().get(10000L); // default is 5000. It may be too short.
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

			// save the openid user if don't exists.
			PersistUser.saveUser(info.id, email);
			
			// Save user info in the session
			session(SESSION.USERNAME.toString(), info.id);
			session(SESSION.FULL_NAME.toString(), name);
			session(SESSION.EMAIL.toString(), email);
			// Store the authentication provider. See app/views/user/index.scala.html
			session(SESSION.PROVIDER.toString(), "openID");
			
			// redirect to the user home page
			flash("success", Messages.get("authentication.signedIn"));
			return redirect(routes.User.index().absoluteURL(request()));
		} catch(Throwable ex) {
			ex.printStackTrace();
			
			Form<OpenIDUser> form = Form.form(OpenIDUser.class);
			if(ex.getClass().equals(TimeoutException.class)) {
				flash("error", Messages.get("authentication.errors.openIdVerificationTimeout"));
				return status(GATEWAY_TIMEOUT, index.render(form));
			}
			if(ex instanceof Errors.AUTH_CANCEL$) {
				flash("error", Messages.get("authentication.errors.openIdProcessCanceled"));
				return badRequest(index.render(form));
			}
			throw new OpenIDException("authentication.errors.openIdVerificationUnexpected");
		}
	}

	@Transactional
	public static Result exchangeCodeWithAccessToken() throws OAuthException
	{
		try
		{
			Map<String, String[]> body = request().body().asFormUrlEncoded();
			if(!body.containsKey("code") || !body.containsKey("userId")) {
				throw new IllegalStateException("authentication.errors.oauthMissingParams");
			}
			// Capture the name of the provider used to authenticate.
			String providerName = request().getQueryString("provider")
					,userId = body.get("userId")[0]
					,userEmail = body.containsKey("userEmail") ? body.get("userEmail")[0] : ""
					,userName = body.containsKey("userName") ? body.get("userName")[0] : "";

			// Create an OAuth2 object based on the OAuth2 authentication provider used by the user using the factory method pattern.
			GoogleOAuth2 oauth2Object = new GoogleOAuth2();
			// exchange the code with an access token
			TokenResponse token = oauth2Object.exchangeAuthCode(body.get("code")[0]);
			// validate token
			oauth2Object.validateToken(token, userId);
			
			// save the access token and the refresh token
			PersistOAuth2User.saveUser(providerName, new AccessToken(userId, userEmail, token.getAccessToken(), token.getRefreshToken()), "email", userEmail);
			
			// Save user info in the session
			session(SESSION.USERNAME.toString(), userId);
			session(SESSION.FULL_NAME.toString(), userName);
			session(SESSION.EMAIL.toString(), userEmail);
		    // Store the token in the session for later use.
			session(SESSION.ACCESS_TOKEN.toString(), token.getAccessToken());
			// Store the authentication provider. See app/views/user/index.scala.html
			session(SESSION.PROVIDER.toString(), providerName);
			
			// redirect to the user home page
			flash("success", Messages.get("authentication.signedIn"));
			
			// Content negotiation
			String returnURL = routes.User.index().absoluteURL(request());
			if (request().accepts("text/json") || request().accepts("application/json")) {
				// Return a json object with the url to redirect to.
				ObjectNode result = Json.newObject();
				result.put("status", "OK");
				result.put("url", returnURL);
				return ok(result);
			}
			return redirect(returnURL);
		} catch(Exception ex) {
			ex.printStackTrace();
			
			Form<OpenIDUser> form = Form.form(OpenIDUser.class);
			if(ex.getClass().equals(OAuth2ValidationException.class))
			{
				flash("error", Messages.get(ex.getMessage()));
				return forbidden(index.render(form));
			}
			if(ex.getClass().equals(IOException.class))
			{
				flash("error", ex.getMessage());
				return status(BAD_GATEWAY, index.render(form));
			}
			if(ex.getClass().equals(IllegalStateException.class))
			{
				flash("error", Messages.get(ex.getMessage()));
				return badRequest(index.render(form));
			}
			throw new OAuthException("authentication.errors.oauthExchangeCodeUnexpected", ex);
		}
	}
	
	@Authenticated
	@Transactional
	// As the routes file doesn't define the parameter as optional the provider is always required. An exception is thrown before reaching this action.
	public static Result connectTo(String provider) throws InstantiationException, OAuthException
	{
		IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(provider, routes.Authentication.connectToCallback(provider).absoluteURL(request()));
		
		// Get request token
		String redirectUrl = oauthObject.getRequestToken(routes.Authentication.connectToCallback(provider).absoluteURL(request()));
		
		// Redirect to the authentication url
		return redirect(redirectUrl);
	}
	
	@Authenticated
	@Transactional
	// As the routes file doesn't define the parameter as optional the provider is always required. An exception is thrown before reaching this action.
	public static Result connectToCallback(String provider) throws InstantiationException, OAuthException
	{
		// Create the object that represents the service
		IOAuth oauthObject = OAuthFactory.getInstanceFromProviderName(provider, routes.Authentication.connectToCallback(provider).absoluteURL(request()));
		
		// Verify parameters
		String requestToken = oauthObject.verifyCallbackRequest(request().queryString());
		
		// Get oauth_token_secret and oauth_token
		AccessToken oauthToken = oauthObject.exchangeRequestTokenForAnAccessToken(requestToken);
		
		// If the user doesn't exists, insert in the database and create relationship
		PersistOAuthUser.saveUser(provider, oauthToken, "id", session(SESSION.USERNAME.toString()));
		
		return redirect(routes.User.index());
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
