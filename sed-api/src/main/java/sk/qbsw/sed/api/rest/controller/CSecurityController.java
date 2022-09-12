package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.api.security.CSecurityContext;
import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.exception.CDataValidationException;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.request.CAutoLoginRequest;
import sk.qbsw.sed.client.request.CLoginRequest;
import sk.qbsw.sed.client.request.CLogoutRequest;
import sk.qbsw.sed.client.response.CLoginResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.EErrorCode;
import sk.qbsw.sed.core.service.ISecurityService;
import sk.qbsw.sed.framework.security.exception.CSecurityException;

@Controller
@RequestMapping(value = CApiUrl.SECURITY)
public class CSecurityController extends AController {

	/** domain security interface */
	@Autowired
	private ISecurityService securityService;

	public CSecurityController() {
		gson = new GsonBuilder().create();
	}

	@RequestMapping(value = CApiUrl.SECURITY_LOGIN, method = RequestMethod.POST, consumes = { "application/json; charset=UTF-8" }, produces = { "application/json; charset=UTF-8" })
	public void login(@RequestBody String json, HttpSession session, HttpServletResponse response) throws CApiException, CDataValidationException {
		CLoginRequest request = fromJsonToObject(json, CLoginRequest.class);

		CResponse<CLoginResponseContent> responseData = new CResponse<>();
		CLoginResponseContent content = new CLoginResponseContent();

		CLoggedUserRecord user = new CLoggedUserRecord();
		try {
			user = securityService.login(request.getLogin(), request.getPassword(), request.getStaySignedIn(), request.getLocale());
		} catch (CSecurityException ex) {
			// handle security exception to api security exception
			throw new CApiException(ex.getMessage(), ex);
		}
		content.setSecurityToken(user.getSecurityToken());
		content.setUser(user);
		responseData.setContent(content);

		// add security token for login to session
		CSecurityContext.addSecurityToken(content.getSecurityToken(), request.getLogin(), session);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = "/loginByAutoLoginToken")
	public void loginByAutoLoginToken(@RequestBody String json, HttpSession session, HttpServletResponse response) throws CApiException, CDataValidationException {
		CAutoLoginRequest request = fromJsonToObject(json, CAutoLoginRequest.class);

		CResponse<CLoginResponseContent> responseData = new CResponse<>();
		CLoginResponseContent content = new CLoginResponseContent();

		CLoggedUserRecord user = new CLoggedUserRecord();
		try {
			user = securityService.loginByAutoLoginToken(request.getAutoLoginToken(), request.getLocale());
		} catch (CSecurityException ex) {
			// handle security exception to api security exception
			throw new CApiException(ex.getMessage(), ex);
		}
		content.setSecurityToken(user != null ? user.getSecurityToken() : null);
		content.setUser(user);
		responseData.setContent(content);

		// add security token for login to session
		if (user != null) {
			CSecurityContext.addSecurityToken(content.getSecurityToken(), user.getLogin(), session);
		}

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/logout")
	public void logout(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLogoutRequest request = fromJsonToObject(json, CLogoutRequest.class);

		try {
			securityService.logout(request.getLogin());
		} catch (CSecurityException e) {
			// handle security exception to api security exception
			throw new CApiException(EErrorCode.SYSTEM_ERROR, e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
