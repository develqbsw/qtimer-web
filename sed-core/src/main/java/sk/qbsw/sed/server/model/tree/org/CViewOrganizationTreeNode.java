package sk.qbsw.sed.server.model.tree.org;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.tree.org.CViewOrganizationTreeNodeRecord;

/**
 * Treenode used on client but also on sever side for mapping
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "v_organization_tree")
public class CViewOrganizationTreeNode implements Serializable {
	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "fk_possition_superior", nullable = true)
	private Long parentId;

	@Column(name = "user_pk_id", nullable = true)
	private Long userId;

	@Column(name = "fk_client", nullable = true)
	private Long clientId;

	@Column(name = "c_name", nullable = true)
	private String name;

	@Column(name = "c_surname", nullable = true)
	private String surname;

	@Column(name = "c_login_long", nullable = true)
	private String login;

	@Column(name = "c_flag_valid", nullable = true)
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

	public CViewOrganizationTreeNodeRecord convert() {
		CViewOrganizationTreeNodeRecord record = new CViewOrganizationTreeNodeRecord();

		record.setClientId(clientId);
		record.setId(id);
		record.setIsValid(isValid);
		record.setLogin(login);
		record.setName(name);
		record.setParentId(parentId);
		record.setSurname(surname);
		record.setUserId(userId);

		return record;
	}

	public CViewOrganizationTreeNodeRecord convertWithoutParentId() {
		CViewOrganizationTreeNodeRecord record = new CViewOrganizationTreeNodeRecord();

		record.setClientId(clientId);
		record.setId(id);
		record.setIsValid(isValid);
		record.setLogin(login);
		record.setName(name);
		record.setSurname(surname);
		record.setUserId(userId);

		return record;
	}
}
