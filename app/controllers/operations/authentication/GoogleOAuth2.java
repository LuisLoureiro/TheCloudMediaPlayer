package controllers.operations.authentication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;

import controllers.operations.authentication.exceptions.OAuth2ValidationException;
import controllers.operations.authentication.exceptions.OAuthException;

public class GoogleOAuth2 /*extends AbstractOAuth2 */{

	private final String CLIENT_ID, CLIENT_SECRET;
	/**
	 * Default HTTP transport to use to make HTTP requests.
	 */
	private final HttpTransport TRANSPORT = new NetHttpTransport();
	/**
	 * Default JSON factory to use to deserialize JSON.
	 */
	private final JsonFactory JSON_FACTORY = new JacksonFactory();
	/**
	 * The address to use when revoking access and refresh tokens.
	 */
	private final String REVOKE_URL_FORMAT = "https://accounts.google.com/o/oauth2/revoke?token=%s";

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
	
	/**
	 * Exchange the authorisation code for an access token and a refresh token.
	 * @throws IOException 
	 */
	/*@Override*/
	public TokenResponse exchangeAuthCode(String authorizationCode) throws IOException {
		return new GoogleAuthorizationCodeTokenRequest(
				TRANSPORT, JSON_FACTORY, CLIENT_ID, CLIENT_SECRET, authorizationCode, "postmessage").execute();
	}

	/**
	 * Check that the token is valid.
	 * <p>
	 * Make sure the token we got is for the intended user.
	 * <p>
	 * Make sure the token we got is for our app.
	 * <p>
	 * If the validation has errors an OAuth2ValidationException is thrown.
	 * @param token
	 * @param userId
	 * @throws OAuth2ValidationException
	 * @throws IOException 
	 */
	public void validateToken(TokenResponse token, String userId) throws OAuth2ValidationException, IOException {
		Tokeninfo tokenInfo = isTokenValid(token);
		if(!isTokenForTheIntendedUser(tokenInfo, userId)) throw new OAuth2ValidationException("authentication.errors.oauthTokenIntendedUser");
		if(!isTokenForOurApp(tokenInfo)) throw new OAuth2ValidationException("authentication.errors.oauthTokenIntendedApp");
	}

	/*@Override*/
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

	/*@Override*/
	public boolean isTokenForTheIntendedUser(Tokeninfo tokenInfo, String userId) {
	    return tokenInfo.getUserId().equals(userId);
	}

	/*@Override*/
	public boolean isTokenForOurApp(Tokeninfo tokenInfo) {
	    return tokenInfo.getIssuedTo().equals(CLIENT_ID);
	}
	
	public void revokeToken(String token) throws IOException, OAuthException
	{
		HttpResponse revokeResponse = null;
        // Execute HTTP GET request to revoke current token.
        try
        {
            revokeResponse = TRANSPORT.createRequestFactory()
                    .buildGetRequest(new GenericUrl(
                        String.format(REVOKE_URL_FORMAT, token)))
                    .execute();
        } catch(HttpResponseException ex)
        {
        	throw new OAuthException("user.account.deleted.notPossibleToRevokeGoogleToken", ex);
        } finally
        {
        	if(revokeResponse != null)
        	{
        		try
				{
					revokeResponse.disconnect();
				}
				catch(IOException e){}
        	}
        }
	}
}
