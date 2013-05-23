package models.mapper;

import java.util.Collection;

import models.db.OAuth2User;
import play.db.jpa.JPA;

public class OAuth2UserMapper implements IMapper<String, OAuth2User> {

	@Override
	public OAuth2User findById(String id) {
        return JPA.em().find(OAuth2User.class, id);
	}

	@Override
	public Collection<OAuth2User> findBy(String field, String data) {
		return JPA.em().createQuery("SELECT u FROM oauth2_users u WHERE u."+field+" = :value", OAuth2User.class)
				.setParameter("value", data)
				.getResultList();
	}

	@Override
	public Collection<OAuth2User> getAll() {
    	return JPA.em().createQuery("SELECT u FROM oauth2_users u", OAuth2User.class)
    			.getResultList();
	}

	@Override
	public void update(OAuth2User object) {
        JPA.em().merge(object);
	}

	@Override
	public void save(OAuth2User object) {
        JPA.em().persist(object);
	}

	@Override
	public void delete(OAuth2User object) {
        JPA.em().remove(object);
	}

}
