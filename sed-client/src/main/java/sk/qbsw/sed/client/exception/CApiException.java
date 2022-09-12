package sk.qbsw.sed.client.exception;

import sk.qbsw.sed.client.response.EErrorCode;

/**
 * @author martinkovic
 * @since 2.0.0
 * @version 2.0.0
 */
public class CApiException extends Exception {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	private EErrorCode errorCode;

	/**
	 * @param message
	 */
	public CApiException(String message) {
		super(message);
	}

	public CApiException(String message, Exception e) {
		super(message, e);
	}

	/**
	 * 
	 * @param error
	 */
	public CApiException(EErrorCode error) {
		super("");
		errorCode = error;
	}

	/**
	 * @param badParams
	 */
	public CApiException(EErrorCode error, Exception e) {
		super("", e);
		errorCode = error;
	}

	/**
	 * @param badParams
	 */
	public CApiException(String message, EErrorCode error) {
		super(message);
		errorCode = error;

	}

	/**
	 * @return the errorCode
	 */
	public EErrorCode getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(EErrorCode errorCode) {
		this.errorCode = errorCode;
	}
}
