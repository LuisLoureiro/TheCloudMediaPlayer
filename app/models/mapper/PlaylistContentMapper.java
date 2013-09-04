package models.mapper;

import models.database.PlaylistContent;
import models.database.compositeKeys.PlaylistContentKey;

public class PlaylistContentMapper extends AbstractMapper<PlaylistContentKey, PlaylistContent>
{
	@Override
	public Class<PlaylistContent> getClazz()
	{
		return PlaylistContent.class;
	}
}
