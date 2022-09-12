package sk.qbsw.sed.client.response;

import sk.qbsw.sed.client.exception.CDataValidationException;

/**
 * empty response content
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.0.0
 */
public class CEmptyResponseContent extends AResponseContent {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	@Override
	public void validate() throws CDataValidationException {
		// do nothing
	}
}
