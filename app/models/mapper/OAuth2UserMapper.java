package models.mapper;

import java.util.Collection;

import models.db.OAuth2User;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;

public class OAuth2UserMapper implements IMapper<String, OAuth2User> {

	@Override
	@Transactional
	public OAuth2User findById(String id) {
        return JPA.em().find(OAuth2User.class, id);
	}

	@Override
	@Transactional
	public Collection<OAuth2User> findBy(String field, String data) {
		return JPA.em().createQuery("SELECT u.id FROM oauth2_users u WHERE u."+field+" = :value", OAuth2User.class)
				.setParameter("value", data)
				.getResultList();
	}

	@Override
	@Transactional
	public Collection<OAuth2User> getAll() {
    	return JPA.em().createQuery("SELECT * FROM oauth2_users", OAuth2User.class)
    			.getResultList();
	}

	@Override
	@Transactional
	public void update(OAuth2User object) {
        JPA.em().merge(object);
	}

	@Override
	@Transactional
	public void save(OAuth2User object) {
        JPA.em().persist(object);
	}

	@Override
	@Transactional
	public void delete(OAuth2User object) {
        JPA.em().remove(object);
	}

}
