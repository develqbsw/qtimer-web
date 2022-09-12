package sk.qbsw.sed.api.rest.service;

import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.response.AResponseContent;
import sk.qbsw.sed.client.response.CEmptyResponseContent;
import sk.qbsw.sed.client.response.CResponse;

/**
 * The service handles the responses.
 *
 * @author Marek Martinkovic
 * @version 2.0.0
 * @since 2.0.0
 */
@Service(value = "responseService")
public class CResponseService {
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(CResponseService.class);

	@Autowired
	protected Validator validator;

	/**
	 * Create empty response.
	 *
	 * @param gson initialized google gson object
	 * @return empty json response
	 */
	public String createEmptyResponse(Gson gson) {
		CResponse<CEmptyResponseContent> contentResponse = new CResponse<>();
		contentResponse.setContent(new CEmptyResponseContent());

		return gson.toJson(contentResponse);
	}

	/**
	 * Create response with "content" attribute in json.
	 *
	 * @param <T>         the generic type
	 * @param contentData object with content
	 * @param gson        initialized google gson object
	 * @return response content in json
	 */
	public <T extends AResponseContent> String createResponse(T contentData, Gson gson) {
		CResponse<T> contentResponse = new CResponse<>();
		contentResponse.setContent(contentData);

		return gson.toJson(contentResponse);
	}

	/**
	 * Create response with "errorMessage" attribute - ex.getMessage() "error"
	 * attribute - name of error "errorCode" attribute - code of error
	 *
	 * in json and "errorCode" attribute in json.
	 *
	 * @param ex exception from with create
	 * @return errorCode in json object
	 */
	@SuppressWarnings({ "rawtypes" })
	public String createResponseError(CApiException ex) {
		Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

		CResponse response = new CResponse();
		response.setErrorMessage(ex.getMessage());
		response.setError(ex.getErrorCode() == null ? null : ex.getErrorCode().toString());
		response.setErrorCode(ex.getErrorCode() == null ? null : ex.getErrorCode().getCode().toString());

		return gson.toJson(response);
	}

	/**
	 * Validate the response.
	 *
	 * @param response the response
	 * @throws CApiException validation failed
	 */
	public <T extends AResponseContent> void validateResponse(CResponse<T> response) throws CDataValidationException {
		// first part checks mandatory fields
		response.validate();

		// second parts check constrains on fields
		Set<ConstraintViolation<CResponse<T>>> constraintViolations = validator.validate(response);

		if (!constraintViolations.isEmpty()) {
			throw new CDataValidationException();
		}
	}

	/**
	 * Writes response as json character.
	 *
	 * @param response the response
	 * @param json     the json
	 */
	public void writeToResponse(HttpServletResponse response, String json) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			response.getWriter().write(json);
		} catch (Exception ex) {
			LOGGER.error("Sending JSON response ", ex);
		}
	}
}
