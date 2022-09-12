package sk.qbsw.sed.client.model.restriction;

import java.io.Serializable;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

/**
 * Activity group record ( C<->S communication)
 * 
 * @author rosenberg
 * @version 1.6.6.1
 * @since 1.6.6.1
 */
@SuppressWarnings("serial")
public class CGroupsAIData implements Serializable {
	Long id;

	String name;

	Boolean valid;

	Long activityId;

	String activityName;

	String projectGroup;

	public CCodeListRecord getActivityType() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(activityId);
		return record;
	}

	public void setActivityType(CCodeListRecord record) {
		this.activityId = record.getId();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getProjectGroup() {
		return projectGroup;
	}

	public void setProjectGroup(String projectGroup) {
		this.projectGroup = projectGroup;
	}
}
