package controllers.operations.authentication.enums;

// Para se ver os URIs dos atributos
// http://test-id.net/OP/AXFetch.aspx
// http://openid.net/specs/openid-attribute-properties-list-1_0-01.html
// https://groups.google.com/forum/?fromgroups=#!topic/openid4java/kcus-G7y8wY
// http://stackoverflow.com/questions/7403536/list-of-available-attributes-for-http-axschema-org-and-http-schemas-openid-n
// http://stackoverflow.com/questions/887630/is-there-a-list-of-which-openid-providers-use-which-schema-attributes?rq=1
// http://openid.net/specs/openid-simple-registration-extension-1_0.html
// http://stackoverflow.com/questions/3284312/difference-between-http-schema-openid-net-contact-email-and-http-axschema-or?rq=1
// https://developers.google.com/google-apps/marketplace/best_practices#openid_post
public enum OPENID_ATTRIBUTES {

	EMAIL("email", "email.1", "http://schema.openid.net/contact/email"),
	EMAIL_AX("email_ax", "email_ax.1", "http://axschema.org/contact/email"),
	EMAIL_OP("email_op", "email_op.1", "http://openid.net/schema/contact/internet/email"),
	FULL_NAME_AX("fullname_ax", "fullname_ax.1", "http://axschema.org/namePerson"),
	FRIENDLY_NAME("friendly", "friendly.1", "http://schema.openid.net/namePerson/friendly"),
	FRIENDLY_NAME_OP("friendly_op", "friendly_op.1", "http://openid.net/schema/namePerson/friendly");
//		FIRST_NAME("firstname", "http://schema.openid.net/namePerson/first"),
//		FIRST_NAME_AX("firstname_ax", "http://axschema.org/namePerson/first"),
//		FIRST_NAME_OP("firstname_op", "http://openid.net/schema/namePerson/first"),
//		LAST_NAME("lastname", "http://schema.openid.net/namePerson/last"),
//		LAST_NAME_AX("lastname_ax", "http://axschema.org/namePerson/last"),
//		LAST_NAME_OP("lastname_op", "http://openid.net/schema/namePerson/last,");
	
	private final String name, nameWithIndex, uri;
	private OPENID_ATTRIBUTES(String name, String nameWithIndex, String uri) {
		this.name = name;
		this.nameWithIndex = nameWithIndex;
		this.uri = uri;
	}
	public String getName() {
		return name;
	}
	public String getUri() {
		return uri;
	}
	public String getNameWithIndex() {
		return nameWithIndex;
	}
}
