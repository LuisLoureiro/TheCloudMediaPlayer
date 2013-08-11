package controllers.operations.persistence;

import java.util.Collection;
import java.util.List;

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
	private static final IMapper<ContentKey, Content> CONTENT_MAPPER = new ContentMapper();
	
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
		CriteriaBuilder query = CONTENT_MAPPER.getQueryBuilder();
		CriteriaQuery<Content> cQuery = query.createQuery(Content.class);
		Root<Content> from = cQuery.from(Content.class);
		Join<Content, PlaylistContent> join = from.join("playlists", JoinType.LEFT);
		Collection<Content> queryResults = CONTENT_MAPPER.executeQuery(
				cQuery.select(from)
					.where(query.isNull(join.get("content"))));
		
		for(Content content : queryResults)
		{
				CONTENT_MAPPER.delete(content);
		}
	}
	
	static Content findIfNullSave(String id, String provider)
	{
		ContentKey key = new ContentKey(id, provider);
		Content content = CONTENT_MAPPER.findById(key);
		if(content == null)
		{
			content = new Content(key, null);
			CONTENT_MAPPER.save(content);
		}
		
		return content;
	}
	
	static void deleteContentsWithoutPlaylist(List<PlaylistContent> contents)
	{
		for(PlaylistContent playlistContent : contents)
		{
			if(playlistContent.getContent().getPlaylists().isEmpty())
				CONTENT_MAPPER.delete(playlistContent.getContent());
		}
	}
}
