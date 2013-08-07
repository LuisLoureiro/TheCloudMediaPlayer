package controllers.operations.persistence;

import models.authentication.AccessToken;
import models.db.OAuth1User;
import models.db.User;
import models.mapper.IMapper;
import models.mapper.OAuth1UserMapper;
import models.mapper.UserMapper;

public class PersistOAuth1User {

	/**
	 * Create a new {@link OAuth1User} if the {@code userId} is inexistent, saving the oauth token and oauth token secret.
	 * <p>
	 * If the {@code userId} already exists update the oauth token.
	 * <p>
	 * Before saving or updating the {@link OAuth1User} check the existence of an {@link User} with {@code findByFieldName} field equal to the {@code findByFieldValue} parameter, 
	 * creating a relationship between them if exists.
	 * 
	 * @param token an instance of {@link AccessToken} containing user id, oauth token and oauth token secret.
	 * @param findByFieldName
	 * @param findByFieldValue
	 */
	public static void saveUser(String provider, AccessToken token, String findByFieldName, String findByFieldValue)
	{
		IMapper<String, OAuth1User> oauth1Mapper = new OAuth1UserMapper(); // TODO ver a possibilidade de usar o padrão factory.
		
		OAuth1User user = oauth1Mapper.findById(token.getUid());
		if(user == null)
		{
			// if there's an user with the same email address...
			User existingRecord = null;
			if(findByFieldName != null && !findByFieldName.isEmpty())
			{
				for (User auxUser : new UserMapper().findBy(findByFieldName, findByFieldValue)) { // TODO ver a possibilidade de usar o padrão factory.
					existingRecord = auxUser;
					break;
				}
			}
			// Insert the new oauth1 user.
			user = new OAuth1User();
			user.setId(token.getUid());
			user.setProviderName(provider);
			user.setOauthToken(token.getAccessToken());
			user.setOauthTokenSecret(token.getRefreshToken());
			// Create relationship
			user.setFirstAuth(existingRecord);
			user.setEmail(token.getEmail());
			
			oauth1Mapper.save(user);
		} else {
			// Update existing oauth1 user.
			user.setOauthToken(token.getAccessToken());
			if(token.getRefreshToken() != null && !token.getRefreshToken().isEmpty())
				user.setOauthTokenSecret(token.getRefreshToken());
			
			oauth1Mapper.update(user);
		}
	}
}
