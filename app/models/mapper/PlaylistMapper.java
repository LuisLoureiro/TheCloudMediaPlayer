package models.mapper;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.db.Playlist;
import play.db.jpa.JPA;

public class PlaylistMapper extends AbstractMapper<Long, Playlist>
{
	@Override
	public Playlist findById(Long id)
	{
		return JPA.em().find(Playlist.class, id);
	}

	@Override
	public Collection<Playlist> findBy(String field, String data)
	{
		CriteriaBuilder cbPlaylist = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Playlist> cqPlaylist = cbPlaylist.createQuery(Playlist.class);
		Root<Playlist> playlist = cqPlaylist.from(Playlist.class);
		return JPA.em().createQuery(
				cqPlaylist.select(playlist)
					.where(
						cbPlaylist.equal(playlist.get(field), data)
					)
			).getResultList();
	}

	@Override
	public Collection<Playlist> getAll()
	{
		CriteriaBuilder cbPlaylist = JPA.em().getCriteriaBuilder();
		CriteriaQuery<Playlist> cqPlaylist = cbPlaylist.createQuery(Playlist.class);
		return JPA.em().createQuery(
				cqPlaylist.select(cqPlaylist.from(Playlist.class))
			).getResultList();
	}
}
