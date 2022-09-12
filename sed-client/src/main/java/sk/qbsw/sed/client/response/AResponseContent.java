package sk.qbsw.sed.client.response;

import java.io.Serializable;

import sk.qbsw.sed.client.exception.CDataValidationException;

/**
 * The parent class for content of response - content means variable "content"
 * in CResponse class.
 *
 * @author Podmajersky Lukas
 * @version 2.0.0
 * @since 2.5.0
 */
public abstract class AResponseContent implements Serializable {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/**
	 * Validate the mandatory request content data.
	 */
	public abstract void validate() throws CDataValidationException;
}
