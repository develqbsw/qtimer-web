package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CUploadResponseContent extends AResponseContent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String result;
	private String additionalInfo;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	@Override
	public void validate() throws CDataValidationException {
		if (result == null) {
			throw new CDataValidationException();
		}
	}
}
