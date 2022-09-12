package sk.qbsw.sed.client.request;

public class CGetClientRecordsForTheYearRequest extends ARequest {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Long clientId;

	private Integer selectedYearDate;

	public CGetClientRecordsForTheYearRequest() {
		super();
	}

	public CGetClientRecordsForTheYearRequest(Long clientId, Integer selectedYearDate) {
		super();
		this.clientId = clientId;
		this.selectedYearDate = selectedYearDate;
	}

	public Long getClientId() {
		return clientId;
	}

	public void setClientId(Long clientId) {
		this.clientId = clientId;
	}

	public Integer getSelectedYearDate() {
		return selectedYearDate;
	}

	public void setSelectedYearDate(Integer selectedYearDate) {
		this.selectedYearDate = selectedYearDate;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
