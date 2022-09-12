package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;

public class CRequestReasonListsDataResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CRequestReasonListsData requestReasonListsData;

	public CRequestReasonListsData getRequestReasonListsData() {
		return requestReasonListsData;
	}

	public void setRequestReasonListsData(CRequestReasonListsData requestReasonListsData) {
		this.requestReasonListsData = requestReasonListsData;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
