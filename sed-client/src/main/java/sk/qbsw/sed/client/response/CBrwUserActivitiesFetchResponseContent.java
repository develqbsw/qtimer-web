package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.brw.CUserActivityRecord;

public class CBrwUserActivitiesFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CUserActivityRecord> result;

	public List<CUserActivityRecord> getResult() {
		return result;
	}

	public void setResult(List<CUserActivityRecord> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
