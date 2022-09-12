package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.timestamp.CPredefinedTimeStamp;

public class CPredefinedTimeStampResponseContent extends AResponseContent {

	private static final long serialVersionUID = 1L;

	private CPredefinedTimeStamp predefinedTimeStamp;

	public CPredefinedTimeStamp getPredefinedTimeStamp() {
		return predefinedTimeStamp;
	}

	public void setPredefinedTimeStamp(CPredefinedTimeStamp predefinedTimeStamp) {
		this.predefinedTimeStamp = predefinedTimeStamp;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
