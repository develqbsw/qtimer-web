package sk.qbsw.sed.client.request;

public class CLoadTreeByClientUserRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long clientId;

	private Long userId;
	private Boolean onlyValid;

	private Boolean withoutMe;

	public CLoadTreeByClientUserRequest() {
		super();
	}

	public CLoadTreeByClientUserRequest(Long clientId, Long userId, Boolean onlyValid, Boolean withoutMe) {
		super();
		this.clientId = clientId;
		this.userId = userId;
		this.onlyValid = onlyValid;
		this.withoutMe = withoutMe;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Boolean getOnlyValid() {
		return onlyValid;
	}

	public void setOnlyValid(Boolean onlyValid) {
		this.onlyValid = onlyValid;
	}

	public Boolean getWithoutMe() {
		return withoutMe;
	}

	public void setWithoutMe(Boolean withoutMe) {
		this.withoutMe = withoutMe;
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
