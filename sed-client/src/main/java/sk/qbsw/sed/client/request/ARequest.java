package sk.qbsw.sed.client.request;

import java.io.Serializable;

/**
 * @author martinkovic
 * @since 2.0.0
 * @version 2.0.0
 */
public abstract class ARequest implements Serializable {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/**
	 * Validate yourself.
	 *
	 * @return the boolean
	 */
	public abstract Boolean validate();
}
