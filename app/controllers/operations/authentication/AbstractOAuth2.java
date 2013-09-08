package controllers.operations.authentication;

import java.util.Map;

import controllers.enums.OAUTH2_ERROR_CODES;
import controllers.enums.OAUTH2_ERROR_PARAMETERS;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.authentication.exceptions.OAuthMissingRequiredParametersException;


public abstract class AbstractOAuth2 implements IOAuth
{
	@Override
	public String verifyCallbackRequest(Map<String, String[]> queryString) throws OAuthException
	{
		// Implementing http://tools.ietf.org/html/rfc6749#section-4.1.2.1
		if(!queryString.containsKey(OAUTH2_ERROR_PARAMETERS.error.name()))
		{
			if(!queryString.containsKey("code"))
				throw new OAuthMissingRequiredParametersException();
			
			return queryString.get("code")[0];
		}
		
		String errorCode = queryString.get(OAUTH2_ERROR_PARAMETERS.error.name())[0];
		System.out.println(String.format("%s: %s; %s.",
				errorCode,
				queryString.containsKey(OAUTH2_ERROR_PARAMETERS.error_description.name()) ? queryString.get(OAUTH2_ERROR_PARAMETERS.error_description.name())[0] : null,
				queryString.containsKey(OAUTH2_ERROR_PARAMETERS.error_uri.name()) ? queryString.get(OAUTH2_ERROR_PARAMETERS.error_uri.name()) : null));
		
		throw new OAuthException(OAUTH2_ERROR_CODES.valueOf(errorCode).getMessage());
	}
}
