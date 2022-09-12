package sk.qbsw.sed.client.exception;

public class CSystemFailureException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5202808730426837074L;

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * 
	 * @param message the detail message
	 * @param cause   the cause
	 */
	public CSystemFailureException(final String message, final Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new exception with the specified cause.
	 * 
	 * @param cause the cause
	 */
	public CSystemFailureException(final Throwable cause) {
		super(cause);
	}

	public CSystemFailureException(final String message) {
		super(message);
	}
}
