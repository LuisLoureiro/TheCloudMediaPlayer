package controllers.operations.persistence;

import com.google.api.client.auth.oauth2.TokenResponse;

/**
 * TODO utilizar o padrão Singleton ou usar outra alternativa para, ao invés de chamar PersistOAuth1User ou PersistOAuth2User, sem obrigar, na sua implementação, a não existir
 * a obrigatoriedade de criar o método saveUser. Tentar fazer com que esta classe implemente o padrão Template Method, mantendo o Singleton.
 * @author utilizador
 *
 */
public class PersistOAuthUser {

	/**
	 * 
	 */
	public static void saveUser(String provider, String oauthToken, String oauthTokenSecret, String userId, String findByFieldName, String findByFieldValue)
	{
		switch (provider) {
			case "dropbox":
				PersistOAuth1User.saveUser(oauthToken, oauthTokenSecret, userId, findByFieldName, findByFieldValue);
				return;
				
			case "soundcloud":
				TokenResponse token = new TokenResponse();
				token.setAccessToken(oauthToken); token.setRefreshToken(oauthTokenSecret);
				PersistOAuth2User.saveUser(token, userId, findByFieldName, findByFieldValue);
				break;
	
			default:
				// Throw Exception
				break;
		}
	}
}
