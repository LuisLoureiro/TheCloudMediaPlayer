package controllers.operations.authentication;

import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import models.beans.dataBinding.Playlist;
import models.beans.dataBinding.Track;
import models.beans.dataBinding.TrackStreamUrl;
import models.beans.dataBinding.UserId;
import models.beans.dataObject.AccessToken;
import models.beans.dataObject.Resource;
import models.beans.dataObject.ServiceResources;

import org.apache.http.HttpResponse;

import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;

import controllers.enums.OAUTH_SERVICE_PROVIDERS;
import controllers.operations.authentication.exceptions.OAuthException;
import controllers.operations.parsers.IParserStrategy;

public class SoundcloudOAuth2 extends AbstractOAuth2
{
	private final String APP_KEY, APP_SECRET;
	private final URI REDIRECT_URL;
	
	private final IParserStrategy PARSER;

	public SoundcloudOAuth2(IParserStrategy parser, String redirectUrl) throws InstantiationException
	{
		try
		{
			Properties properties = new Properties();
			properties.load(new FileReader("conf/application.properties"));
			this.APP_KEY = properties.getProperty("SOUNDCLOUD_CLIENT_ID");
			this.APP_SECRET = properties.getProperty("SOUNDCLOUD_CLIENT_SECRET");
			
			this.REDIRECT_URL = new URI(redirectUrl);
			
			this.PARSER = parser;
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
			throw new InstantiationException(e.getMessage());
		}
	}

	@Override
	public String getRequestToken(String callbackUrl)
	{
		// TODO we should send a state value, confirming it in the callback.
		return new ApiWrapper(APP_KEY, APP_SECRET, REDIRECT_URL, null)
				.authorizationCodeUrl(Endpoints.CONNECT)
				.toString();
	}

	@Override
	public AccessToken exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuthException
	{
		AccessToken uidAccessRefreshToken = new AccessToken();
		try {
//			Token accessToken = this.WRAPPER.authorizationCode(requestToken);
			ApiWrapper wrapper = new ApiWrapper(APP_KEY, APP_SECRET, REDIRECT_URL, null);
			Token accessToken = wrapper.authorizationCode(requestToken);
			
			uidAccessRefreshToken.setAccessToken(accessToken.access);
			uidAccessRefreshToken.setRefreshToken(accessToken.refresh);
			// See http://tools.ietf.org/html/draft-ietf-oauth-v2-31#page-40
			// The expires in returned in the Token object is the expire date instead of lifetime in seconds as said in the ieft draft.
			uidAccessRefreshToken.setExpiresIn(accessToken.expiresIn);
			
			// TODO think about the possibility to use the public uri to be the id. It's unique! Think about it for every service!
			// TODO beware about the .json and the PARSER.
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
			ApiWrapper wrapper = new ApiWrapper(APP_KEY, APP_SECRET, REDIRECT_URL, new Token(accessToken.getAccessToken(), accessToken.getRefreshToken()));
//			HttpResponse resp = this.WRAPPER.get(Request.to(Endpoints.MY_FAVORITES+".json").usingToken(new Token(accessToken.getAccessToken(), accessToken.getRefreshToken())));
//			HttpResponse resp = this.WRAPPER.get(Request.to(Endpoints.MY_TRACKS+".json").usingToken(new Token(accessToken.getAccessToken(), accessToken.getRefreshToken())));
//			HttpResponse resp = this.WRAPPER.get(Request.to(Endpoints.MY_EXCLUSIVE_TRACKS+".json").usingToken(new Token(accessToken.getAccessToken(), accessToken.getRefreshToken())));
			HttpResponse resp = wrapper.get(Request.to(Endpoints.MY_PLAYLISTS+".json"));
			// TODO verify StatusLine before reading the entity! If Unauthorized refresh token!
			Playlist[] contents = PARSER.parse(Playlist[].class, resp.getEntity().getContent());
			if(contents != null)
			{
				for(Playlist playlist : contents)
				{
					for(Track track : playlist.getTracks())
					{
						// TODO pass the stream url to the client. Pass this value when calling the track/streamurl instead of the trackId. This way we can avoid one API call!
						resourcesList.add(new Resource(String.valueOf(track.getId()), track.getTitle(), "audio"/*TODO the subtype is missing.*/));
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO throw new
		}
		
		return new ServiceResources(OAUTH_SERVICE_PROVIDERS.SOUNDCLOUD.toString(), resourcesList);
	}

	@Override
	public String getResourceStreamUrl(AccessToken accessToken, String trackId) throws OAuthException
	{
		ApiWrapper wrapper = new ApiWrapper(APP_KEY, APP_SECRET, REDIRECT_URL, new Token(accessToken.getAccessToken(), accessToken.getRefreshToken()));
		
		try {
			HttpResponse resp = wrapper.get(Request.to(Endpoints.TRACK_DETAILS+".json", Integer.parseInt(trackId))); // TODO consider the track streamable property!
			Track trackDetails = PARSER.parse(Track.class, resp.getEntity().getContent());
			if(trackDetails != null)
			{
				HttpResponse respStreamUrl = wrapper.get(Request.to(trackDetails.getStream_url()).with("allow_redirects", "False"));
				TrackStreamUrl trackStreamUrl = PARSER.parse(TrackStreamUrl.class, respStreamUrl.getEntity().getContent());
				if(trackStreamUrl != null)
					return trackStreamUrl.getLocation();
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new OAuthException(e.getMessage(), e); // TODO better exception message!
		}
		
		return null;
	}
}
