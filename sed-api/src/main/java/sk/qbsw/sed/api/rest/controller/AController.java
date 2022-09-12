package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import sk.qbsw.sed.api.rest.service.CResponseService;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.table.IFilterCriteria;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.EErrorCode;
import sk.qbsw.sed.client.service.brw.IBrwService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

/**
 * Parent class for controllers.
 *
 * @author Marek Martinkovic
 * @version 2.0.0
 * @since 2.0.0
 */
public abstract class AController {
	
	/** Response service creates responses. */
	@Autowired
	protected CResponseService responseService;

	/** JSON parser. */
	protected Gson gson;

	public void count(IFilterCriteria criteria, IBrwService brwService, HttpServletResponse response) throws CApiException {
		CResponse<CBrwCountResponseContent> responseData = new CResponse<>();
		CBrwCountResponseContent content = new CBrwCountResponseContent();

		Long result = null;

		try {
			result = brwService.count(criteria);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCount(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	/**
	 * Convert json to object.
	 *
	 * @param json the json string
	 * @param objectClass class to convert string into
	 * @return converted class
	 * @throws CApiException the string has wrong syntax
	 */
	protected <T> T fromJsonToObject(String json, Class<T> objectClass) throws CApiException {
		try {
			return gson.fromJson(json, objectClass);
		} catch (JsonSyntaxException e) {
			throw new CApiException(EErrorCode.BAD_PARAMS, e);
		} catch (JsonParseException e) {
			throw new CApiException(EErrorCode.BAD_PARAMS, e);
		}
	}
}
