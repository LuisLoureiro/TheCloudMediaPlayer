package controllers.operations.persistence;

import models.db.User;
import models.mapper.IMapper;
import models.mapper.UserMapper;

public class PersistUser {

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
		IMapper<String, User> mapper = new UserMapper(); // TODO ver a possibilidade de usar o padrão factory.
		User user = mapper.findById(userId);
		if(user == null)
		{
			// if there's an user with the same email address...
			User existingRecord = null;
			if(userEmail != null && !userEmail.isEmpty())
			{
				for (User auxUser : mapper.findBy("email", userEmail)) {
					existingRecord = auxUser;
					break;
				}
			}
			// Insert the new user.
			user = new User();
			user.setId(userId);
			user.setEmail(userEmail);
			if(existingRecord != null) 
			{
				user.setFirstAuth(existingRecord);
				existingRecord.getRelatedAuth().add(user);
			}
			mapper.save(user);
		}
	}
}
