package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;

public class CGenerateReportResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private byte[] byteArray;

	private String fileName;

	public byte[] getByteArray() {
		return byteArray;
	}

	public void setByteArray(byte[] byteArray) {
		this.byteArray = byteArray;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
