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

import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Model mapped to table public.t_activity_interval
 * 
 * @author rosenberg
 * @version 1.0
 * @since 1.6.6.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_activity_interval", sequenceName = "s_activity_interval", allocationSize = 1)
@Table(schema = "public", name = "t_activity_interval")
public class CActivityInterval implements Serializable {

	@Id
	@GeneratedValue(generator = "s_activity_interval", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	CClient client;

	@Column(name = "c_name", length = 200, nullable = false)
	String name;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_activity", nullable = false)
	CActivity activity;

	@Column(name = "c_time_from", nullable = true)
	Calendar time_from;

	@Column(name = "c_time_to", nullable = true)
	Calendar time_to;

	/**
	 * allowed values: 0 - work day, 1 - free day
	 */
	@Column(name = "c_day_type", nullable = false)
	Long dateType;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_activity_restr_grp", nullable = false)
	CActivityRestrictionGroup group;

	@Column(name = "c_date_from", nullable = true)
	Calendar date_from;

	@Column(name = "c_date_to", nullable = true)
	Calendar date_to;

	@Column(name = "c_flag_valid", nullable = false)
	Boolean valid;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

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

	public CActivity getActivity() {
		return activity;
	}

	public void setActivity(CActivity activity) {
		this.activity = activity;
	}

	public Calendar getDate_from() {
		return date_from;
	}

	public void setDate_from(Calendar date_from) {
		this.date_from = date_from;
	}

	public Calendar getDate_to() {
		return date_to;
	}

	public void setDate_to(Calendar date_to) {
		this.date_to = date_to;
	}

	public Calendar getTime_from() {
		return time_from;
	}

	public void setTime_from(Calendar time_from) {
		this.time_from = time_from;
	}

	public Calendar getTime_to() {
		return time_to;
	}

	public void setTime_to(Calendar time_to) {
		this.time_to = time_to;
	}

	public Long getDateType() {
		return dateType;
	}

	public void setDateType(Long dateType) {
		this.dateType = dateType;
	}

	public CActivityRestrictionGroup getGroup() {
		return group;
	}

	public void setGroup(CActivityRestrictionGroup group) {
		this.group = group;
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

	public CActivityIntervalData convert() {
		CActivityIntervalData data = new CActivityIntervalData();

		data.setId(this.getId());
		data.setName(this.getName());
		data.setValid(this.getValid());
		data.setActivityId(this.getActivity().getId());
		data.setActivityName(this.getActivity().getName());

		if (this.getDate_from() != null) {
			data.setDateValidFrom(this.getDate_from().getTime());
		}
		if (this.getDate_to() != null) {
			data.setDateValidTo(this.getDate_to().getTime());
		}
		data.setDayTypeId(this.getDateType());

		data.setGroupId(this.getGroup().getId());
		data.setGroupName(this.getGroup().getName());

		if (this.getTime_from() != null) {
			data.setTimeFrom(this.getTime_from().getTime());
		}
		if (this.getTime_to() != null) {
			data.setTimeTo(this.getTime_to().getTime());
		}
		return data;
	}
}
