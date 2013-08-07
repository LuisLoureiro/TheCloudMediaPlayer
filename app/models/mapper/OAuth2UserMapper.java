package models.mapper;

import models.db.OAuth2User;

public class OAuth2UserMapper extends AbstractMapper<String, OAuth2User>
{
	@Override
	public Class<OAuth2User> getClazz()
	{
		return OAuth2User.class;
	}
}
