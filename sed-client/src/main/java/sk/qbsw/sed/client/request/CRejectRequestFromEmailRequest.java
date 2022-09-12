package sk.qbsw.sed.client.request;

public class CRejectRequestFromEmailRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	String requestId;

	String requestCode;

	public CRejectRequestFromEmailRequest() {
		super();
	}

	public CRejectRequestFromEmailRequest(String requestId, String requestCode) {
		super();
		this.requestId = requestId;
		this.requestCode = requestCode;
	}

	public String getRequestId() {
		return requestId;
	}

	public String getRequestCode() {
		return requestCode;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public void setRequestCode(String requestCode) {
		this.requestCode = requestCode;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
