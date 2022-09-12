package sk.qbsw.sed.client.model.registration;

import java.io.Serializable;

import sk.qbsw.sed.client.model.security.CClientInfo;

/**
 * 
 * Object for transfering User data
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
public class CRegistrationUserRecord implements Serializable {
	private Long id;
	private CClientInfo clientInfo;
	private String email;
	private String login;
	private String name;
	private String password;
	private String phoneFix;
	private String surname;
	private Long userType;
	private Boolean isValid;
	private Boolean isMain;

	public CClientInfo getClientInfo() {
		return this.clientInfo;
	}

	public void setClientInfo(final CClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	public Boolean getIsMain() {
		return this.isMain;
	}

	public void setIsMain(final Boolean isMain) {
		this.isMain = isMain;
	}

	public Boolean getIsValid() {
		return this.isValid;
	}

	public void setIsValid(final Boolean isValid) {
		this.isValid = isValid;
	}

	public Long getUserType() {
		return this.userType;
	}

	public void setUserType(final Long userType) {
		this.userType = userType;
	}

	public String getEmail() {
		return this.email;
	}

	public String getLogin() {
		return this.login;
	}

	public String getName() {
		return this.name;
	}

	public String getPassword() {
		return this.password;
	}

	public String getPhoneFix() {
		return this.phoneFix;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public void setPhoneFix(final String phoneFix) {
		this.phoneFix = phoneFix;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
