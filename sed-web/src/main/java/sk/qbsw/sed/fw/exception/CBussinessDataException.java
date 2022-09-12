package sk.qbsw.sed.fw.exception;

public class CBussinessDataException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private CBussinessDataExceptionModel model;

	public CBussinessDataException(CBussinessDataExceptionModel model) {
		super();
		this.model = model;
	}

	public CBussinessDataExceptionModel getModel() {
		return model;
	}

	public void setModel(CBussinessDataExceptionModel model) {
		this.model = model;
	}
}
