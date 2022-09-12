package sk.qbsw.sed.server.model.domain;

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

import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.server.model.codelist.CActivity;
import sk.qbsw.sed.server.model.codelist.CProject;
import sk.qbsw.sed.server.model.codelist.CRequestReason;
import sk.qbsw.sed.server.model.codelist.CTimeSheetRecordStatus;

/**
 * Model mapped to table public.t_time_sheet_record
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
@Entity
@SequenceGenerator(name = "s_time_sheet_record", sequenceName = "s_time_sheet_record", allocationSize = 1)
@Table(schema = "public", name = "t_time_sheet_record")
public class CTimeSheetRecord implements Serializable, ITimeSheetRecord {
	
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_activity", nullable = false)
	private CActivity activity;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_client", nullable = false)
	private CClient client;

	@Column(name = "c_datetime_changed", nullable = false)
	private Calendar changeTime;

	@Id
	@GeneratedValue(generator = "s_time_sheet_record", strategy = GenerationType.SEQUENCE)
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_project", nullable = true)
	private CProject project;

	@Column(name = "c_time_from", nullable = false)
	private Calendar timeFrom;

	@Column(name = "c_time_to", nullable = true)
	private Calendar timeTo;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_createdby", nullable = false)
	private CUser createdBy;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_changedby", nullable = false)
	private CUser changedBy;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_user_owner", nullable = false)
	private CUser owner;

	@Column(name = "c_flag_valid", nullable = false)
	private Boolean valid;

	@Column(name = "c_note", nullable = true)
	private String note;

	@Column(name = "c_phase", nullable = true)
	private String phase;

	@Column(name = "c_flag_outside", nullable = true)
	private Boolean outside;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_reason", nullable = true)
	private CRequestReason reason;

	@Column(name = "c_flag_last", nullable = true)
	private Boolean last;

	@Column(name = "c_flag_last_working", nullable = true)
	private Boolean lastWorking;

	@Column(name = "c_flag_last_nonworking", nullable = true)
	private Boolean lastNonWorking;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_status", nullable = false)
	private CTimeSheetRecordStatus status;

	@Column(name = "c_flag_home_office", nullable = true)
	private Boolean homeOffice;

	public CActivity getActivity() {
		return activity;
	}

	public CClient getClient() {
		return client;
	}

	public CUser getCreatedBy() {
		return createdBy;
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

	public String getNote() {
		return note;
	}

	public CUser getOwner() {
		return owner;
	}

	public CProject getProject() {
		return project;
	}

	public Calendar getTimeFrom() {
		return timeFrom;
	}

	public Calendar getTimeTo() {
		return timeTo;
	}

	public Boolean getValid() {
		return valid;
	}

	/**
	 * Identifies if record has starting and ending date filled
	 * 
	 * @return true/false
	 */
	public boolean isFullyFilled() {
		return getTimeTo() != null && getTimeFrom() != null;
	}

	/**
	 * Checks if recordToCheck is on same day as actual object
	 * 
	 * @param date
	 * @return
	 */
	public boolean isOnSameDay(Calendar date) {
		if (date == null) {
			return false;
		}

		Calendar t1 = getTimeFrom();
		Calendar t1Check = date;

		if (t1.get(Calendar.DAY_OF_YEAR) == t1Check.get(Calendar.DAY_OF_YEAR) && t1.get(Calendar.YEAR) == t1Check.get(Calendar.YEAR)) {
			return true;
		}

		return false;
	}

	public void setActivity(CActivity activity) {
		this.activity = activity;
	}

	public void setClient(CClient client) {
		this.client = client;
	}

	public void setCreatedBy(CUser createdBy) {
		this.createdBy = createdBy;
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

	public void setNote(String note) {
		this.note = note;
	}

	public void setOwner(CUser owner) {
		this.owner = owner;
	}

	public void setProject(CProject project) {
		this.project = project;
	}

	public void setTimeFrom(Calendar timeFrom) {
		this.timeFrom = timeFrom;
	}

	public void setTimeTo(Calendar timeTo) {
		this.timeTo = timeTo;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Boolean getOutside() {
		return outside;
	}

	public void setOutside(Boolean outside) {
		this.outside = outside;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public CRequestReason getReason() {
		return reason;
	}

	public void setReason(CRequestReason reason) {
		this.reason = reason;
	}

	public Boolean getLast() {
		return last;
	}

	public void setLast(Boolean last) {
		this.last = last;
	}

	public Boolean getLastWorking() {
		return lastWorking;
	}

	public void setLastWorking(Boolean lastWorking) {
		this.lastWorking = lastWorking;
	}

	public Boolean getLastNonWorking() {
		return lastNonWorking;
	}

	public void setLastNonWorking(Boolean lastNonWorking) {
		this.lastNonWorking = lastNonWorking;
	}

	public CTimeSheetRecordStatus getStatus() {
		return status;
	}

	public void setStatus(CTimeSheetRecordStatus status) {
		this.status = status;
	}

	public Boolean getHomeOffice() {
		return homeOffice;
	}

	public void setHomeOffice(Boolean homeOffice) {
		this.homeOffice = homeOffice;
	}

	public CTimeStampRecord convert() {
		final CTimeSheetRecord record = this;

		final CTimeStampRecord retVal = new CTimeStampRecord();
		retVal.setId(record.getId());
		retVal.setActivityId(record.getActivity().getId());
		retVal.setActivityName(record.getActivity().getName());
		retVal.setActivityInactive(!record.getActivity().getValid());
		retVal.setNote(record.getNote());
		retVal.setChangedByName(record.getChangedBy().getName());
		retVal.setChangedBySurname(record.getChangedBy().getSurname());
		retVal.setChangeTime(record.getChangeTime().getTime());
		retVal.setDateFrom(record.getTimeFrom().getTime());
		retVal.setOutsideWorkplace(record.getOutside());
		retVal.setHomeOffice(record.getHomeOffice());
		retVal.setPhase(record.getPhase());
		if (record.getTimeTo() != null) {
			retVal.setDateTo(record.getTimeTo().getTime());
		}
		retVal.setEmployeeId(record.getOwner().getId());
		retVal.setEmployeeName(record.getOwner().getName());
		retVal.setEmployeeSurname(record.getOwner().getSurname());

		if (record.getProject() != null) {
			retVal.setProjectId(record.getProject().getId());
			retVal.setProjectName(record.getProject().getName());
			retVal.setProjectInactive(!record.getProject().getValid());
		}
		if (record.getReason() != null) {
			retVal.setRequestReasonId(record.getReason().getId());
			retVal.setRequestReasonName(record.getReason().getReasonName());
		}

		retVal.setStatus(record.getStatus().getDescription());
		retVal.setStatusId(record.getStatus().getId());
		retVal.setWorking(record.getActivity().getWorking());
		return retVal;
	}

	@Override
	public void setJiraTimeSpentSeconds(Long jiraTimeSpentSeconds) {
		// nothing
	}
}
