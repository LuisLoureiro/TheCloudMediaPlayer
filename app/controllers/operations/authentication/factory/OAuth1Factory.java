package controllers.operations.authentication.factory;

import play.i18n.Lang;
import play.i18n.Messages;
import controllers.operations.authentication.DropboxOAuth1;
import controllers.operations.authentication.IOAuth1;

public class OAuth1Factory {

	public static IOAuth1 getInstanceFromProviderName(String name, Lang lang) throws InstantiationException {
		if("dropbox".equals(name)) {
			return new DropboxOAuth1();
		}
		
		throw new InstantiationException(Messages.get(lang, "authentication.errors.oauthFactoryProviderName"));
	}
}
