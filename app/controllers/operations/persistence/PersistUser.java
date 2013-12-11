package controllers.operations.persistence;

import java.util.List;

import models.database.User;
import models.database.notEntity.OAuthUser;
import models.mapper.IMapper;
import models.mapper.UserMapper;
import controllers.enums.OAUTH_SERVICE_PROVIDERS;
import controllers.operations.exceptions.ApplicationOperationException;

public class PersistUser
{
	private static final IMapper<String, User> MAPPER = new UserMapper();
	/**
	 * Create a new {@link User} if the {@code userId} is inexistent.
	 * Before saving the {@link User} check the existence of an {@link User} with email address equal to the {@code userEmail} parameter, 
	 * creating a relationship between them if exists.
	 * 
	 * @param userId
	 * @param userEmail
	 */
	public static void saveUser(String userId, String userEmail)
	{
		User user = loadUser(userId);
		if(user == null)
		{
			// if there's an user with the same email address...
			User existingRecord = null;
			if(userEmail != null && !userEmail.isEmpty())
			{
				for (User auxUser : MAPPER.findBy("email", userEmail)) {
					existingRecord = auxUser;
					break;
				}
			}
			// Insert the new user.
			user = new User();
			user.setId(userId);
			user.setEmail(userEmail);
			user.setFirstAuth(existingRecord);
			
			MAPPER.save(user);
		}
	}
	
	public static void deleteUser(String userId) throws ApplicationOperationException
	{
		User user = loadUser(userId)
				, firstAuth;
		List<String> allLowerCase = OAUTH_SERVICE_PROVIDERS.getAllLowerCase();
		
		if(user == null)
		{
			throw new ApplicationOperationException("user.userIdUnexistent");
		}
		if((firstAuth = user.getFirstAuth()) != null)
		{
			firstAuth.removeUser(user);
		}
		// TODO this operation should be done automatically with cascade instructions?!? Change the database schema.
		for(User relatedOAuthUser : PersistOAuthUser.findAllAccessTokens(userId)) // Only remove the services to whom the user has connected to.
		{
			/**
			 * Because of the possibility of relationship between different authentication users (with email address)
			 * there could be relatedAuth that are not OAuthUser (could be openID) or could be OAuth but from a service
			 * that we don't want to get contents from.
			 */
//			try{
//				tokens.add((OAuthUser)relatedUser);
//			} catch(ClassCastException e){/* Not OAuth user! */}
			System.out.println("Checking relatedOAuthUser: "+relatedOAuthUser.getId());
			if(relatedOAuthUser instanceof OAuthUser && allLowerCase.contains(((OAuthUser) relatedOAuthUser).getProviderName()))
			{
				System.out.println("Removing relatedOAuthUser: "+relatedOAuthUser.getId()+", "+((OAuthUser) relatedOAuthUser).getProviderName());
				MAPPER.delete(relatedOAuthUser);
			}
		}

		System.out.println("Removing user: "+user.getId());
		MAPPER.delete(user);
	}
	
	static User loadUser(String userId)
	{
		return MAPPER.findById(userId);
	}
}
