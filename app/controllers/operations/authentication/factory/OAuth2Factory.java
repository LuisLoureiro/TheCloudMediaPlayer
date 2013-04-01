package controllers.operations.authentication.factory;

import controllers.operations.authentication.GoogleOAuth2;
import controllers.operations.authentication.IOAuth2;

public class OAuth2Factory {

	public static IOAuth2 getInstanceFromProviderName(String name) throws InstantiationException {
		if("google".equals(name)) {
			return new GoogleOAuth2();
		}
		
		throw new InstantiationException("There's no match for the given provider name.");
	}
}
