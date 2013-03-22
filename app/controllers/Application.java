package controllers;

import models.form.OpenIDUser;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.application.index;

public class Application extends Controller {

    public static Result index() {
    	Form<OpenIDUser> openIDForm = Form.form(OpenIDUser.class);
        return ok(index.render(openIDForm));
    }
}
