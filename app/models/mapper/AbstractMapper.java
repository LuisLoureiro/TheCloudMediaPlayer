package models.mapper;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;

public abstract class AbstractMapper<K, V> implements IMapper<K, V>
{
	public abstract Class<V> getClazz();
	
	public V findById(K id)
	{
		return JPA.em().find(getClazz(), id);
	}
	
	public Collection<V> findBy(String field, Object data)
	{
		CriteriaBuilder cBuilder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<V> cQuery = cBuilder.createQuery(getClazz());
		Root<V> root = cQuery.from(getClazz());
		return JPA.em().createQuery(
				cQuery.select(root)
					.where(
						cBuilder.equal(root.get(field), data)
					)
			).getResultList();
	}
	
	public CriteriaBuilder getQueryBuilder()
	{
		return JPA.em().getCriteriaBuilder();
	}
	
	public Collection<V> executeQuery(CriteriaQuery<V> query)
	{
		return JPA.em().createQuery(query).getResultList();
	}
	
	public Collection<V> getAll()
	{
		CriteriaBuilder cBuilder = JPA.em().getCriteriaBuilder();
		CriteriaQuery<V> cQuery = cBuilder.createQuery(getClazz());
		return JPA.em().createQuery(
				cQuery.select(cQuery.from(getClazz()))
			).getResultList();
	}
	
	public void save(V object)
	{
		JPA.em().persist(object);
	}
	
	public void update(V object)
	{
		JPA.em().merge(object);
	}
	
	public void delete(V object)
	{
		JPA.em().remove(object);
	}
	
	public void sync()
	{
		JPA.em().flush();
	}
}
