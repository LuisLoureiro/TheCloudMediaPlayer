package controllers;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import models.form.OpenIDUser;
import play.api.libs.openid.Errors;
import play.data.Form;
import play.libs.OpenID;
import play.libs.OpenID.UserInfo;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.application.index;

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
			return redirect(OpenID.redirectURL(user.getId(), routes.Authentication.openIDCallback().absoluteURL(request())).get());
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
	
	// TODO wait a json request and return a json response
	public static Result exchangeCodeWithAccessToken() {
		// if code == null throw exception
		Map<String, String[]> body = request().body().asFormUrlEncoded();
		if(!body.containsKey("code"))
			return badRequest("The authorization code cannot be null or empty");
//		// Ensure that this is no request forgery going on.
//		if (!body.containsKey("csrf") || !body.get("csrf")[0].equals(session("csrf")))
//		    return unauthorized("Invalid CSRF token.");

		// exchange the code with an access token
		// save the access token and the refresh token
		// authenticate user
		session("username", body.get("code")[0]);
		// redirect to the user home page
		flash("success", "Successfully signed in.");
		return redirect(routes.User.index("google"));
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
			// Save user info in the session
			session("username", info.id); // TODO create an object of type User and associate it to the request/response or session.
			
			// redirect to the user home page
			flash("success", "Successfully signed in.");
			return redirect(routes.User.index("openID"));
		} catch(Throwable ex) {
			Form<OpenIDUser> form = Form.form(OpenIDUser.class);
			if(ex.getClass().equals(TimeoutException.class)) {
				flash("error", "The OpenID provider is taking too much time to verify the ID. " +
						"This could be a temporary problem. Please try to sign in again.");
				return status(GATEWAY_TIMEOUT, index.render(form));
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
	
	public static Result signOut() {
		// Remove the username from the session.
		if(session("username") != null)
			session().remove("username");
		
		// Notify the identity provider if needed
		
		// Redirect to the index page
		flash("success", "Successfully signed out.");
		return redirect(routes.Application.index());
	}
}
