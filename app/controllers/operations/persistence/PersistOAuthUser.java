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
			/**
			 * TODO because of the possibility of relationship between different authentication users (with email address)
			 * there could be relatedAuth that are not OAuthUser (could be openID) or could be Oauth but from a service
			 * that we don't want to get contents from.
			 * 
			 * This workaround is not good!
			 */
			try{
				tokens.add((OAuthUser)relatedUser); // TODO create generalisation in the database
			} catch(ClassCastException e){
				// Not OAuth user!
				// TODO it's time to get the OAuth users related with this NotOAuthUser!
			}
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
				throw new OAuthException(Messages.get(lang, "authentication.errors.oauthFactoryProviderName"));
		}
	}
	
	public static OAuthUser findByUserIdAndProviderName(String userId, String providerName)
	{
		IMapper<String, User> userMapper = new UserMapper();
		
		User user = userMapper.findById(userId);
		if(user != null)
		{
			for(User relatedUser : user.getRelatedAuth())
			{
				OAuthUser relatedOAuthUser = (OAuthUser)relatedUser; // TODO create generalisation in the database
				if(providerName.equals(relatedOAuthUser.getProviderName()))
					return relatedOAuthUser;
			}
		}
		return null;
	}
}
