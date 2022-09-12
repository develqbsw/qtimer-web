package sk.qbsw.sed.server.model.codelist;

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

import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.server.model.domain.CClient;
import sk.qbsw.sed.server.model.domain.CTimeSheetRecord;
import sk.qbsw.sed.server.model.domain.CUser;

/**
 * Model mapped to table public.t_ct_project
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_ct_project", sequenceName = "s_ct_project", allocationSize = 1)
@Table(schema = "public", name = "t_ct_project")
public class CProject implements Serializable {
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Id
	@GeneratedValue(generator = "s_ct_project", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "c_name", nullable = false)
	private String name;

	@Column(name = "c_note", nullable = true)
	private String note;

	@Column(name = "c_client_order", nullable = true)
	private Integer order;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "project")
	private List<CTimeSheetRecord> timeSheetRecord = new ArrayList<>(0);

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_group", nullable = false)
	private String group;

	@Column(name = "c_id", nullable = false)
	private String eviproCode;

	@Column(name = "c_flag_default", nullable = false)
	private Boolean flagDefault;

	public CClient getClient() {
		return client;
	}

	public CUser getChangedBy() {
		return changedBy;
	}

	public Calendar getChangeTime() {
		return changeTime;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getNote() {
		return note;
	}

	public Integer getOrder() {
		return order;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setClient(CClient client) {
		this.client = client;
	}

	public void setChangedBy(CUser changedBy) {
		this.changedBy = changedBy;
	}

	public void setChangeTime(Calendar changeTime) {
		this.changeTime = changeTime;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public List<CTimeSheetRecord> getTimeSheetRecord() {
		return timeSheetRecord;
	}

	public void setTimeSheetRecord(List<CTimeSheetRecord> timeSheetRecord) {
		this.timeSheetRecord = timeSheetRecord;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getEviproCode() {
		return eviproCode;
	}

	public void setEviproCode(String eviproCode) {
		this.eviproCode = eviproCode;
	}

	public Boolean getFlagDefault() {
		return flagDefault;
	}

	public void setFlagDefault(Boolean flagDefault) {
		this.flagDefault = flagDefault;
	}

	/**
	 * Converts model for client
	 * 
	 * @param project
	 * @return
	 */
	public CProjectRecord convert() {
		final CProjectRecord retVal = new CProjectRecord();

		retVal.setId(this.getId());
		retVal.setActive(this.getValid());
		retVal.setChangeName(this.getChangedBy().getName());
		retVal.setChangeSurname(this.getChangedBy().getSurname());
		retVal.setChangeTime(this.getChangeTime().getTime());
		retVal.setName(this.getName());
		retVal.setNote(this.getNote());

		if (this.getOrder() != null) {
			retVal.setOrder((int) this.getOrder());
		} else {
			retVal.setOrder(null);
		}
		retVal.setFlagDefault(this.getFlagDefault());
		
		retVal.setEviproCode(this.getEviproCode());

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
		CProject other = (CProject) obj;
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
