package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.request.CSendWarningToPresentedEmployeesRequest;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.ISendEmailService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

/**
 * 
 * @author lobb
 *
 */
@Controller
@RequestMapping(value = "/sendEmail")
public class CSendEmailController extends AController {

	@Autowired
	private ISendEmailService sendEmailService;

	public CSendEmailController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/sendMissingEmployeesEmail")
	public void sendMissingEmployeesEmail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		String request = fromJsonToObject(json, String.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long value = sendEmailService.sendMissingEmployeesEmail(request);

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/sendMissingEmployeesEmails")
	public void sendMissingEmployeesEmails(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CUserSystemEmailContainer request = fromJsonToObject(json, CUserSystemEmailContainer.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long value;
		try {
			value = sendEmailService.sendMissingEmployeesEmail(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	/**
	 * volane z recepcneho tabletu
	 * 
	 * @param json
	 * @param response
	 * @throws CApiException
	 */
	@RequestMapping("/sendWarningToPresentedEmployees")
	public void sendWarningToPresentedEmployees(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CSendWarningToPresentedEmployeesRequest request = fromJsonToObject(json, CSendWarningToPresentedEmployeesRequest.class);

		try {
			sendEmailService.sendWarningToPresentedEmployees(request.getUser().getUserId(), request.getZoneId(), request.getEmployees());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
