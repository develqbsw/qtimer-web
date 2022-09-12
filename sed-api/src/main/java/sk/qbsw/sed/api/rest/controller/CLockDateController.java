package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.lock.CLockDateParameters;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.ILockDateService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/lockDate")
public class CLockDateController extends AController {

	@Autowired
	private ILockDateService lockDateService;

	public CLockDateController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("generateMissigLockInfoRecords")
	public void generateMissigLockInfoRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long value = null;

		try {
			value = lockDateService.generateMissigLockInfoRecords(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("updateLockDateToSelectedUsers")
	public void updateLockDateToSelectedUsers(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLockDateParameters request = fromJsonToObject(json, CLockDateParameters.class);

		CResponse<CBooleanResponseContent> responseData = new CResponse<>();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean value = null;

		try {
			value = lockDateService.updateLockDateToSelectedUsers(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
