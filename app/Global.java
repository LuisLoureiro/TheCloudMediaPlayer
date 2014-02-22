import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import models.database.Content;
import models.database.User;

import org.codehaus.jackson.node.ObjectNode;

import com.llorieruo.projects.oauth2Login.OAuth2Factory;
import com.llorieruo.projects.oauth2Login.proxy.FacebookOAuth2Proxy;

import play.Application;
import play.GlobalSettings;
import play.db.jpa.JPA;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.F.Callback0;
import play.libs.Json;
import play.libs.Yaml;
import play.mvc.Action;
import play.mvc.Http.Context;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import controllers.routes;
import controllers.operations.exceptions.ApplicationOperationException;

public class Global extends GlobalSettings
{
	// Intercepting application start-up and shutdown
	@SuppressWarnings("unchecked")
	@Override
	public void onStart(Application app)
	{
		OAuth2Factory.registerInstance("facebook", 
				// Passing a proxy to the real OAuth2 object.
				new FacebookOAuth2Proxy((app.isProd() ? "http://thecloudmediaplayer.herokuapp.com" : "http://localhost:9000")+routes.Authentication.authenticate("facebook").url()));
		// Logger.info("Application has started");
		if(app.isTest())
		{
			final Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("test-data.yml");
			JPA.withTransaction(new Callback0()
			{
				@Override
				public void invoke() throws Throwable
				{
					EntityManager manager = JPA.em();
					for(Object user : all.get("users"))
					{
						User userUser = (User)user;
						manager.persist(userUser);
					}
					for(Object content : all.get("contents"))
					{
						Content contentContent = (Content)content;
						manager.persist(contentContent);
					}
				}
			});
		}
		super.onStart(app);
	}
	
	@Override
	public void onStop(Application app)
	{
		// Logger.info("Application shutdown...");
		super.onStop(app);
	}
	
	// Providing an application error page
	// When an exception occurs in your application, the onError operation will
	// be called. The default is to use the internal framework error page.
	@Override
	public Result onError(RequestHeader request, Throwable t)
	{
		// Get error message
		String errorMessage;
		try
		{
			throw handleThrowable(t);
		}
		catch(ApplicationOperationException | InstantiationException e)
		{
			errorMessage = Messages.get(Lang.preferred(request.acceptLanguages()), e.getMessage());
		}
		catch(NullPointerException e)
		{
			errorMessage = Messages.get(Lang.preferred(request.acceptLanguages()), "errors.genericMessage");
		}
		catch(Throwable e)
		{
			errorMessage = e.getMessage();
		}
		
		// Content negotiation
		if(request.accepts("application/json") || request.accepts("text/json"))
		{
			// Return a json object with the error message
			ObjectNode result = Json.newObject();
			result.put("error", errorMessage);
			return Results.internalServerError(result);
		}
		// If authenticated content = views.html.user.index
		// we need to find a way to verify if the user is authenticated or not!
		
		// Else content = views.html.application.index
		Result ret = Results.internalServerError(views.html.application.index.render());
		
		// Flash error message!
		Context.current().flash().put("error", errorMessage);
		return ret;
	}
	
	// Handling action not found
	// If the framework doesn’t find an action method for a request, the
	// onHandlerNotFound operation will be called.
	@Override
	public Result onHandlerNotFound(RequestHeader request)
	{
		return super.onHandlerNotFound(request);
	}
	
	// The onBadRequest operation will be called if a route was found, but it
	// was not possible to bind the request parameters.
	@Override
	public Result onBadRequest(RequestHeader request, String error)
	{
		return super.onBadRequest(request, error);
	}
	
	// Overriding onRequest
	// One important aspect of the GlobalSettings class is that it provides a
	// way to intercept requests and execute business logic before a request is
	// dispatched to an action.
	@Override
	public Action<?> onRequest(Request request, Method actionMethod)
	{
		System.out.println("before each request..." + request.toString());
		Action<?> action = super.onRequest(request, actionMethod);
		return action;
	}

	private Throwable handleThrowable(Throwable t)
	{
		if(t != null)
		{
			Throwable cause = t.getCause();
			return cause != null && !t.equals(cause) ? handleThrowable(cause): t;
		}
		return t;
	}
}
