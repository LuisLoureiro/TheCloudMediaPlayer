package controllers.enums;

public enum OAUTH_SERVICE_PROVIDERS {

	DROPBOX("dropbox", "Dropbox")
	, SOUNDCLOUD("soundcloud", "SoundCloud")
//	, YOUTUBE("youtube", "YouTube")
//	, CLOUDPT("cloudpt", "CloudPT")
	;
	
	private final String lowerCase
		, bestCase;
	
	private OAUTH_SERVICE_PROVIDERS(String lowerCase, String bestCase) {
		this.lowerCase = lowerCase;
		this.bestCase = bestCase;
	}
	
	public String getBestCase() {
		return bestCase;
	}
	
	@Override
	public String toString() {
		return lowerCase;
	}
}
