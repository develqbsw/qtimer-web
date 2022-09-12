package sk.qbsw.sed.server.model.codelist;

/**
 * class used for sending localized emails
 * 
 * @author lobb
 *
 */
public class CEmailWithLanguage {

	private String email;
	private String language;

	public CEmailWithLanguage(String email, String language) {
		this.email = email;
		this.language = language;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
