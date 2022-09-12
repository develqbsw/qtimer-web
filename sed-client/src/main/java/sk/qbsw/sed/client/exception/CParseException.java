package sk.qbsw.sed.client.exception;

public class CParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CParseException(String message) {
		super(message);
	}

	public CParseException(String message, Throwable cause) {
		super(message, cause);
	}
}
