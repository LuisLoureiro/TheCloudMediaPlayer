package models.mapper;

import models.database.OAuth1User;

public class OAuth1UserMapper extends AbstractMapper<String, OAuth1User>
{
	@Override
	public Class<OAuth1User> getClazz()
	{
		return OAuth1User.class;
	}
}
