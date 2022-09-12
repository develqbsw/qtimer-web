package sk.qbsw.sed.server.model.codelist;

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

import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Model mapped to table public.t_ct_activity
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_ct_activity", sequenceName = "s_ct_activity", allocationSize = 1)
@Table(schema = "public", name = "t_ct_activity")
public class CActivity implements Serializable {
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Id
	@GeneratedValue(generator = "s_ct_activity", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_note", nullable = true)
	private String note;

	@Column(name = "c_client_order", nullable = true)
	private Integer order;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_flag_working", nullable = false)
	private Boolean working;

	@Column(name = "c_flag_changeable", nullable = false)
	private Boolean changeable;

	@Column(name = "c_flag_default", nullable = false)
	private Boolean flagDefault;

	@Column(name = "c_time_min", nullable = true)
	private Calendar timeMin;

	@Column(name = "c_time_max", nullable = true)
	private Calendar timeMax;

	@Column(name = "c_hours_max", nullable = true)
	private Integer hoursMax;

	@Column(name = "c_flag_export", nullable = true)
	private Boolean flagExport;

	@Column(name = "c_flag_sum", nullable = true)
	private Boolean flagSum;

	public CClient getClient() {
		return this.client;
	}

	public Calendar getChangeTime() {
		return this.changeTime;
	}

	public Long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getOrder() {
		return this.order;
	}

	public Boolean getValid() {
		return this.valid;
	}

	public Boolean getWorking() {
		return this.working;
	}

	public void setClient(final CClient client) {
		this.client = client;
	}

	public void setChangeTime(final Calendar changeTime) {
		this.changeTime = changeTime;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setOrder(final Integer order) {
		this.order = order;
	}

	public void setValid(final Boolean valid) {
		this.valid = valid;
	}

	public void setWorking(final Boolean working) {
		this.working = working;
	}

	public CUser getChangedBy() {
		return this.changedBy;
	}

	public void setChangedBy(final CUser changedBy) {
		this.changedBy = changedBy;
	}

	public Boolean getChangeable() {
		return this.changeable;
	}

	public void setChangeable(final Boolean changeable) {
		this.changeable = changeable;
	}

	public Boolean getFlagDefault() {
		return flagDefault;
	}

	public void setFlagDefault(Boolean flagDefault) {
		this.flagDefault = flagDefault;
	}

	public Calendar getTimeMin() {
		return timeMin;
	}

	public void setTimeMin(Calendar timeMin) {
		this.timeMin = timeMin;
	}

	public Calendar getTimeMax() {
		return timeMax;
	}

	public void setTimeMax(Calendar timeMax) {
		this.timeMax = timeMax;
	}

	public Integer getHoursMax() {
		return hoursMax;
	}

	public void setHoursMax(Integer hoursMax) {
		this.hoursMax = hoursMax;
	}

	public Boolean getFlagExport() {
		return flagExport;
	}

	public void setFlagExport(Boolean flagExport) {
		this.flagExport = flagExport;
	}

	public Boolean getFlagSum() {
		return flagSum;
	}

	public void setFlagSum(Boolean flagSum) {
		this.flagSum = flagSum;
	}

	/**
	 * Converts model for client
	 * 
	 * @param activity
	 * @return
	 */
	public CActivityRecord convert() {
		final CActivityRecord retVal = new CActivityRecord();

		retVal.setId(this.getId());
		retVal.setActive(this.getValid());
		retVal.setChangeName(this.getChangedBy().getName());
		retVal.setChangeSurname(this.getChangedBy().getSurname());
		retVal.setChangeTime(this.getChangeTime().getTime());
		retVal.setName(this.getName());
		retVal.setNote(this.getNote());
		retVal.setFlagDefault(this.getFlagDefault());

		if (this.getOrder() != null) {
			retVal.setOrder((int) this.getOrder());
		} else {
			retVal.setOrder(null);
		}
		retVal.setWorking(this.getWorking());
		retVal.setChangeable(changeable);
		retVal.setTimeMin(timeMin == null ? null : timeMin.getTime());
		retVal.setTimeMax(timeMax == null ? null : timeMax.getTime());
		retVal.setHoursMax(hoursMax);
		retVal.setFlagExport(flagExport);
		retVal.setFlagSum(flagSum);

		return retVal;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		CActivity other = (CActivity) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}
}
