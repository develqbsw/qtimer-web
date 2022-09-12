package sk.qbsw.sed.client.response;

import java.util.List;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;

public class CBrwTimeStampGenerateFetchResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private List<CTmpTimeSheet> result;

	public List<CTmpTimeSheet> getResult() {
		return result;
	}

	public void setResult(List<CTmpTimeSheet> result) {
		this.result = result;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
