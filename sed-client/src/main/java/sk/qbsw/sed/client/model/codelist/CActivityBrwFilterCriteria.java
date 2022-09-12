package sk.qbsw.sed.client.model.codelist;

public class CActivityBrwFilterCriteria implements IActivityBrwFilterCriteria {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long activityId;

	private String activityName;

	private Boolean working;

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

	public Boolean isWorking() {
		return working;
	}

	public void setWorking(Boolean working) {
		this.working = working;
	}
}
