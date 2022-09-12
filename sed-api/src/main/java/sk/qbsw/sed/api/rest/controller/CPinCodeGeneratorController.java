package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.request.CGetGeneratedPINRequest;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.client.service.business.IPinCodeGeneratorService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/pinCodeGenerator")
public class CPinCodeGeneratorController extends AController {

	@Autowired
	private IPinCodeGeneratorService pinCodeGeneratorService;

	public CPinCodeGeneratorController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("getGeneratedPIN")
	public void getGeneratedPIN(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetGeneratedPINRequest request = fromJsonToObject(json, CGetGeneratedPINRequest.class);

		CResponse<CStringResponseContent> responseData = new CResponse<>();
		CStringResponseContent content = new CStringResponseContent();

		String value = null;

		try {
			value = pinCodeGeneratorService.getGeneratedPIN(request.getLogin(), request.getOldPINvalue());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
