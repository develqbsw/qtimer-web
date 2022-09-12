package sk.qbsw.sed.client.model.tree.org;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CViewOrganizationTreeNodeRecord implements Serializable {

	private Long id;
	private Long parentId;
	private Long userId;
	private Long clientId;
	private String name;
	private String surname;
	private String login;
	private Boolean isValid;

	public Long getId() {
		return this.id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return this.parentId;
	}

	public void setParentId(final Long parentId) {
		this.parentId = parentId;
	}

	public Long getUserId() {
		return this.userId;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public Long getClientId() {
		return this.clientId;
	}

	public void setClientId(final Long clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(final String surname) {
		this.surname = surname;
	}

	public String getLogin() {
		return this.login;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

	public Boolean getIsValid() {
		return this.isValid;
	}

	public void setIsValid(final Boolean isValid) {
		this.isValid = isValid;
	}

	@Override
	public String toString() {
		return name + " " + surname;
	}
}
