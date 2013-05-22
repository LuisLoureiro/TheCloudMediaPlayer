package models.mapper;

import java.util.Collection;

import models.db.OAuth1User;
import play.db.jpa.JPA;

public class OAuth1UserMapper implements IMapper<String, OAuth1User> {

	@Override
	public OAuth1User findById(String id) {
        return JPA.em().find(OAuth1User.class, id);
	}

	@Override
	public Collection<OAuth1User> findBy(String field, String data) {
		return JPA.em().createQuery("SELECT u.id FROM oauth1_users u WHERE u."+field+" = :value", OAuth1User.class)
				.setParameter("value", data)
				.getResultList();
	}

	@Override
	public Collection<OAuth1User> getAll() {
    	return JPA.em().createQuery("SELECT * FROM oauth1_users", OAuth1User.class)
    			.getResultList();
	}

	@Override
	public void update(OAuth1User object) {
        JPA.em().merge(object);
	}

	@Override
	public void save(OAuth1User object) {
        JPA.em().persist(object);
	}

	@Override
	public void delete(OAuth1User object) {
        JPA.em().remove(object);
	}

}
