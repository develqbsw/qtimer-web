package sk.qbsw.sed.framework.security.exception;

/**
 * Security exception
 *
 * @author Dalibor Rak
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings("serial")
public class CSecurityException extends CBusinessException {

	/**
	 * Instantiates a new c security exception.
	 *
	 * @param message the message
	 */
	public CSecurityException(String message) {
		super(message);
	}
}
