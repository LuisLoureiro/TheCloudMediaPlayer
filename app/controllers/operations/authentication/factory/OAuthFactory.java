package controllers.operations.authentication.factory;

import play.i18n.Lang;
import play.i18n.Messages;
import controllers.operations.authentication.DropboxOAuth1;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.SoundcloudOAuth2;

public class OAuthFactory {// TODO think about the use of the singleton pattern. NO. See method exchange of soundcloud. We're setting an oauth token to the wrapper. See again!!

	// TODO think about the possibility of receiving the request as a parameter, collecting the actual parameters from the request object.
	public static IOAuth getInstanceFromProviderName(String name, String redirectUri, Lang lang) throws InstantiationException
	{
		switch (name) {
			case "dropbox":
				return new DropboxOAuth1();
				
			case "soundcloud":
				return new SoundcloudOAuth2(redirectUri);
				
			default:
				throw new InstantiationException(Messages.get(lang, "authentication.errors.oauthFactoryProviderName"));
		}
	}
}
