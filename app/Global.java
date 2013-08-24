import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import models.db.Content;
import models.db.User;

import org.codehaus.jackson.node.ObjectNode;

import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.exceptions.OpenIDException;
import controllers.operations.exceptions.ApplicationOperationException;
import play.Application;
import play.GlobalSettings;
import play.db.jpa.JPA;
import play.i18n.Lang;
import play.i18n.Messages;
import play.libs.F.Callback0;
import play.libs.Json;
import play.libs.Yaml;
import play.mvc.Action;
import play.mvc.Http.Request;
import play.mvc.Http.RequestHeader;
import play.mvc.Result;
import play.mvc.Results;
import scala.Tuple2;
import scala.collection.JavaConversions;

public class Global extends GlobalSettings
{
	// Intercepting application start-up and shutdown
	@Override
	public void onStart(Application app)
	{
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
		// return internalServerError(views.html.error.errorPage.render(t));
		// return
		// Results.internalServerError(views.html.defaultpages.error.render(null));
		// return super.onError(request, t);
		
		// Get error message
		String errorMessage;
		Throwable innerT = t.getCause();
		if(innerT != null)
		{
			try
			{
				throw innerT.getCause();
			}
			catch(ApplicationOperationException | InstantiationException e)
			{
				errorMessage = Messages.get(Lang.preferred(request.acceptLanguages()), e.getMessage());
			}
			catch(NullPointerException e)
			{
				errorMessage = innerT.getMessage();
			}
			catch(Throwable e)
			{
				errorMessage = e.getMessage();
			}
		}
		else
		{
			errorMessage = t.getMessage();
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
		Set<Tuple2<String, String>> flashSet = new HashSet<Tuple2<String, String>>();
		flashSet.add(new Tuple2<String, String>("error", errorMessage));
		ret.getWrappedResult().flashing(JavaConversions.asScalaSet(flashSet).toSeq());
		return ret;
	}
	
	// Handling action not found
	// If the framework doesn’t find an action method for a request, the
	// onHandlerNotFound operation will be called.
	@Override
	public Result onHandlerNotFound(RequestHeader request)
	{
		// return
		// Results.notFound(views.html.error.pageNotFound.render(request.uri()));
		// return
		// Results.notFound(views.html.defaultpages.notFound.render(request,
		// null));
		return super.onHandlerNotFound(request);
	}
	
	// The onBadRequest operation will be called if a route was found, but it
	// was not possible to bind the request parameters.
	@Override
	public Result onBadRequest(RequestHeader request, String error)
	{
		// return Results.badRequest(views.html.error.badRequest.render());
		// return
		// Results.badRequest(views.html.defaultpages.badRequest.render(request,
		// "Don't try to hack the URI!"));
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
		System.out.println("after each request..." + request.toString());
		return action;
	}
}
