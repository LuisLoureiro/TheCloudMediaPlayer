package controllers.operations.authentication.factory;

import controllers.operations.authentication.DropboxOAuth1;
import controllers.operations.authentication.IOAuth;
import controllers.operations.authentication.SoundcloudOAuth2;
import controllers.operations.parsers.JsonParser;

public class OAuthFactory // TODO think about the use of the singleton pattern. NO. See method exchange of soundcloud. We're setting an oauth token to the wrapper. See again!! 
{
	private static JsonParser jsonParser;
	
	public static IOAuth getInstanceFromProviderName(String name, String redirectUri) throws InstantiationException
	{
		switch (name) {
			case "dropbox":
				return new DropboxOAuth1();
				
			case "soundcloud":
				return new SoundcloudOAuth2(getJsonParser(), redirectUri);
				
			default:
				throw new InstantiationException("authentication.errors.oauthFactoryProviderName");
		}
	}
	
	private static synchronized JsonParser getJsonParser()
	{
		if(jsonParser == null)
		{
			jsonParser = new JsonParser();
		}
		return jsonParser;
	}
}
