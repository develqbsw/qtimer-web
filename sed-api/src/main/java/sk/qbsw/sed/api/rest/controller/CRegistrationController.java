package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.request.CRegisterOrganizationRequest;
import sk.qbsw.sed.client.service.business.IRegistrationService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/registration")
public class CRegistrationController extends AController {

	@Autowired
	private IRegistrationService registrationService;

	public CRegistrationController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/register")
	public void register(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRegisterOrganizationRequest request = fromJsonToObject(json, CRegisterOrganizationRequest.class);

		try {
			registrationService.register(request.getOrg(), request.getUser());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
