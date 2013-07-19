package models.mapper;

import models.db.OAuth1User;

public class OAuth1UserMapper extends AbstractMapper<String, OAuth1User>
{
	@Override
	public Class<OAuth1User> getClazz()
	{
		return OAuth1User.class;
	}

//	@Override
//	public OAuth1User findById(String id) {
//        return JPA.em().find(OAuth1User.class, id);
//	}
//
//	@Override
//	public Collection<OAuth1User> findBy(String field, Object data) {
//		return JPA.em().createQuery("SELECT u FROM oauth1_users u WHERE u."+field+" = :value", OAuth1User.class)
//				.setParameter("value", data)
//				.getResultList();
//	}
//
//	@Override
//	public Collection<OAuth1User> getAll() {
//    	return JPA.em().createQuery("SELECT u FROM oauth1_users u", OAuth1User.class)
//    			.getResultList();
//	}
}
