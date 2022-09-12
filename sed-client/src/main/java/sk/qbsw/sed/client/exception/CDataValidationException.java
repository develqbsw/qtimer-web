package sk.qbsw.sed.client.exception;

/**
 * The validation of the data failed.
 * 
 * @author Martinkovic
 * @version 2.0.0
 * @since 2.0.0
 */
public class CDataValidationException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1229621231534123L;

	/**
	 * Instantiates a new data validation exception.
	 */
	public CDataValidationException() {
		super();
	}

	/**
	 * Instantiates a new data validation exception.
	 * 
	 * @param message the message
	 */
	public CDataValidationException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new data validation exception.
	 * 
	 * @param cause the throwable
	 */
	public CDataValidationException(Throwable cause) {
		super(cause);
	}

	/**
	 * Instantiates a new data validation exception.
	 * 
	 * @param message the message
	 * @param cause   the throwable
	 */
	public CDataValidationException(String message, Throwable cause) {
		super(message, cause);
	}
}
