package controllers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import models.form.OpenIDUser;

import org.codehaus.jackson.node.ObjectNode;

import play.api.libs.openid.Errors;
import play.data.Form;
import play.libs.Json;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.application.index;

import com.google.api.client.auth.oauth2.TokenResponse;

import controllers.enums.SESSION;
import controllers.operations.authentication.IOAuth2;
import controllers.operations.authentication.enums.OPENID_ATTRIBUTES;
import controllers.operations.authentication.exceptions.OAuth2ValidationException;
import controllers.operations.authentication.factory.OAuth2Factory;

public class Authentication extends Controller {

	public static Result openID() {
    	Form<OpenIDUser> openIDUserForm = Form.form(OpenIDUser.class).bindFromRequest();
    	if(openIDUserForm.hasErrors()) {
    		return badRequest(index.render(openIDUserForm));
    	}
    	OpenIDUser user = openIDUserForm.get();
    	
		try {
			// If the OpenID is invalid, an exception is thrown.
//			/*Promise<String> redirectUrl = */OpenID.redirectURL(id, routes.Authentication.openIDCallback)/*;
//			redirectUrl*/.onRedeem(new Callback<String>() {
//				@Override
//				public void invoke(String url) throws Throwable {
//					redirect(url);
//				}
//			});
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
			return redirect(
					OpenID.redirectURL(user.getId(), routes.Authentication.openIDCallback().absoluteURL(request()), attributes)
					.get());
		} catch(Throwable ex) {
			Throwable exCause = ex.getCause();
			if(exCause != null && exCause.getClass().equals(URISyntaxException.class)) {
				flash("error", "There's an error with your OpenID.\nPlease review it and try again.");
				return badRequest(index.render(openIDUserForm));
			}
			if(ex instanceof Errors.NETWORK_ERROR$) {
				flash("error", "The provided OpenID doesn't exist or it wasn't possible to discover the corresponding provider.");
				return badRequest(index.render(openIDUserForm));
			}
			flash("error", "Some unexpected error occured. Verify your OpenID and try again. Please contact us if the problem continues.");
			return internalServerError(index.render(openIDUserForm));
		}
	}
	
	public static Result openIDCallback() {
		// If the information is not correct or if the server check is false (for example if the redirect URL has been forged), the returned Promise will be a Thrown.
		try {
//			OpenID.verifiedId().onRedeem(new Callback<OpenID.UserInfo>() {
//				@Override
//				public void invoke(UserInfo info) throws Throwable {
//					flash("success", "Successfully signed in.");
//					// redirect to the user home page
//					redirect(routes.User.index());
//				}
//			});
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
								: "";
			String email = attributes.containsKey(OPENID_ATTRIBUTES.EMAIL.getName()) ? attributes.get(OPENID_ATTRIBUTES.EMAIL.getName())
					: attributes.containsKey(OPENID_ATTRIBUTES.EMAIL.getNameWithIndex()) ? attributes.get(OPENID_ATTRIBUTES.EMAIL.getNameWithIndex()) : "";
			// Save user info in the session
			session(SESSION.USERNAME.getId(), info.id);
			session(SESSION.FULL_NAME.getId(), name);
			session(SESSION.EMAIL.getId(), email);
			
			// redirect to the user home page
			flash("success", "Successfully signed in.");
			return redirect(routes.User.index().absoluteURL(request())+"?provider=openID");
		} catch(Throwable ex) {
			Form<OpenIDUser> form = Form.form(OpenIDUser.class);
			if(ex.getClass().equals(TimeoutException.class)) {
				flash("error", "The OpenID provider is taking too much time to verify the ID. " +
						"This could be a temporary problem. Please try to sign in again.");
				return status(GATEWAY_TIMEOUT, index.render(form)); // TODO be sure that this is the correct error code!
			}
			if(ex instanceof Errors.AUTH_CANCEL$) {
				flash("error", "The authentication process was interrupted by the user.");
				return badRequest(index.render(form));
			}
			flash("error", "Some unexpected error occured during the verification of your OpenID. " +
					"Please try to sign in again. Contact us if the problem continues.");
			return internalServerError(index.render(form));
		}
	}
	
	public static Result exchangeCodeWithAccessToken() {
		try {
			// if code == null throw exception
			Map<String, String[]> body = request().body().asFormUrlEncoded();
			if(!body.containsKey("code") || !body.containsKey("userId")) {
				throw new IllegalStateException("The authorization code and/or the user id cannot be null or empty.");
			}
			// Capture the name of the provider used to authenticate.
			String providerName = request().getQueryString("provider");
//			// Ensure that this is no request forgery going on.
//			if (!body.containsKey("csrf") || !body.get("csrf")[0].equals(session("csrf")))
//			    return unauthorized("Invalid CSRF token.");

			// Create an OAuth2 object based on the OAuth2 authentication provider used by the user using the factory method pattern.
			IOAuth2 oauth2Object = OAuth2Factory.getInstanceFromProviderName(providerName);
			// exchange the code with an access token
			TokenResponse token = oauth2Object.exchangeAuthCode(body.get("code")[0]);
			// validate token
			oauth2Object.validateToken(token, body.get("userId")[0]);
			// TODO save the access token and the refresh token
			
			// authenticate user
			session(SESSION.USERNAME.getId(), body.get("userId")[0]);
		    // Store the token in the session for later use.
			session(SESSION.ACCESS_TOKEN.getId(), token.getAccessToken());
			// redirect to the user home page
			flash("success", "Successfully signed in.");
			
			// Content negotiation
			String returnURL = routes.User.index().absoluteURL(request())+"?provider="+providerName;
			if (request().accepts("text/json") || request().accepts("application/json")) {
				// Return a json object with the url to redirect to.
				ObjectNode result = Json.newObject(); result.put("status", "OK"); result.put("url", returnURL);
				return ok(result);
			}
			return redirect(returnURL);
		} catch(Exception ex) {
			Form<OpenIDUser> form = Form.form(OpenIDUser.class);
			if(ex.getClass().equals(OAuth2ValidationException.class)) {
				flash("error", ex.getMessage());
				return forbidden(index.render(form));
			} else if(ex.getClass().equals(IOException.class)) {
				flash("error", ex.getMessage());
				return status(BAD_GATEWAY, index.render(form)); // TODO be sure that this is the correct error code!
			} else if(ex.getClass().equals(IllegalStateException.class)) {
				flash("error", ex.getMessage());
				return badRequest(index.render(form));
			}
			flash("error", "Some unexpected error occured during the exchange of the authorization code. " +
					"Please try to sign in again. Contact us if the problem continues.");
			return internalServerError(index.render(form));
		}
	}
	
	public static Result signOut() {
		// Clear session. TODO Maybe is better to selectively remove the unneeded information.
		session().clear();
//		// Remove the username from the session.
//		if(session(SESSION.USERNAME.getId()) != null)
//			session().remove(SESSION.USERNAME.getId());
		
		// Notify the identity provider if needed
		
		// Redirect to the index page
		flash("success", "Successfully signed out.");
		return redirect(routes.Application.index());
	}
}
