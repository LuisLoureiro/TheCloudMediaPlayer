package controllers.enums;

public enum SESSION {

	USERNAME("username"),
	FULL_NAME("full_name"),
	EMAIL("email"),
	ACCESS_TOKEN("access_token");
	
	private final String id;
	private SESSION(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
}
