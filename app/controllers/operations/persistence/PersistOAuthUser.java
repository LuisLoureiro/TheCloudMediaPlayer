package controllers.operations.persistence;

import play.i18n.Lang;
import play.i18n.Messages;
import controllers.operations.authentication.exceptions.OAuthException;
import models.authentication.AccessToken;

/**
 * TODO utilizar o padrão Singleton ou usar outra alternativa para, ao invés de chamar PersistOAuth1User ou PersistOAuth2User, sem obrigar, na sua implementação, a não existir
 * a obrigatoriedade de criar o método saveUser. Tentar fazer com que esta classe implemente o padrão Template Method, mantendo o Singleton.
 * @author utilizador
 *
 */
public class PersistOAuthUser {

	/**
	 * @throws OAuthException If the provider is inexistent.
	 * 
	 */
	public static void saveUser(String provider, AccessToken accessToken, String findByFieldName, String findByFieldValue, Lang lang) throws OAuthException
	{
		switch (provider) {
			case "dropbox":
				PersistOAuth1User.saveUser(accessToken, findByFieldName, findByFieldValue);
				return;
				
			case "soundcloud":
				PersistOAuth2User.saveUser(accessToken, findByFieldName, findByFieldValue);
				break;
	
			default:
				// Throw Exception
				throw new OAuthException(Messages.get(lang, "authentication.errors.oauthFactoryProviderName"));
		}
	}
}
