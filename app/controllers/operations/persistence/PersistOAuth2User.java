package controllers.operations.persistence;

import models.beans.dataObject.AccessToken;
import models.database.OAuth2User;
import models.database.User;
import models.mapper.IMapper;
import models.mapper.OAuth2UserMapper;
import models.mapper.UserMapper;

public class PersistOAuth2User {

	/**
	 * Create a new {@link OAuth2User} if the {@code userId} is inexistent, saving the access and refresh tokens.
	 * <p>
	 * If the {@code userId} already exists update the access token.
	 * <p>
	 * Before saving or updating the {@link OAuth2User} check the existence of an {@link User} with email address equal to the {@code userEmail} parameter, 
	 * creating a relationship between them if exists.
	 * 
	 * @param token an instance of {@link AccessToken} containing user id, access token and refresh token.
	 * @param findByFieldName
	 * @param findByFieldValue
	 */
	public static void saveUser(String provider, AccessToken token, String findByFieldName, String findByFieldValue)
	{
		IMapper<String, OAuth2User> oauth2Mapper = new OAuth2UserMapper(); // TODO ver a possibilidade de usar o padrão factory.
		
		OAuth2User user = oauth2Mapper.findById(token.getUid());
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
			// Insert the new oauth2 user.
			user = new OAuth2User();
			user.setId(token.getUid());
			user.setProviderName(provider);
			user.setAccessToken(token.getAccessToken());
			user.setRefreshToken(token.getRefreshToken());
			user.setExpiresIn(token.getExpiresIn());
			// Create relationship
			user.setFirstAuth(existingRecord);
			user.setEmail(token.getEmail());
			
			oauth2Mapper.save(user);
		} else {
			// Update existing oauth2 user.
			user.setAccessToken(token.getAccessToken());
			if(token.getRefreshToken() != null && !token.getRefreshToken().isEmpty())
				user.setRefreshToken(token.getRefreshToken());
			
			oauth2Mapper.update(user);
		}
	}
}
