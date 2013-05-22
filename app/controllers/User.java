package controllers;

import controllers.enums.SESSION;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security.Authenticated;
import views.html.user.index;

public class User extends Controller {

	@Authenticated
	public static Result index() {
		String name = session(SESSION.FULL_NAME.getId()) == null || session(SESSION.FULL_NAME.getId()).isEmpty()
				? session(SESSION.USERNAME.getId()) : session(SESSION.FULL_NAME.getId());
		return ok(index.render(name, session(SESSION.EMAIL.getId()), request().getQueryString("provider"), null));
	}
}
