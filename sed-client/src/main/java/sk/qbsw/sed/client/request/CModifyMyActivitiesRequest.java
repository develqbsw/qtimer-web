package sk.qbsw.sed.client.request;

public class CModifyMyActivitiesRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long activityId;

	private boolean flagMyActivity;

	private Long userId;

	public CModifyMyActivitiesRequest() {
		super();
	}

	public CModifyMyActivitiesRequest(Long activityId, boolean flagMyActivity, Long userId) {
		super();
		this.activityId = activityId;
		this.flagMyActivity = flagMyActivity;
		this.userId = userId;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public boolean isFlagMyActivity() {
		return flagMyActivity;
	}

	public void setFlagMyActivity(boolean flagMyActivity) {
		this.flagMyActivity = flagMyActivity;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
