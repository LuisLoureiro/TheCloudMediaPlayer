package models.beans.dataBinding;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

//can be used to indicate that certain properties are to be ignored for serialization and/or deserialization (handling differs a bit depending on which operation is affected):
@JsonIgnoreProperties(ignoreUnknown=true)
public class Playlist
{
	private Track[] tracks;

	public Track[] getTracks() {
		return tracks;
	}

	public void setTracks(Track[] tracks) {
		this.tracks = tracks;
	}
}
