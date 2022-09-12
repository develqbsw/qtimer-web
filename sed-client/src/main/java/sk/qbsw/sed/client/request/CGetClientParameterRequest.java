package sk.qbsw.sed.client.request;

public class CGetClientParameterRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long clientId;

	private String name;

	public CGetClientParameterRequest() {
		super();
	}

	public CGetClientParameterRequest(Long clientId, String name) {
		super();
		this.clientId = clientId;
		this.name = name;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
