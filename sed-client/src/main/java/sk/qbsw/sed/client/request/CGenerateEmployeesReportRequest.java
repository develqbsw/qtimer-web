package sk.qbsw.sed.client.request;

public class CGenerateEmployeesReportRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long clientId;

	private Boolean onlyValid;

	public CGenerateEmployeesReportRequest() {
		super();
	}

	public CGenerateEmployeesReportRequest(Long clientId, Boolean onlyValid) {
		super();
		this.clientId = clientId;
		this.onlyValid = onlyValid;
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

	@Override
	public Boolean validate() {
		return true;
	}
}
