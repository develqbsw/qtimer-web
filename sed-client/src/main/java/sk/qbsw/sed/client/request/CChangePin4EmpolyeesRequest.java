package sk.qbsw.sed.client.request;

public class CChangePin4EmpolyeesRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private Long userId;

	private String newPin;

	public CChangePin4EmpolyeesRequest() {
		super();
	}

	public CChangePin4EmpolyeesRequest(Long userId, String newPin) {
		super();
		this.userId = userId;
		this.newPin = newPin;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getNewPin() {
		return newPin;
	}

	public void setNewPin(String newPin) {
		this.newPin = newPin;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
