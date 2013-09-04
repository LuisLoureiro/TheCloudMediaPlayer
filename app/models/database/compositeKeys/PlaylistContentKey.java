package models.database.compositeKeys;

import java.io.Serializable;

public class PlaylistContentKey implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1350652865362396511L;
	private long playlistId;
	private String contentId;
	private String contentProvider;
	private int position;
	
	public PlaylistContentKey(){}
	
	public PlaylistContentKey(long playlistId, String contentId, String contentProvider, int position)
	{
		this.playlistId = playlistId;
		this.contentId = contentId;
		this.contentProvider = contentProvider;
		this.position = position;
	}
	
	public long getPlaylistId() {
		return playlistId;
	}
	public void setPlaylistId(long playlistId) {
		this.playlistId = playlistId;
	}
	public String getContentId() {
		return contentId;
	}
	public void setContentId(String contentId) {
		this.contentId = contentId;
	}
	public String getContentProvider() {
		return contentProvider;
	}
	public void setContentProvider(String contentProvider) {
		this.contentProvider = contentProvider;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contentId == null) ? 0 : contentId.hashCode());
		result = prime
				* result
				+ ((contentProvider == null) ? 0 : contentProvider.hashCode());
		result = prime * result + position;
		result = prime * result + (int) (playlistId ^ (playlistId >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlaylistContentKey other = (PlaylistContentKey) obj;
		if (contentId == null) {
			if (other.contentId != null)
				return false;
		} else if (!contentId.equals(other.contentId))
			return false;
		if (contentProvider == null) {
			if (other.contentProvider != null)
				return false;
		} else if (!contentProvider.equals(other.contentProvider))
			return false;
		if (position != other.position)
			return false;
		if (playlistId != other.playlistId)
			return false;
		return true;
	}
}
