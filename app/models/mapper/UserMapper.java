package models.mapper;

import models.db.User;

public class UserMapper extends AbstractMapper<String, User>
{
	@Override
	public Class<User> getClazz()
	{
		return User.class;
	}
	
//	@Override
//	public User findById(String id) {
//        return JPA.em().find(User.class, id);
//	}
//
//	@Override
//	public Collection<User> findBy(String field, Object data) {
//		return JPA.em().createQuery("SELECT u FROM users u WHERE u."+field+" = :value", User.class)
//				.setParameter("value", data)
//				.getResultList();
//	}
//
//	@Override
//	public Collection<User> getAll() {
//    	return JPA.em().createQuery("SELECT u FROM users u", User.class)
//    			.getResultList();
//	}
}
