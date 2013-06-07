package controllers.operations.persistence;

import java.util.LinkedList;
import java.util.List;

import models.authentication.AccessToken;
import models.db.User;
import models.db.notEntity.OAuthUser;
import models.mapper.IMapper;
import models.mapper.UserMapper;
import play.i18n.Lang;
import play.i18n.Messages;
import controllers.operations.authentication.exceptions.OAuthException;

/**
 * TODO utilizar o padrão Singleton ou usar outra alternativa para, ao invés de chamar PersistOAuth1User ou PersistOAuth2User, sem obrigar, na sua implementação, a não existir
 * a obrigatoriedade de criar o método saveUser. Tentar fazer com que esta classe implemente o padrão Template Method, mantendo o Singleton.
 * @author utilizador
 *
 */
public class PersistOAuthUser
{
	/**
	 * @param userId
	 * @return list with all the tokens corresponding to the requested userId.
	 */
	public static List<OAuthUser> findAllAccessTokens(String userId)
	{
		IMapper<String, User> userMapper = new UserMapper();
		List<OAuthUser> tokens = new LinkedList<OAuthUser>();
		
		User user = userMapper.findById(userId);
		if(user == null)
			return null;
		
		for(User relatedUser : user.getRelatedAuth())
		{
			tokens.add((OAuthUser)relatedUser);
		}
		
		return tokens;
	}
	/**
	 * @throws OAuthException If the provider is inexistent.
	 * 
	 */
	public static void saveUser(String provider, AccessToken accessToken, String findByFieldName, String findByFieldValue, Lang lang) throws OAuthException
	{
		switch (provider) {
			case "dropbox":
				PersistOAuth1User.saveUser(provider, accessToken, findByFieldName, findByFieldValue);
				return;
				
			case "soundcloud":
				PersistOAuth2User.saveUser(provider, accessToken, findByFieldName, findByFieldValue);
				break;
	
			default:
				// Throw Exception
				throw new OAuthException(Messages.get(lang, "authentication.errors.oauthFactoryProviderName"));
		}
	}
}
