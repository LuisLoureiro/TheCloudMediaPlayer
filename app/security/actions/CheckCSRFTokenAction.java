package security.actions;

import org.codehaus.jackson.node.ObjectNode;

import models.beans.dataBinding.CSRFData;
import play.data.Form;
import play.libs.Json;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Result;
import security.annotations.CheckCSRFToken;

/**
 * An action used to validate a CSRF token.
 * Check for a valid token in the request and in the session.
 */
public class CheckCSRFTokenAction extends Action<CheckCSRFToken>
{
	@Override
	public Result call(Context ctx) throws Throwable
	{
		ObjectNode errorJson = Json.newObject();
		Form<CSRFData> form = Form.form(CSRFData.class).bindFromRequest();
		if(form.hasErrors())
			return badRequest(form.errorsAsJson());
		
		String stateSession = ctx.session().get(configuration.tokenName());
		if(stateSession == null || stateSession.isEmpty())
		{
			errorJson.put("error", "There's no CSRF token in the current session.");
			return badRequest(errorJson);
		}
		
		if(!form.get().getState().equals(stateSession))
		{
			errorJson.put("error", "CSRF tokens don't match.");
			return badRequest(errorJson);
		}
		
		return delegate.call(ctx);
	}
}
