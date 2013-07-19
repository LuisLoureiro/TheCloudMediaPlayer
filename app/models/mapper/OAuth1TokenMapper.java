package models.mapper;

import models.db.OAuth1Token;

public class OAuth1TokenMapper extends AbstractMapper<String, OAuth1Token>
{
	public Class<OAuth1Token> getClazz()
	{
		return OAuth1Token.class;
	}
//	@Override
//	public OAuth1Token findById(String id) {
//        return JPA.em().find(OAuth1Token.class, id);
//	}
//
//	@Override
//	public Collection<OAuth1Token> findBy(String field, Object data) {
//		return JPA.em().createQuery("SELECT u FROM oauth1_token u WHERE u."+field+" = :value", OAuth1Token.class)
//				.setParameter("value", data)
//				.getResultList();
//	}
//
//	@Override
//	public Collection<OAuth1Token> getAll() {
//    	return JPA.em().createQuery("SELECT u FROM oauth1_token u", OAuth1Token.class)
//    			.getResultList();
//	}
}
