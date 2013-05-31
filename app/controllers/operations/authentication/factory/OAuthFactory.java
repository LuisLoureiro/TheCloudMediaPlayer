package controllers.operations.authentication.factory;

import play.i18n.Lang;
import play.i18n.Messages;
import controllers.operations.authentication.DropboxOAuth1;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.SoundcloudOAuth2;

public class OAuthFactory {

	public static IOAuth getInstanceFromProviderName(String name, Lang lang) throws InstantiationException
	{
		switch (name) {
			case "dropbox":
				return new DropboxOAuth1();
				
			case "soundcloud":
				return new SoundcloudOAuth2();
				
			default:
				throw new InstantiationException(Messages.get(lang, "authentication.errors.oauthFactoryProviderName"));
		}
	}
}
