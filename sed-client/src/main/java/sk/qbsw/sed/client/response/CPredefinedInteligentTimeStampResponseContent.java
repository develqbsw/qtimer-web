package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;

public class CPredefinedInteligentTimeStampResponseContent extends AResponseContent {

	private static final long serialVersionUID = 1L;

	private CPredefinedInteligentTimeStamp predefinedInteligentTimeStamp;

	public CPredefinedInteligentTimeStamp getPredefinedInteligentTimeStamp() {
		return predefinedInteligentTimeStamp;
	}

	public void setPredefinedInteligentTimeStamp(CPredefinedInteligentTimeStamp predefinedInteligentTimeStamp) {
		this.predefinedInteligentTimeStamp = predefinedInteligentTimeStamp;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
