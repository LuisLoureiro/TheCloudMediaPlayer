package utils;

import java.util.LinkedList;
import java.util.List;

import play.data.Form;
import play.data.validation.ValidationError;
import play.i18n.Lang;
import play.i18n.Messages;

public class Utils
{
	public interface ITransform<T, V>
	{
		V transform(T elem);
	}
	
	public static <T> String buildMessageFromValidationErrors(Form<T> form, Lang lang)
	{
		StringBuilder errorString = new StringBuilder();
    	for(List<ValidationError> errors : form.errors().values())
    	{
    		for(ValidationError error : errors)
    		{
    			if(errorString.length() > 0)
    				errorString.append(", ");
    			errorString.append(Messages.get(lang, error.message()));
    		}
    	}
    	return errorString.toString();
	}
	
	public static <V, T> List<V> transform(Iterable<T> iter, ITransform<T, V> transform)
	{
		List<V> list = new LinkedList<V>();
		for (T t : iter) {
			list.add(transform.transform(t));
		}
		
		return list;
	}
}
