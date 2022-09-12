package sk.qbsw.sed.client.request;

public class CRequestIdRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long requestId;

	public CRequestIdRequest() {
		super();
	}

	public CRequestIdRequest(Long requestId) {
		super();
		this.requestId = requestId;
	}

	public Long getRequestId() {
		return requestId;
	}

	public void setRequestId(Long requestId) {
		this.requestId = requestId;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
