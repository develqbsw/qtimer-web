package sk.qbsw.sed.client.model.brw;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("serial")
public class CViewTimeStampRecord implements Serializable {

	private Long id;
	private Long activityId;
	private String activityName;
	private Long clientId;
	private boolean flagWorking;
	private boolean flagOutsideWorkplace;
	private String phase;
	private String note;
	private String changedByName;
	private String changedBySurname;
	private Long projectId;
	private String projectName;
	private Date timeFrom;
	private Date timeTo;
	private Long userId;
	private String userName;
	private String userSurname;
	private Long day;
	private Long month;
	private Long year;
	private Long reasonId;
	private String reasonName;
	private String status;
	private Long statusId;

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

	public Date getTimeFrom() {
		return this.timeFrom;
	}

	public Date getTimeTo() {
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

	public void setTimeFrom(final Date timeFrom) {
		this.timeFrom = timeFrom;
	}

	public void setTimeTo(final Date timeTo) {
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
}
