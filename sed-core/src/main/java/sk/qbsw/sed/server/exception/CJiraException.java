package sk.qbsw.sed.server.exception;

/**
 * Exception used for JIRA connection problems
 * 
 * @author lobb
 *
 */
public class CJiraException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 
	 * @param paramThrowable
	 */
	public CJiraException(Throwable paramThrowable) {
		super(paramThrowable);
	}
}
