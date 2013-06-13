package controllers.operations.authentication;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import models.authentication.AccessToken;
import models.beans.Resource;
import models.beans.ServiceResources;
import models.beans.dataBinding.Playlist;
import models.beans.dataBinding.Track;
import models.beans.dataBinding.UserId;

import org.apache.http.HttpResponse;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;

import controllers.enums.OAUTH_SERVICE_PROVIDERS;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.parsers.IParserStrategy;

public class SoundcloudOAuth2 implements IOAuth
{
	private final String APP_KEY, APP_SECRET;
//	private final ApiWrapper WRAPPER;
	private final URI redirectUrl;
	
	private final IParserStrategy PARSER;

	public SoundcloudOAuth2(IParserStrategy parser, String redirectUrl) throws InstantiationException
	{
		try
		{
			Properties properties = new Properties();
			properties.load(new FileReader("conf/application.properties"));
			this.APP_KEY = properties.getProperty("SOUNDCLOUD_CLIENT_ID");
			this.APP_SECRET = properties.getProperty("SOUNDCLOUD_CLIENT_SECRET");
			
//			this.WRAPPER = new ApiWrapper(APP_KEY, APP_SECRET, new URI(redirectUrl), null);
			this.redirectUrl = new URI(redirectUrl);
			
			this.PARSER = parser;
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace(); // TODO remove!
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		}
	}

	@Override
	public String getRequestToken(String callbackUrl)
	{
		// TODO we should send a state value, confirming it in the callback.
//		return this.WRAPPER.authorizationCodeUrl(Endpoints.CONNECT).toString();
		return new ApiWrapper(APP_KEY, APP_SECRET, redirectUrl, null)
				.authorizationCodeUrl(Endpoints.CONNECT)
				.toString();
	}

	@Override
	public AccessToken exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuthException
	{
		AccessToken uidAccessRefreshToken = new AccessToken();
		try {
//			Token accessToken = this.WRAPPER.authorizationCode(requestToken);
			ApiWrapper wrapper = new ApiWrapper(APP_KEY, APP_SECRET, redirectUrl, null);
			Token accessToken = wrapper.authorizationCode(requestToken);
			
			uidAccessRefreshToken.setAccessToken(accessToken.access);
			uidAccessRefreshToken.setRefreshToken(accessToken.refresh);
			// See http://tools.ietf.org/html/draft-ietf-oauth-v2-31#page-40
			// The expires in returned in the Token object is the expire date instead of lifetime in seconds as said in the ieft draft.
			uidAccessRefreshToken.setExpiresIn(accessToken.expiresIn);
			
			// TODO think about the possibility to use the public uri to be the id. It's unique! Think about it for every service!
			// TODO beware about the .json and the PARSER.
//			HttpResponse response = this.WRAPPER.get(Request.to(Endpoints.MY_DETAILS+".json").usingToken(accessToken));
			HttpResponse response = wrapper.get(Request.to(Endpoints.MY_DETAILS+".json"));
			uidAccessRefreshToken.setUid(
					PARSER.parse(UserId.class, response.getEntity().getContent())
						.getId());
		} catch (IOException e) {
			throw new OAuthException(e.getMessage(), e);
		}
		return uidAccessRefreshToken;
	}

	@Override
	public ServiceResources getResources(AccessToken accessToken)
	{
		List<Resource> resourcesList = new LinkedList<Resource>();
		try {
			ApiWrapper wrapper = new ApiWrapper(APP_KEY, APP_SECRET, redirectUrl, new Token(accessToken.getAccessToken(), accessToken.getRefreshToken()));
//			HttpResponse resp = this.WRAPPER.get(Request.to(Endpoints.MY_FAVORITES+".json").usingToken(new Token(accessToken.getAccessToken(), accessToken.getRefreshToken())));
//			HttpResponse resp = this.WRAPPER.get(Request.to(Endpoints.MY_TRACKS+".json").usingToken(new Token(accessToken.getAccessToken(), accessToken.getRefreshToken())));
//			HttpResponse resp = this.WRAPPER.get(Request.to(Endpoints.MY_EXCLUSIVE_TRACKS+".json").usingToken(new Token(accessToken.getAccessToken(), accessToken.getRefreshToken())));
			HttpResponse resp = wrapper.get(Request.to(Endpoints.MY_PLAYLISTS+".json"));
			Playlist[] contents = PARSER.parse(Playlist[].class, resp.getEntity().getContent());
			if(contents != null)
			{
				for(Playlist playlist : contents)
				{
					for(Track track : playlist.getTracks())
					{
						resourcesList.add(new Resource(track.getTitle()));
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// TODO throw new
		}
		
		return new ServiceResources(OAUTH_SERVICE_PROVIDERS.SOUNDCLOUD.toString(), resourcesList);
	}
}
