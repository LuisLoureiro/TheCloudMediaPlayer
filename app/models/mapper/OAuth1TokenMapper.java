package models.mapper;

import models.database.OAuth1Token;

public class OAuth1TokenMapper extends AbstractMapper<String, OAuth1Token>
{
	public Class<OAuth1Token> getClazz()
	{
		return OAuth1Token.class;
	}
}
