package controllers.enums;

public enum SESSION {

	USERNAME("username")
	, FULL_NAME("full_name")
	, EMAIL("email")
	, ACCESS_TOKEN("access_token")
	, PROVIDER("provider") // The authentication provider used to authenticate in the application.
	;
	
	private final String id;
	private SESSION(String id) {
		this.id = id;
	}
	public String toString(){
		return id;
	}
}
