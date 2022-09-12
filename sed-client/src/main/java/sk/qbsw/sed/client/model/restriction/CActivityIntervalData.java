package sk.qbsw.sed.client.model.restriction;

import java.io.Serializable;
import java.util.Date;

import sk.qbsw.sed.client.model.codelist.CCodeListRecord;

/**
 * Activity interval record ( C<->S communication)
 * 
 * @author rosenberg
 * @version 1.6.6.1
 * @since 1.6.6.1
 */
@SuppressWarnings("serial")
public class CActivityIntervalData implements Serializable {
	Long id;

	String name;

	Long activityId;

	Long dayTypeId;

	Date timeFrom;

	Date timeTo;

	Long groupId;

	Date dateValidFrom;

	Date dateValidTo;

	Boolean valid;

	String activityName;

	String groupName;

	public CCodeListRecord getActivityType() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(activityId);
		return record;
	}

	public void setActivityType(CCodeListRecord record) {
		this.activityId = record.getId();
	}

	public CCodeListRecord getDayType() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(dayTypeId);
		return record;
	}

	public void setDayType(CCodeListRecord record) {
		this.dayTypeId = record.getId();
	}

	public CCodeListRecord getGroup() {
		CCodeListRecord record = new CCodeListRecord();
		record.setId(groupId);
		return record;
	}

	public void setGroup(CCodeListRecord record) {
		this.groupId = record.getId();
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

	public Long getDayTypeId() {
		return dayTypeId;
	}

	public void setDayTypeId(Long dayTypeId) {
		this.dayTypeId = dayTypeId;
	}

	public Date getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(Date timeFrom) {
		this.timeFrom = timeFrom;
	}

	public Date getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(Date timeTo) {
		this.timeTo = timeTo;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public Date getDateValidFrom() {
		return dateValidFrom;
	}

	public void setDateValidFrom(Date dateValidFrom) {
		this.dateValidFrom = dateValidFrom;
	}

	public Date getDateValidTo() {
		return dateValidTo;
	}

	public void setDateValidTo(Date dateValidTo) {
		this.dateValidTo = dateValidTo;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
