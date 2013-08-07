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
}
