package models.mapper;

import play.db.jpa.JPA;

public abstract class AbstractMapper<K, V> implements IMapper<K, V>
{
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
