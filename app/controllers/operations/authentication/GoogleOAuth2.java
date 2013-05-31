package controllers.operations.authentication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import models.authentication.AccessToken;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;

import controllers.operations.authentication.exceptions.OAuth2ValidationException;
import controllers.operations.authentication.exceptions.OAuthException;

public class GoogleOAuth2 extends AbstractOAuth2 {

	private final String CLIENT_ID, CLIENT_SECRET;
	/**
	 * Default HTTP transport to use to make HTTP requests.
	 */
	private final HttpTransport TRANSPORT = new NetHttpTransport();
	/**
	 * Default JSON factory to use to deserialize JSON.
	 */
	private final JsonFactory JSON_FACTORY = new JacksonFactory();

	public GoogleOAuth2() throws InstantiationException {
		try {
			Properties properties = new Properties();
			properties.load(new FileReader("conf/application.properties"));
			this.CLIENT_ID = properties.getProperty("GOOGLE_CLIENT_ID");
			this.CLIENT_SECRET = properties.getProperty("GOOGLE_CLIENT_SECRET");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		}
	}
	
	@Override
	public TokenResponse exchangeAuthCode(String authorizationCode) throws IOException {
		return new GoogleAuthorizationCodeTokenRequest(
				TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, authorizationCode, "postmessage").execute();
	}

	@Override
	public Tokeninfo isTokenValid(TokenResponse tokenResponse) throws IOException, OAuth2ValidationException {
		// Create a credential representation of the token data.
	    Credential credential = new GoogleCredential.Builder()
	        .setJsonFactory(JSON_FACTORY)
	        .setTransport(TRANSPORT)
	        .setClientSecrets(CLIENT_ID, CLIENT_SECRET).build()
	        .setFromTokenResponse(tokenResponse);
		
	    Oauth2 oauth2 = new Oauth2.Builder(TRANSPORT, JSON_FACTORY, credential).build();
	    Tokeninfo tokenInfo = oauth2.tokeninfo().setAccessToken(credential.getAccessToken()).execute();
		if(tokenInfo.containsKey("error")) throw new OAuth2ValidationException(tokenInfo.get("error").toString());
		
		return tokenInfo;
	}

	@Override
	public boolean isTokenForTheIntendedUser(Tokeninfo tokenInfo, String userId) {
	    return tokenInfo.getUserId().equals(userId);
	}

	@Override
	public boolean isTokenForOurApp(Tokeninfo tokenInfo) {
	    return tokenInfo.getIssuedTo().equals(CLIENT_ID);
	}

	@Override
	public String getRequestToken(String callbackUrl) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccessToken exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuthException {
		// TODO Auto-generated method stub
		return null;
	}
}
