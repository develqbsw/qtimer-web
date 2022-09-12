package sk.qbsw.sed.client.response;

import javax.validation.constraints.NotNull;

import com.google.gson.annotations.Expose;

import sk.qbsw.sed.client.exception.CDataValidationException;

/**
 * root of response
 *
 * @param <T> generic type
 *
 * @author Podmajersky Lukas
 * @since 2.0.0
 * @version 2.5.0
 */
public class CResponse<T extends AResponseContent> extends CResponseBase {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** The content. */
	@Expose
	@NotNull
	private T content;

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public T getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(T content) {
		this.content = content;
	}

	/**
	 * Validate the mandatory request data.
	 *
	 * @throws CDataValidationException
	 */
	public void validate() throws CDataValidationException {
		content.validate();
	}
}
