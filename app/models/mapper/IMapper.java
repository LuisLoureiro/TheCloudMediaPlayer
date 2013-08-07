package models.mapper;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

public interface IMapper<T, V> {

	/**
	 * 
	 * @param id The value of the entity id to search for.
	 * @return The object corresponding to the provided id or null if none is found.
	 */
	V findById(T id);
	
	/**
	 * 
	 * @param field The name of the field used to search for.
	 * @param data The value of the field used to search for.
	 * @return All the objects that have the field equal to the value.
	 */
	Collection<V> findBy(String field, Object data);
	
	/**
	 * 
	 * @return An object to use for the creation of Criteria API queries.
	 */
	CriteriaBuilder getQueryBuilder();
	
	/**
	 * 
	 * @param query Criteria API query to execute in the database.
	 * @return The result of the query execution.
	 */
	Collection<V> executeQuery(CriteriaQuery<V> query);
	/**
	 * 
	 * @return All the existing objects.
	 */
	Collection<V> getAll();
	
	/**
	 * 
	 * @param object The object to be updated.
	 */
	void update(V object);
	
	/**
	 * 
	 * @param object The object to be saved.
	 */
	void save(V object);
	
	/**
	 * 
	 * @param object The object to be deleted.
	 */
	void delete(V object);
	
	/*
	 * Forces a synchronization of the persistence context to the database.
	 */
	void sync();
}
