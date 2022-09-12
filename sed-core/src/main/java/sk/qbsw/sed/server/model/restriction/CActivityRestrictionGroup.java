package sk.qbsw.sed.server.model.restriction;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Model mapped to table public.t_restriction_group
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_restriction_group", sequenceName = "s_restriction_group", allocationSize = 1)
@Table(schema = "public", name = "t_restriction_group")
public class CActivityRestrictionGroup implements Serializable {

	@Id
	@GeneratedValue(generator = "s_restriction_group", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_activity", nullable = false)
	private CActivity activity;

	@Column(name = "c_name", length = 200, nullable = false)
	private String name;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Column(name = "c_project_group", nullable = true)
	private String projectGroup;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CClient getClient() {
		return client;
	}

	public void setClient(CClient client) {
		this.client = client;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public CUser getChangedBy() {
		return changedBy;
	}

	public void setChangedBy(CUser changedBy) {
		this.changedBy = changedBy;
	}

	public Calendar getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Calendar changeTime) {
		this.changeTime = changeTime;
	}

	public CActivity getActivity() {
		return activity;
	}

	public void setActivity(CActivity activity) {
		this.activity = activity;
	}

	public String getProjectGroup() {
		return projectGroup;
	}

	public void setProjectGroup(String projectGroup) {
		this.projectGroup = projectGroup;
	}

	public CGroupsAIData convert() {
		CGroupsAIData data = new CGroupsAIData();

		data.setId(this.getId());
		data.setName(this.getName());
		data.setValid(this.getValid());
		data.setActivityId(this.getActivity().getId());
		data.setActivityName(this.getActivity().getName());
		data.setProjectGroup(projectGroup);

		return data;
	}
}
