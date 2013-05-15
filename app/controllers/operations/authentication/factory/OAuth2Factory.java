package controllers.operations.authentication.factory;

import play.i18n.Lang;
import play.i18n.Messages;
import controllers.operations.authentication.GoogleOAuth2;
import controllers.operations.authentication.IOAuth2;

public class OAuth2Factory {

	public static IOAuth2 getInstanceFromProviderName(String name, Lang lang) throws InstantiationException {
		if("google".equals(name)) {
			return new GoogleOAuth2();
		}
		
		throw new InstantiationException(Messages.get(lang, "authentication.errors.oauthFactoryProviderName"));
	}
}
