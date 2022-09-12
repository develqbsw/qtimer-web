package sk.qbsw.sed.client.request;

public class CGetValidActivityGroupsRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long clientId;

	private Long activityId;

	private Boolean validFlag;

	public CGetValidActivityGroupsRequest() {
		super();
	}

	public CGetValidActivityGroupsRequest(Long clientId, Long activityId, Boolean validFlag) {
		super();
		this.clientId = clientId;
		this.activityId = activityId;
		this.validFlag = validFlag;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Long getActivityId() {
		return activityId;
	}

	public void setActivityId(Long activityId) {
		this.activityId = activityId;
	}

	public Boolean getValidFlag() {
		return validFlag;
	}

	public void setValidFlag(Boolean validFlag) {
		this.validFlag = validFlag;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
