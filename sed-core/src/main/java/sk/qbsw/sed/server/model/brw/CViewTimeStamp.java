package sk.qbsw.sed.server.model.brw;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;

/**
 * Model for view of timestamps
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 * 
 */
@SuppressWarnings("serial")
@Entity
@Table(schema = "public", name = "v_timestamps")
public class CViewTimeStamp implements Serializable {

	@Id
	@Column(name = "pk_id", nullable = false)
	private Long id;

	@Column(name = "fk_activity", nullable = true)
	private Long activityId;

	@Column(name = "c_activity_name", nullable = true)
	private String activityName;

	@Column(name = "fk_client", nullable = true)
	private Long clientId;

	@Column(name = "c_flag_working", nullable = true)
	private boolean flagWorking;

	@Column(name = "c_flag_outside", nullable = false)
	private boolean flagOutsideWorkplace;

	@Column(name = "c_flag_home_office", nullable = true)
	private Boolean flagHomeOffice;

	@Column(name = "c_phase", nullable = true)
	private String phase;

	@Column(name = "c_note", nullable = true)
	private String note;

	@Column(name = "c_changedby_name", nullable = true)
	private String changedByName;

	@Column(name = "c_changedby_surname", nullable = true)
	private String changedBySurname;

	@Column(name = "fk_project", nullable = true)
	private Long projectId;

	@Column(name = "c_project_name", nullable = true)
	private String projectName;

	@Column(name = "c_time_from", nullable = true)
	private Calendar timeFrom;

	@Column(name = "c_time_to", nullable = true)
	private Calendar timeTo;

	@Column(name = "fk_user", nullable = true)
	private Long userId;

	@Column(name = "c_user_name", nullable = true)
	private String userName;

	@Column(name = "c_user_surname", nullable = true)
	private String userSurname;

	@Column(name = "c_date_day")
	private Long day;

	@Column(name = "c_date_month")
	private Long month;

	@Column(name = "c_date_year")
	private Long year;

	@Column(name = "fk_reason", nullable = true)
	private Long reasonId;

	@Column(name = "c_reason_name", nullable = true)
	private String reasonName;

	@Column(name = "c_status_description", nullable = false)
	private String status;

	@Column(name = "c_status", nullable = false)
	private Long statusId;

	@Column(name = "c_duration_minutes", nullable = true)
	private Integer duration;

	@Column(name = "c_flag_sum", nullable = true)
	private Boolean flagSum;

	@Column(name = "c_flag_export", nullable = true)
	private Boolean flagExport;

	public Long getActivityId() {
		return this.activityId;
	}

	public String getActivityName() {
		return this.activityName;
	}

	public Long getClientId() {
		return this.clientId;
	}

	public String getChangedByName() {
		return this.changedByName;
	}

	public String getChangedBySurname() {
		return this.changedBySurname;
	}

	public Long getId() {
		return this.id;
	}

	public Long getProjectId() {
		return this.projectId;
	}

	public String getProjectName() {
		return this.projectName;
	}

	public Calendar getTimeFrom() {
		return this.timeFrom;
	}

	public Calendar getTimeTo() {
		return this.timeTo;
	}

	public Long getUserId() {
		return this.userId;
	}

	public String getUserName() {
		return this.userName;
	}

	public String getUserSurname() {
		return this.userSurname;
	}

	public boolean isFlagWorking() {
		return this.flagWorking;
	}

	public void setActivityId(final Long activityId) {
		this.activityId = activityId;
	}

	public void setActivityName(final String activityName) {
		this.activityName = activityName;
	}

	public void setClientId(final Long clientId) {
		this.clientId = clientId;
	}

	public void setFlagWorking(final boolean flagWorking) {
		this.flagWorking = flagWorking;
	}

	public void setChangedByName(final String changedByName) {
		this.changedByName = changedByName;
	}

	public void setChangedBySurname(final String changedBySurname) {
		this.changedBySurname = changedBySurname;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setProjectId(final Long projectId) {
		this.projectId = projectId;
	}

	public void setProjectName(final String projectName) {
		this.projectName = projectName;
	}

	public void setTimeFrom(final Calendar timeFrom) {
		this.timeFrom = timeFrom;
	}

	public void setTimeTo(final Calendar timeTo) {
		this.timeTo = timeTo;
	}

	public void setUserId(final Long userId) {
		this.userId = userId;
	}

	public void setUserName(final String userName) {
		this.userName = userName;
	}

	public void setUserSurname(final String userSurname) {
		this.userSurname = userSurname;
	}

	public boolean isFlagOutsideWorkplace() {
		return this.flagOutsideWorkplace;
	}

	public void setFlagOutsideWorkplace(final boolean flagOutsideWorkplace) {
		this.flagOutsideWorkplace = flagOutsideWorkplace;
	}

	public String getPhase() {
		return this.phase;
	}

	public void setPhase(final String phase) {
		this.phase = phase;
	}

	public String getNote() {
		return this.note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public Long getDay() {
		return this.day;
	}

	public void setDay(final Long day) {
		this.day = day;
	}

	public Long getMonth() {
		return this.month;
	}

	public void setMonth(final Long month) {
		this.month = month;
	}

	public Long getYear() {
		return this.year;
	}

	public void setYear(final Long year) {
		this.year = year;
	}

	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	public String getReasonName() {
		return reasonName;
	}

	public void setReasonName(String reasonName) {
		this.reasonName = reasonName;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	public Boolean getFlagSum() {
		return flagSum;
	}

	public void setFlagSum(Boolean flagSum) {
		this.flagSum = flagSum;
	}

	public Boolean getFlagExport() {
		return flagExport;
	}

	public void setFlagExport(Boolean flagExport) {
		this.flagExport = flagExport;
	}

	public Boolean isFlagHomeOffice() {
		return flagHomeOffice;
	}

	public void setFlagHomeOffice(Boolean flagHomeOffice) {
		this.flagHomeOffice = flagHomeOffice;
	}

	public CTimeStampRecord convertToTimeStampRecord() {
		CTimeStampRecord retVal = new CTimeStampRecord();
		retVal.setActivityId(activityId);
		retVal.setActivityName(activityName);
		retVal.setChangedByName(changedByName);
		retVal.setChangedBySurname(changedBySurname);
		retVal.setOutsideWorkplace(flagOutsideWorkplace);
		retVal.setHomeOffice(flagHomeOffice);
		retVal.setWorking(flagWorking);
		retVal.setId(id);
		retVal.setNote(note);
		retVal.setPhase(phase);
		retVal.setProjectId(projectId);
		retVal.setProjectName(projectName);
		retVal.setRequestReasonId(reasonId);
		retVal.setRequestReasonName(reasonName);
		retVal.setStatus(status);
		retVal.setDateFrom(timeFrom.getTime());
		retVal.setDateTo(timeTo == null ? null : timeTo.getTime());
		retVal.setEmployeeId(userId);
		retVal.setEmployeeName(userName);
		retVal.setEmployeeSurname(userSurname);
		retVal.setDuration(duration);
		return retVal;
	}
}
