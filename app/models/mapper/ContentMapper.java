package models.mapper;

import models.database.Content;
import models.database.compositeKeys.ContentKey;

public class ContentMapper extends AbstractMapper<ContentKey, Content>
{
	@Override
	public Class<Content> getClazz()
	{
		return Content.class;
	}
}
