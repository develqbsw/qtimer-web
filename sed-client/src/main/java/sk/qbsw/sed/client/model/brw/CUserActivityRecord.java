package sk.qbsw.sed.client.model.brw;

import java.io.Serializable;

import sk.qbsw.sed.client.model.abstractclass.ADataForEditableColumn;

@SuppressWarnings("serial")
public class CUserActivityRecord extends ADataForEditableColumn implements Serializable {

	private Long activityId;
	private String activityName;
	private Boolean flagMyActivity;

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

	public Boolean getFlagMyActivity() {
		return flagMyActivity;
	}

	public void setFlagMyActivity(Boolean flagMyActivity) {
		this.flagMyActivity = flagMyActivity;
	}

	@Override
	public Boolean isActive() {
		return getFlagMyActivity();
	}
}
