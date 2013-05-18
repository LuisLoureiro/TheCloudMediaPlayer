package models.mapper;

import java.util.Collection;

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
	Collection<V> findBy(String field, String data);
	
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
}
