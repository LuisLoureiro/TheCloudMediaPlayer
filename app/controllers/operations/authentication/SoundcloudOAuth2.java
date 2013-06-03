package controllers.operations.authentication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import models.authentication.AccessToken;
import models.beans.ServiceResources;

import org.apache.http.HttpResponse;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;

import play.i18n.Lang;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.soundcloud.api.ApiWrapper;
import com.soundcloud.api.Endpoints;
import com.soundcloud.api.Request;
import com.soundcloud.api.Token;

import controllers.operations.authentication.exceptions.OAuth2ValidationException;
import controllers.operations.authentication.exceptions.OAuthException;

public class SoundcloudOAuth2 implements IOAuth2
{
	private final String APP_KEY, APP_SECRET;
	private final ApiWrapper WRAPPER;

	public SoundcloudOAuth2(String redirectUrl) throws InstantiationException
	{
		try
		{
			Properties properties = new Properties();
			properties.load(new FileReader("conf/application.properties"));
			this.APP_KEY = properties.getProperty("SOUNDCLOUD_CLIENT_ID");
			this.APP_SECRET = properties.getProperty("SOUNDCLOUD_CLIENT_SECRET");
			
			this.WRAPPER = new ApiWrapper(APP_KEY, APP_SECRET, new URI(redirectUrl), null);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		} catch (IOException | URISyntaxException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		}
	}

	@Override
	public String getRequestToken(String callbackUrl)
	{
		// TODO we should send a state value, confirming it in the callback.
		return this.WRAPPER.authorizationCodeUrl(Endpoints.CONNECT).toString();
	}

	@Override
	public AccessToken exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuthException
	{
		AccessToken uidAccessRefreshToken = new AccessToken();
		try {
			Token accessToken = this.WRAPPER.authorizationCode(requestToken);
//			this.WRAPPER.setToken(accessToken);
			
			uidAccessRefreshToken.setAccessToken(accessToken.access);
			uidAccessRefreshToken.setRefreshToken(accessToken.refresh);
			// TODO the uid. Send a request to /me and grab the id value!
			// TODO think about the possibility to use the public uri to be the id. It's unique! Think about it for every service!
			// TODO use enumerator for the accept header value..setHeader(org.apache.http.HttpHeaders.ACCEPT, "application/json")
			HttpResponse response = this.WRAPPER.get(Request.to(Endpoints.MY_DETAILS+".json").usingToken(accessToken));
			uidAccessRefreshToken.setUid(getIdFromJSON(response.getEntity().getContent()));
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
	
	// TODO read this: http://wiki.fasterxml.com/JacksonStreamingApi
	private String getIdFromJSON(InputStream stream)
	{
		String value = null;
		try {
			JsonFactory jsonFactory = new JsonFactory(); // or, for data binding, org.codehaus.jackson.mapper.MappingJsonFactory 
			JsonParser jp = jsonFactory.createJsonParser(stream); // or URL, Stream, Reader, String, byte[]
			// Read this: Also, if you happen to have an ObjectMapper, there is also ObjectMapper.getJsonFactory() that you can use to reuse factory it has
			//  (since (re)using a JsonFactory instances is one Performance Best Practices).
			// Sanity check: verify that we got "Json Object":
			if (jp.nextToken() != JsonToken.START_OBJECT)
			{
			    throw new IOException("Expected data to start with an Object");
			}
			// Iterate over object fields:
			while (jp.nextToken() != JsonToken.END_OBJECT)
			{
			    String fieldName = jp.getCurrentName();
			    // Let's move to value
			    jp.nextToken();
			    if (fieldName.equals("id")) {
			    	value = String.valueOf(jp.getLongValue());
			    	break;
			    }
			}
			jp.close(); // important to close both parser and underlying File reader
			return value;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}

	@Override
	public ServiceResources getResources() {
		// TODO Auto-generated method stub
		return null;
	}
}
