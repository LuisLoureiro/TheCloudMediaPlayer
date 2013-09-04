package models.mapper;

import models.database.Playlist;

public class PlaylistMapper extends AbstractMapper<Long, Playlist>
{
	@Override
	public Class<Playlist> getClazz()
	{
		return Playlist.class;
	}
}
