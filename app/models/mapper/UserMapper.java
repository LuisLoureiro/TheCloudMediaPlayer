package models.mapper;

import models.db.User;

public class UserMapper extends AbstractMapper<String, User>
{
	@Override
	public Class<User> getClazz()
	{
		return User.class;
	}
}
