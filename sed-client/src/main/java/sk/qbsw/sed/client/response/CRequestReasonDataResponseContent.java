package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;

public class CRequestReasonDataResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CRequestReasonData requestReasonData;

	public CRequestReasonData getRequestReasonData() {
		return requestReasonData;
	}

	public void setRequestReasonData(CRequestReasonData requestReasonData) {
		this.requestReasonData = requestReasonData;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
