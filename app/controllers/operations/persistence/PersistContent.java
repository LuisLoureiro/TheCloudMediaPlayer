package controllers.operations.persistence;

import java.util.Collection;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;

import models.db.Content;
import models.db.PlaylistContent;
import models.db.compositeKeys.ContentKey;
import models.mapper.ContentMapper;
import models.mapper.IMapper;

public class PersistContent
{
	public static void deleteOrphanContents()
	{
		/**
		 * SELECT * FROM contents c
		 * LEFT JOIN playlists_contents pc
		 * ON (c.id = pc.content_id 
		 *  AND c.provider = pc.content_provider)
		 * WHERE pc.content_id is null 
		 *  AND pc.content_provider is null;
		 */
		IMapper<ContentKey, Content> contentMapper = new ContentMapper();
		CriteriaBuilder query = contentMapper.getQueryBuilder();
		CriteriaQuery<Content> cQuery = query.createQuery(Content.class);
		Root<Content> from = cQuery.from(Content.class);
		Join<Content, PlaylistContent> join = from.join("playlists", JoinType.LEFT);
		Collection<Content> queryResults = contentMapper.executeQuery(
				cQuery.select(from)
					.where(query.isNull(join.get("content"))));
		
		for(Content content : queryResults)
		{
			if(content.getPlaylists().isEmpty())
				contentMapper.delete(content);
		}
	}
}
