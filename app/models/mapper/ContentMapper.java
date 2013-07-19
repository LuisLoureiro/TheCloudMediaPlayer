package models.mapper;

import models.db.Content;
import models.db.compositeKeys.ContentKey;

public class ContentMapper extends AbstractMapper<ContentKey, Content>
{

	@Override
	public Class<Content> getClazz()
	{
		return Content.class;
	}
//	@Override
//	public Content findById(ContentKey id)
//	{
//		return JPA.em().find(Content.class, id);
//	}
//
//	@Override
//	public Collection<Content> findBy(String field, Object data)
//	{
//		CriteriaBuilder cbPlaylist = JPA.em().getCriteriaBuilder();
//		CriteriaQuery<Content> cqPlaylist = cbPlaylist.createQuery(Content.class);
//		Root<Content> playlist = cqPlaylist.from(Content.class);
//		return JPA.em().createQuery(
//				cqPlaylist.select(playlist)
//					.where(
//						cbPlaylist.equal(playlist.get(field), data)
//					)
//			).getResultList();
//	}
//
//	@Override
//	public Collection<Content> getAll()
//	{
//		CriteriaBuilder cbPlaylist = JPA.em().getCriteriaBuilder();
//		CriteriaQuery<Content> cqPlaylist = cbPlaylist.createQuery(Content.class);
//		return JPA.em().createQuery(
//				cqPlaylist.select(cqPlaylist.from(Content.class))
//			).getResultList();
//	}

}
