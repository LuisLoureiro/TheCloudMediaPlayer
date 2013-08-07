package models.mapper;

import models.db.PlaylistContent;
import models.db.compositeKeys.PlaylistContentKey;

public class PlaylistContentMapper extends AbstractMapper<PlaylistContentKey, PlaylistContent>
{
	@Override
	public Class<PlaylistContent> getClazz()
	{
		return PlaylistContent.class;
	}
}
