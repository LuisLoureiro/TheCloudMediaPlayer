package controllers;

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
			if(ex instanceof Errors.NETWORK_ERROR$) {
				flash("error", "The provided OpenID id is invalid or it wasn't possible to discover the corresponding provider.");
				return badRequest(index.render(openIDUserForm));
			}
			return internalServerError(ex.toString() + "\n" + ex.getMessage());
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
			UserInfo info = OpenID.verifiedId().get();
			// Save user info in the session
			session("username", info.id); // TODO create an object of type User and associate it to the request/response or session.
			
			// redirect to the user home page
			flash("success", "Successfully signed in.");
			return redirect(routes.User.index());
		} catch(Throwable ex) {
			if(ex instanceof Errors.AUTH_CANCEL$) {
				flash("error", "The authentication process was interrupted by the user.");
				return badRequest(index.render(Form.form(OpenIDUser.class)));
			}
			return internalServerError(ex.toString() + "\n" + ex.getMessage());
		}
	}
}
