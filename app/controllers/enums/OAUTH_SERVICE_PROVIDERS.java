package controllers.enums;

import java.util.Arrays;
import java.util.List;

public enum OAUTH_SERVICE_PROVIDERS
{
	DROPBOX("dropbox", "Dropbox")
		, SOUNDCLOUD("soundcloud", "SoundCloud")
//		, YOUTUBE("youtube", "YouTube")
//		, CLOUDPT("cloudpt", "CloudPT")
	;
	
	private final String	lowerCase
							, bestCase;
	
	private OAUTH_SERVICE_PROVIDERS(String lowerCase, String bestCase)
	{
		this.lowerCase = lowerCase;
		this.bestCase = bestCase;
	}
	
	public String getLowerCase()
	{
		return lowerCase;
	}
	
	public String getBestCase()
	{
		return bestCase;
	}
	
	@Override
	public String toString()
	{
		return lowerCase;
	}
	
	public static List<String> getAllLowerCase()
	{
		return Arrays.asList(DROPBOX.lowerCase, SOUNDCLOUD.lowerCase);
	}
	
	public static List<String> getAllBestCase()
	{
		return Arrays.asList(DROPBOX.bestCase, SOUNDCLOUD.bestCase);
	}
}
