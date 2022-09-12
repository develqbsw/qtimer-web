package sk.qbsw.sed.client.model.timestamp;

import java.io.Serializable;
import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

@SuppressWarnings("serial")
public class CTimeStampAddRecord implements Serializable {

	private Long activityId;
	private String activityName;
	private Long employeeId;
	private Long nonWorkingActivityId;
	private String note;
	private Boolean outside;
	private Boolean homeOffice;
	private String phase;
	private Long projectId;
	private String projectName;
	private Date time;
	private Long reasonId;

	public Long getActivityId() {
		return this.activityId;
	}

	public Long getEmployeeId() {
		return this.employeeId;
	}

	public Long getNonWorkingActivityId() {
		return this.nonWorkingActivityId;
	}

	public String getNote() {
		return this.note;
	}

	public Boolean getOutside() {
		return this.outside;
	}

	public Boolean getHomeOffice() {
		return this.homeOffice;
	}

	public String getPhase() {
		return this.phase;
	}

	public Long getProjectId() {
		return this.projectId;
	}

	public Date getTime() {
		return this.time;
	}

	public void setActivityId(final Long activityId) {
		this.activityId = activityId;
	}

	public void setEmployeeId(final Long employeeId) {
		this.employeeId = employeeId;
	}

	public void setNonWorkingActivityId(final Long nonWorkingActivityId) {
		this.nonWorkingActivityId = nonWorkingActivityId;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setOutside(final Boolean outside) {
		this.outside = outside;
	}

	public void setHomeOffice(final Boolean homeOffice) {
		this.homeOffice = homeOffice;
	}

	public void setPhase(final String phase) {
		this.phase = phase;
	}

	public void setProjectId(final Long projectId) {
		this.projectId = projectId;
	}

	public void setTime(final Date time) {
		this.time = time;
	}

	public Long getReasonId() {
		return reasonId;
	}

	public void setReasonId(Long reasonId) {
		this.reasonId = reasonId;
	}

	public CTimeStampAddRecord getClone() {
		CTimeStampAddRecord retVal = new CTimeStampAddRecord();
		if (activityId != null) {
			retVal.setActivityId(new Long(activityId));
		}
		if (employeeId != null) {
			retVal.setEmployeeId(new Long(employeeId));
		}
		if (nonWorkingActivityId != null) {
			retVal.setNonWorkingActivityId(new Long(nonWorkingActivityId));
		}
		if (note != null) {
			retVal.setNote(new String(note));
		}
		if (outside != null) {
			retVal.setOutside(new Boolean(outside));
		}
		if (homeOffice != null) {
			retVal.setHomeOffice(new Boolean(homeOffice));
		}
		if (phase != null) {
			retVal.setPhase(new String(phase));
		}
		if (projectId != null) {
			retVal.setProjectId(new Long(projectId));
		}
		if (time != null) {
			retVal.setTime(new Date(time.getTime()));
		}
		if (reasonId != null) {
			retVal.setReasonId(new Long(reasonId));
		}
		return retVal;
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
		this.projectId = record.getId();
		this.projectName = record.getName();
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
}
