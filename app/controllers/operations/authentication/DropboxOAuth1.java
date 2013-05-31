package controllers.operations.authentication;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import models.db.OAuth1Token;
import models.mapper.IMapper;
import models.mapper.OAuth1TokenMapper;
import play.i18n.Messages;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.RequestTokenPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.WebAuthSession;
import com.dropbox.client2.session.WebAuthSession.WebAuthInfo;

import controllers.operations.authentication.exceptions.OAuth1TokenException;


public class DropboxOAuth1 implements IOAuth1 {
	
	private final String APP_KEY, APP_SECRET;
	private final DropboxAPI<WebAuthSession> DROPBOX_API;
//	private static final AccessType ACCESS_TYPE = AccessType.APP_FOLDER;

	public DropboxOAuth1() throws InstantiationException
	{
		try {
			Properties properties = new Properties();
			properties.load(new FileReader("conf/application.properties"));
			this.APP_KEY = properties.getProperty("DROPBOX_APP_KEY");
			this.APP_SECRET = properties.getProperty("DROPBOX_APP_SECRET");

			this.DROPBOX_API = new DropboxAPI<WebAuthSession>(
					new WebAuthSession(
							new AppKeyPair(APP_KEY, APP_SECRET),
							AccessType.APP_FOLDER));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new InstantiationException(e.getMessage());
		}
	}

	@Override
	public String getRequestToken(String callbackUrl) {
		try
		{
			WebAuthInfo authInfo = DROPBOX_API.getSession().getAuthInfo(callbackUrl);
			// Save key and secret in the database.
			OAuth1Token token = new OAuth1Token();
			token.setToken(authInfo.requestTokenPair.key);
			token.setSecret(authInfo.requestTokenPair.secret);
			IMapper<String, OAuth1Token> mapper = new OAuth1TokenMapper();
			mapper.save(token);
			
			// Return the authorisation url.
			return authInfo.url;
		} catch(DropboxException ex)
		{
			// TODO alterar!
			return null;
		}
	}

	@Override
	public Map.Entry<String, String> exchangeRequestTokenForAnAccessToken(String requestToken) throws OAuth1TokenException
	{
		try
		{
			// Get the request token pair from the database.
			IMapper<String, OAuth1Token> oauth1TokenMapper = new OAuth1TokenMapper(); // TODO ver a possibilidade de usar o padrão factory.
			OAuth1Token token = oauth1TokenMapper.findById(requestToken); 
			if(token == null ) throw new OAuth1TokenException(Messages.get("authentication.errors.oauthTokenInexistent"));
			// Delete from database the request token pair. Are not necessary anymore.
			oauth1TokenMapper.delete(token);
			
			/*String dropboxUID = */DROPBOX_API.getSession().retrieveWebAccessToken(
					new RequestTokenPair(token.getToken(), token.getSecret()));
			
			// Get oauth_token_secret and oauth_token
			AccessTokenPair tokenPair = DROPBOX_API.getSession().getAccessTokenPair();
			
			// Return the OAuth token to be used when accessing protected resources and the oauth token secret.
			return new AbstractMap.SimpleEntry<String, String>(tokenPair.key, tokenPair.secret);
		} catch(DropboxException ex)
		{
//			throws ??; TODO
			return null;
		}
	}
	
	public List<Entry> getFiles()
	{
		try {
			Entry metadata = DROPBOX_API.metadata("/", 10, null, true, null);
//			Iterator<Entry> filteredEntries = metadata.contents.iterator();
//			while(filteredEntries.hasNext()) {
//				Entry entry = filteredEntries.next();
//				if(!entry.mimeType in "audio ou video") filteredEntries.remove();
//			}
			return metadata.contents;
		} catch (DropboxException e) {
//			throws ??; TODO
			return null;
		}
	}
}
