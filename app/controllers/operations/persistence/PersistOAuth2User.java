package controllers.operations.persistence;

import models.db.OAuth2User;
import models.db.User;
import models.mapper.IMapper;
import models.mapper.OAuth2UserMapper;
import models.mapper.UserMapper;

import com.google.api.client.auth.oauth2.TokenResponse;

public class PersistOAuth2User {

	/**
	 * Create a new {@link OAuth2User} if the {@code userId} is inexistent, saving the access and refresh tokens.
	 * <p>
	 * If the {@code userId} already exists update the access token.
	 * <p>
	 * Before saving or updating the {@link OAuth2User} check the existence of an {@link User} with email address equal to the {@code userEmail} parameter, 
	 * creating a relationship between them if exists.
	 * 
	 * @param token
	 * @param userId
	 * @param userEmail
	 */
	public static void saveUser(TokenResponse token, String userId, String userEmail)
	{
		IMapper<String, OAuth2User> oauth2Mapper = new OAuth2UserMapper(); // TODO ver a possibilidade de usar o padrão factory.
		
		OAuth2User user = oauth2Mapper.findById(userId);
		if(user == null)
		{
			// if there's an user with the same email address...
			User existingRecord = null;
			if(userEmail != null && !userEmail.isEmpty())
			{
				for (User auxUser : new UserMapper().findBy("email", userEmail)) { // TODO ver a possibilidade de usar o padrão factory.
					existingRecord = auxUser;
					break;
				}
			}
			// Insert the new oauth2 user.
			user = new OAuth2User();
			user.setId(userId);
			user.setEmail(userEmail);
			user.setAccessToken(token.getAccessToken());
			user.setRefreshToken(token.getRefreshToken());
			// Create relationship
			if(existingRecord != null) 
			{
				user.setFirstAuth(existingRecord);
				existingRecord.getRelatedAuth().add(user);
			}
			oauth2Mapper.save(user);
		} else {
			// Update existing oauth2 user.
			user.setAccessToken(token.getAccessToken());
			oauth2Mapper.update(user);
		}
	}
}
