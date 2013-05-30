package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.user.index;

public class User extends Controller {

	@Authenticated
	public static Result index() {
		return ok(index.render(request().getQueryString("provider"), null));
	}
}
