package sk.qbsw.sed.client.request;

/**
 * upoload request
 *
 * @author Lobb
 */
public class CUploadRequest extends ARequest {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private String[] fileRows;

	public CUploadRequest() {
		super();
	}

	public String[] getFileRows() {
		return fileRows;
	}

	public void setFileRows(String[] fileRows) {
		this.fileRows = fileRows;
	}

	@Override
	public Boolean validate() {
		return true;
	}
}
