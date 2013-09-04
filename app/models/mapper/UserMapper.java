package models.mapper;

import models.database.User;

public class UserMapper extends AbstractMapper<String, User>
{
	@Override
	public Class<User> getClazz()
	{
		return User.class;
	}
}
