package models.mapper;

import java.util.Collection;

import models.db.OAuth1Token;
import play.db.jpa.JPA;

public class OAuth1TokenMapper implements IMapper<String, OAuth1Token> {

	@Override
	public OAuth1Token findById(String id) {
        return JPA.em().find(OAuth1Token.class, id);
	}

	@Override
	public Collection<OAuth1Token> findBy(String field, String data) {
		return JPA.em().createQuery("SELECT u FROM oauth1_token u WHERE u."+field+" = :value", OAuth1Token.class)
				.setParameter("value", data)
				.getResultList();
	}

	@Override
	public Collection<OAuth1Token> getAll() {
    	return JPA.em().createQuery("SELECT u FROM oauth1_token u", OAuth1Token.class)
    			.getResultList();
	}

	@Override
	public void update(OAuth1Token object) {
        JPA.em().merge(object);
	}

	@Override
	public void save(OAuth1Token object) {
        JPA.em().persist(object);
	}

	@Override
	public void delete(OAuth1Token object) {
        JPA.em().remove(object);
	}

}
