package sk.qbsw.sed.client.model.brw;

import java.io.Serializable;
import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.common.utils.CDateUtils;

public class CTmpTimeSheet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long id;
	private Long clientId;
	private Long ownerId;
	private Long createdById;
	private Long changedById;
	private Date changeTime;
	private Date dateFrom;
	private Date dateTo;
	private Date dateGenerateAction;
	private Long projectId;
	private String projectName;
	private Boolean projectInactive;
	private Long activityId;
	private String activityName;
	private Boolean activityInactive;
	private String phase;
	private String note;
	private Boolean valid;
	private Boolean outside;
	private Boolean homeOffice;
	private Boolean generated;
	private Long durationInMinutes;
	private Long durationInPercent;
	private Long summaryDurationInMinutes;
	private Long jiraTimeSpentSeconds;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getCreatedById() {
		return createdById;
	}

	public void setCreatedById(Long createdById) {
		this.createdById = createdById;
	}

	public Long getChangedById() {
		return changedById;
	}

	public void setChangedById(Long changedById) {
		this.changedById = changedById;
	}

	public Long getProjectId() {
		return projectId;
	}

	public void setProjectId(Long projectId) {
		this.projectId = projectId;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getPhase() {
		return phase;
	}

	public void setPhase(String phase) {
		this.phase = phase;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Boolean getValid() {
		return valid;
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

	public Long getDurationInMinutes() {
		return durationInMinutes;
	}

	public void setDurationInMinutes(Long durationInMinutes) {
		this.durationInMinutes = durationInMinutes;
	}

	public String getDurationInMinutesString() {
		return CDateUtils.getMinutesAsString(durationInMinutes);
	}

	public void setDurationInMinutesString(String durationInMinutesString) {
		this.durationInMinutes = CDateUtils.getStringAsMinutes(durationInMinutesString);
	}

	public Long getSummaryDurationInMinutes() {
		return summaryDurationInMinutes;
	}

	public void setSummaryDurationInMinutes(Long summaryDurationInMinutes) {
		this.summaryDurationInMinutes = summaryDurationInMinutes;
	}

	public Boolean getGenerated() {
		return generated;
	}

	public void setGenerated(Boolean generated) {
		this.generated = generated;
	}

	public Date getChangeTime() {
		return changeTime;
	}

	public void setChangeTime(Date changeTime) {
		this.changeTime = changeTime;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date dateFrom) {
		this.dateFrom = dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public Date getDateGenerateAction() {
		return dateGenerateAction;
	}

	public void setDateGenerateAction(Date dateGenerateAction) {
		this.dateGenerateAction = dateGenerateAction;
	}

	public Long getJiraTimeSpentSeconds() {
		return jiraTimeSpentSeconds;
	}

	public void setJiraTimeSpentSeconds(Long jiraTimeSpentSeconds) {
		this.jiraTimeSpentSeconds = jiraTimeSpentSeconds;
	}

	public Long getDurationInPercent() {
		return durationInPercent;
	}

	public void setDurationInPercent(Long durationInPercent) {
		this.durationInPercent = durationInPercent;
	}

	public Boolean getProjectInactive() {
		return projectInactive;
	}

	public void setProjectInactive(Boolean projectInactive) {
		this.projectInactive = projectInactive;
	}

	public Boolean getActivityInactive() {
		return activityInactive;
	}

	public void setActivityInactive(Boolean activityInactive) {
		this.activityInactive = activityInactive;
	}

	public CCodeListRecord getActivity() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(activityId);
		record.setName(activityName);
		return record;
	}

	public void setActivity(CCodeListRecord record) {
		this.activityId = record.getId();
		this.activityName = record.getName();

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

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Boolean getHomeOffice() {
		return homeOffice;
	}

	public void setHomeOffice(Boolean homeOffice) {
		this.homeOffice = homeOffice;
	}
}
