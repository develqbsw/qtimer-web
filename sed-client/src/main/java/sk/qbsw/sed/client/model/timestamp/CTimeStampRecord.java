package sk.qbsw.sed.client.model.timestamp;

import java.io.Serializable;
import java.util.Date;

import sk.qbsw.sed.client.model.IActivityConstant;
import sk.qbsw.sed.client.model.IListBoxValueTypes;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

/**
 * Timestamp record ( C<->S communication)
 * 
 * @author Dalibor Rak
 * @version 0.1
 * @since 0.1
 */
@SuppressWarnings("serial")
public class CTimeStampRecord implements Serializable {

	public CTimeStampRecord() {
		super();
	}

	public CTimeStampRecord(CTimeStampRecord timeStampRecord) {
		super();
		this.activityId = timeStampRecord.getActivityId();
		this.activityName = timeStampRecord.getActivityName();
		this.activityInactive = timeStampRecord.getActivityInactive();
		this.dateFrom = (Date) timeStampRecord.getDateFrom().clone();
		this.dateTo = (Date) timeStampRecord.getDateTo().clone();
		this.employeeId = timeStampRecord.getEmployeeId();
		this.employeeName = timeStampRecord.getEmployeeName();
		this.employeeSurname = timeStampRecord.getEmployeeSurname();
		this.changedByName = timeStampRecord.getChangedByName();
		this.changedBySurname = timeStampRecord.getChangedBySurname();
		this.changeTime = timeStampRecord.getChangeTime();
		this.id = timeStampRecord.getId();
		this.note = timeStampRecord.getNote();
		this.outsideWorkplace = timeStampRecord.getOutsideWorkplace();
		this.homeOffice = timeStampRecord.getHomeOffice();
		this.phase = timeStampRecord.getPhase();
		this.projectId = timeStampRecord.getProjectId();
		this.projectName = timeStampRecord.getProjectName();
		this.projectInactive = timeStampRecord.getProjectInactive();
		this.requestReasonId = timeStampRecord.getRequestReasonId();
		this.requestReasonName = timeStampRecord.getRequestReasonName();
		this.status = timeStampRecord.getStatus();
		this.statusId = timeStampRecord.getStatusId();
		this.working = timeStampRecord.getWorking();
		this.duration = timeStampRecord.getDuration();
	}

	/**
	 * Date of selected activity
	 */
	private Long activityId;
	private String activityName;
	private Boolean activityInactive;

	/**
	 * Date from
	 */
	private Date dateFrom;

	/**
	 * Date to
	 */
	private Date dateTo;

	/**
	 * Employee ID
	 */
	private Long employeeId;
	private String employeeName;
	private String employeeSurname;

	/**
	 * Changed user name
	 */
	private String changedByName;

	/**
	 * Changed user surname
	 */
	private String changedBySurname;

	/**
	 * Change time
	 */
	private Date changeTime;

	/**
	 * Id of timesheet
	 */
	private Long id;

	/**
	 * Note of the timestamp record
	 */
	private String note;

	/**
	 * Identifies that work was done outside standard workspace
	 */
	private Boolean outsideWorkplace;
	private Boolean homeOffice;

	/**
	 * Phase of project
	 */
	private String phase;

	/**
	 * ID of selected project
	 */
	private Long projectId;
	private String projectName;
	private Boolean projectInactive;

	/**
	 * ID of activity reason (for selected activities only)
	 */
	private Long requestReasonId;
	private String requestReasonName;
	private String status;
	private Long statusId;
	private Boolean working;
	private Integer duration;

