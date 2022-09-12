package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.request.CRequestRecord;

public class CRequestRecordResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CRequestRecord requset;

	public CRequestRecord getRequset() {
		return requset;
	}

	public void setRequset(CRequestRecord requset) {
		this.requset = requset;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
