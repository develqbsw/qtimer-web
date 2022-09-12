package sk.qbsw.sed.server.model.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * Model mapped to table public.t_organization_tree
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_organization_tree", sequenceName = "s_organization_tree", allocationSize = 1)
@Table(schema = "public", name = "t_organization_tree")
public class COrganizationTree implements Serializable {
	
	@Id
	@GeneratedValue(generator = "s_organization_tree", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_owner", nullable = false)
	private CUser owner;

	@Column(name = "c_position_name", nullable = true)
	private String position;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_possition_superior", nullable = true)
	private COrganizationTree superior;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "superior")
	private List<COrganizationTree> organizationTreesSubordinate = new ArrayList<>(0);

	public Long getId() {
		return this.id;
	}

	public List<COrganizationTree> getOrganizationTreesSubordinate() {
		return this.organizationTreesSubordinate;
	}

	public void setOrganizationTreesSubordinate(final List<COrganizationTree> organizationTreesSubordinate) {
		this.organizationTreesSubordinate = organizationTreesSubordinate;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public CClient getClient() {
		return this.client;
	}

	public void setClient(final CClient client) {
		this.client = client;
	}

	public String getPossition() {
		return this.position;
	}

	public CUser getOwner() {
		return this.owner;
	}

	public void setOwner(final CUser owner) {
		this.owner = owner;
	}

	public CUser getChangedBy() {
		return this.changedBy;
	}

	public void setChangedBy(final CUser changedBy) {
		this.changedBy = changedBy;
	}

	public void setPossition(final String possition) {
		this.position = possition;
	}

	public COrganizationTree getSuperior() {
		return this.superior;
	}

	public void setSuperior(final COrganizationTree superior) {
		this.superior = superior;
	}

	public Calendar getChangeTime() {
		return this.changeTime;
	}

	public void setChangeTime(final Calendar changeTime) {
		this.changeTime = changeTime;
	}
}