	public Long getActivityId() {
		return activityId;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public Date getTimeFrom() {
		return dateFrom;
	}

	public Date getTimeTo() {
		return dateTo;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public String getChangedByName() {
		return changedByName;
	}

	public String getChangedBySurname() {
		return changedBySurname;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public Long getId() {
		return id;
	}

	public String getNote() {
		return note;
	}

	public Boolean getOutsideWorkplace() {
		return outsideWorkplace;
	}

	public String getPhase() {
		return phase;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Integer getDuration() {
		return duration;
	}

	public void setDuration(Integer duration) {
		this.duration = duration;
	}

	@SuppressWarnings("deprecation")
	public void setDateFrom(Date dateFrom) {
		if (dateFrom != null) {
			dateFrom.setSeconds(0);
		}
		this.dateFrom = dateFrom;
	}

	@SuppressWarnings("deprecation")
	public void setDateTo(Date dateTo) {
		if (dateTo != null) {
			dateTo.setSeconds(0);
		}
		this.dateTo = dateTo;
	}

	@SuppressWarnings("deprecation")
	public void setTimeFrom(Date timeFrom) {
		if (timeFrom != null) {
			this.dateFrom.setSeconds(0);
			this.dateFrom.setHours(timeFrom.getHours());
			this.dateFrom.setMinutes(timeFrom.getMinutes());
		} else {
			this.dateFrom = null;
		}
	}

	@SuppressWarnings("deprecation")
	public void setTimeTo(Date timeTo) {
		if (this.dateTo == null) {
			this.dateTo = new Date();
			this.dateTo.setTime(this.dateFrom.getTime());
		}

		if (timeTo != null) {
			this.dateTo.setSeconds(0);
			this.dateTo.setHours(timeTo.getHours());
			this.dateTo.setMinutes(timeTo.getMinutes());
		} else {
			this.dateTo = null;
		}
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public void setChangedByName(String changedByName) {
		this.changedByName = changedByName;
	}

	public void setChangedBySurname(String changedBySurname) {
		this.changedBySurname = changedBySurname;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setOutsideWorkplace(Boolean outsideWorkplace) {
		this.outsideWorkplace = outsideWorkplace;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getRequestReasonId() {
		return requestReasonId;
	}

	public void setRequestReasonId(Long requestReasonId) {
		this.requestReasonId = requestReasonId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getWorking() {
		return working == null ? false : working;
	}

	public Boolean getShowProject() {
		return getWorking() || IActivityConstant.NOT_WORK_ALERTNESSWORK.equals(activityId) || IActivityConstant.NOT_WORK_INTERACTIVEWORK.equals(activityId);
	}

	public void setWorking(Boolean working) {
		this.working = working;
	}

	public Date getDate() {
		return dateFrom;
	}

	@SuppressWarnings("deprecation")
	public void setDate(Date date) {
		dateFrom.setDate(date.getDate());
		dateFrom.setMonth(date.getMonth());
		dateFrom.setYear(date.getYear());

		if (null != dateTo) {
			dateTo.setDate(date.getDate());
			dateTo.setMonth(date.getMonth());
			dateTo.setYear(date.getYear());
		}
	}

	public CCodeListRecord getActivity() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(activityId);
		record.setName(activityName);
		if (working != null) {
			record.setType(working ? IListBoxValueTypes.WORKING : IListBoxValueTypes.NON_WORKING);
		}
		return record;
	}

	public void setActivity(CCodeListRecord record) {
		this.activityId = record.getId();
		this.activityName = record.getName();
		this.working = IListBoxValueTypes.WORKING.equals(record.getType());
		if (!working) {
			setProject(new CCodeListRecord());
		}
	}

	public CCodeListRecord getProject() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(projectId);
		record.setName(projectName);
		return record;
	}

	public void setProject(CCodeListRecord record) {
		if (record != null) {
			this.projectId = record.getId();
			this.projectName = record.getName();
		}
	}

	public CCodeListRecord getRequestReason() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(requestReasonId);
		record.setName(requestReasonName);
		return record;
	}

	public void setRequestReason(CCodeListRecord record) {
		this.requestReasonId = record.getId();
		this.requestReasonName = record.getName();
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public String getEmployeeSurname() {
		return employeeSurname;
	}

	public void setEmployeeSurname(String employeeSurname) {
		this.employeeSurname = employeeSurname;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getRequestReasonName() {
		return requestReasonName;
	}

	public void setRequestReasonName(String requestReasonName) {
		this.requestReasonName = requestReasonName;
	}

	public CCodeListRecord getEmployee() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(employeeId);
		record.setName(employeeSurname + " " + employeeName);
		return record;
	}

	public void setEmployee(CCodeListRecord record) {
		this.employeeId = record.getId();
	}

	public String getChangedBy() {
		return changedByName + " " + changedBySurname;
	}

	public Boolean getActivityInactive() {
		return activityInactive;
	}

	public void setActivityInactive(Boolean activityInactive) {
		this.activityInactive = activityInactive;
	}

	public Boolean getProjectInactive() {
		return projectInactive;
	}

	public void setProjectInactive(Boolean projectInactive) {
		this.projectInactive = projectInactive;
	}

	public Long getStatusId() {
		return statusId;
	}

	public void setStatusId(Long statusId) {
		this.statusId = statusId;
	}

	public Boolean getHomeOffice() {
		return homeOffice;
	}

	public void setHomeOffice(Boolean homeOffice) {
		this.homeOffice = homeOffice;
	}
}
