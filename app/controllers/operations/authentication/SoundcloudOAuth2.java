package controllers.operations.authentication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import models.authentication.AccessToken;
import play.i18n.Lang;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Token;

import controllers.operations.authentication.exceptions.OAuth2ValidationException;
import controllers.operations.authentication.exceptions.OAuthException;

public class SoundcloudOAuth2 implements IOAuth2
{
	private final String APP_KEY, APP_SECRET;
//	private final ApiWrapper WRAPPER;

	public SoundcloudOAuth2() throws InstantiationException
	{
		try
		{
			Properties properties = new Properties();
			properties.load(new FileReader("conf/application.properties"));
			this.APP_KEY = properties.getProperty("SOUNDCLOUD_CLIENT_ID");
			this.APP_SECRET = properties.getProperty("SOUNDCLOUD_CLIENT_SECRET");
			
//			this.WRAPPER = new ApiWrapper(APP_KEY, APP_SECRET, null, null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		}
	}

	@Override
	public String getRequestToken(String callbackUrl)
	{
		String redirectUrl = null;
		try {
			// TODO we should send a state value, confirming it in the callback.
			redirectUrl = new ApiWrapper(APP_KEY, APP_SECRET, new URI(callbackUrl), null).authorizationCodeUrl(Endpoints.CONNECT, Token.SCOPE_DEFAULT).toString();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return redirectUrl;
	}

	@Override
	public AccessToken exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuthException
	{
		AccessToken uidAccessRefreshToken = new AccessToken();
		try {
			Token accessToken = new ApiWrapper(APP_KEY, APP_SECRET, null, null).authorizationCode(requestToken);
			uidAccessRefreshToken.setAccessToken(accessToken.access);
			uidAccessRefreshToken.setRefreshToken(accessToken.refresh);
			// TODO the uid
		} catch (IOException e) {
			throw new OAuthException(e.getMessage(), e);
		}
		return uidAccessRefreshToken;
	}

	@Override
	public TokenResponse exchangeAuthCode(String authorizationCode) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validateToken(TokenResponse token, String userId, Lang lang) throws OAuth2ValidationException, IOException {
		// TODO Auto-generated method stub
		
	}
}
