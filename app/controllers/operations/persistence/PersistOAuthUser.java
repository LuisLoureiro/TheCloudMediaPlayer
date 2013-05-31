package controllers.operations.persistence;

import models.authentication.AccessToken;

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
	public static void saveUser(String provider, AccessToken accessToken, String findByFieldName, String findByFieldValue)
	{
		switch (provider) {
			case "dropbox":
				PersistOAuth1User.saveUser(accessToken.getAccessToken(), accessToken.getRefreshToken(), accessToken.getUid(), findByFieldName, findByFieldValue);
				return;
				
			case "soundcloud":
				TokenResponse token = new TokenResponse();
				token.setAccessToken(accessToken.getAccessToken()); token.setRefreshToken(accessToken.getRefreshToken());
				PersistOAuth2User.saveUser(token, accessToken.getUid(), findByFieldName, findByFieldValue);
				break;
	
			default:
				// Throw Exception
				break;
		}
	}
}
