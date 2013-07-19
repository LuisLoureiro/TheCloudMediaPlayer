package models.mapper;

import models.db.Playlist;

public class PlaylistMapper extends AbstractMapper<Long, Playlist>
{
	@Override
	public Class<Playlist> getClazz()
	{
		return Playlist.class;
	}
//	@Override
//	public Playlist findById(Long id)
//	{
//		return JPA.em().find(Playlist.class, id);
//	}
//
//	@Override
//	public Collection<Playlist> findBy(String field, Object data)
//	{
//		CriteriaBuilder cbPlaylist = JPA.em().getCriteriaBuilder();
//		CriteriaQuery<Playlist> cqPlaylist = cbPlaylist.createQuery(Playlist.class);
//		Root<Playlist> playlist = cqPlaylist.from(Playlist.class);
//		return JPA.em().createQuery(
//				cqPlaylist.select(playlist)
//					.where(
//						cbPlaylist.equal(playlist.get(field), data)
//					)
//			).getResultList();
//	}
//
//	@Override
//	public Collection<Playlist> getAll()
//	{
//		CriteriaBuilder cbPlaylist = JPA.em().getCriteriaBuilder();
//		CriteriaQuery<Playlist> cqPlaylist = cbPlaylist.createQuery(Playlist.class);
//		return JPA.em().createQuery(
//				cqPlaylist.select(cqPlaylist.from(Playlist.class))
//			).getResultList();
//	}
}
