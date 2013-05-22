package controllers.operations.persistence;

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
	 * @param token
	 * @param userId
	 * @param findByFieldName
	 * @param findByFieldValue
	 */
	public static void saveUser(String oauthToken, String oauthTokenSecret, String userId, String findByFieldName, String findByFieldValue)
	{
		IMapper<String, OAuth1User> oauth1Mapper = new OAuth1UserMapper(); // TODO ver a possibilidade de usar o padrão factory.
		
		OAuth1User user = oauth1Mapper.findById(userId);
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
			user.setId(userId);
			user.setEmail(existingRecord != null ? existingRecord.getEmail() : null);
			user.setOauthToken(oauthToken);
			user.setOauthTokenSecret(oauthTokenSecret);
			// Create relationship
			if(existingRecord != null) 
			{
				user.setFirstAuth(existingRecord);
				existingRecord.getRelatedAuth().add(user);
			}
			oauth1Mapper.save(user);
		} else {
			// Update existing oauth2 user.
			user.setOauthToken(oauthTokenSecret);
			oauth1Mapper.update(user);
		}
	}
}
