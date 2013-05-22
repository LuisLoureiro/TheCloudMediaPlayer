package models.mapper;

import java.util.Collection;

import models.db.User;
import play.db.jpa.JPA;

public class UserMapper implements IMapper<String, User> {

	@Override
	public User findById(String id) {
        return JPA.em().find(User.class, id);
	}

	@Override
	public Collection<User> findBy(String field, String data) {
		return JPA.em().createQuery("SELECT u.id FROM users u WHERE u."+field+" = :value", User.class)
				.setParameter("value", data)
				.getResultList();
	}

	@Override
	public Collection<User> getAll() {
    	return JPA.em().createQuery("SELECT * FROM users", User.class)
    			.getResultList();
	}

	@Override
	public void update(User object) {
        JPA.em().merge(object);
	}

	@Override
	public void save(User object) {
        JPA.em().persist(object);
	}

	@Override
	public void delete(User object) {
        JPA.em().remove(object);
	}
}
