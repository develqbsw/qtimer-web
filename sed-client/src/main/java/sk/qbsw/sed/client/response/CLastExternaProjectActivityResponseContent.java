package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.timestamp.CLastExternaProjectActivity;

public class CLastExternaProjectActivityResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CLastExternaProjectActivity lastExternaProjectActivity;

	public CLastExternaProjectActivity getLastExternaProjectActivity() {
		return lastExternaProjectActivity;
	}

	public void setLastExternaProjectActivity(CLastExternaProjectActivity lastExternaProjectActivity) {
		this.lastExternaProjectActivity = lastExternaProjectActivity;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
