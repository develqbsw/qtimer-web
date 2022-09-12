package sk.qbsw.sed.client.response;

import java.io.Serializable;

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
public class CReportDataResponse implements Serializable {
	/** serial uid */
	private static final long serialVersionUID = 1L;

	/** The content. */
	@Expose
	@NotNull
	private CReportDataResponseContent content;

	/** The error code in case of failure. */
	@Expose
	private String errorCode;

	/** The error code in case of failure. */
	@Expose
	private String error;

	/** The error message in case of failure. */
	@Expose
	private String errorMessage;

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public CReportDataResponseContent getContent() {
		return content;
	}

	/**
	 * Sets the content.
	 *
	 * @param content the new content
	 */
	public void setContent(CReportDataResponseContent content) {
		this.content = content;
	}

	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public String getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code.
	 *
	 * @param errorCode the new error code
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Validate the mandatory request data.
	 *
	 * @throws CDataValidationException
	 */
	public void validate() throws CDataValidationException {
		content.validate();
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
}
