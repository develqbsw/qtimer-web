package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.request.CAutoLoginRequest;
import sk.qbsw.sed.client.request.CLoginRequest;
import sk.qbsw.sed.client.request.CLogoutRequest;
import sk.qbsw.sed.client.response.CEmptyResponseContent;
import sk.qbsw.sed.client.response.CLoginResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IAuthenticationClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.model.CLoggedUser;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CAuthenticationClientService extends AClientService implements IAuthenticationClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CAuthenticationClientService() {
		super(CApiUrl.SECURITY);
	}

	@Override
	public CLoggedUser authenticate(String login, String password, Boolean staySignedIn, String locale) throws CBussinessDataException {
		CLoginRequest requestEntity = new CLoginRequest();
		requestEntity.setLogin(login);
		requestEntity.setPassword(password);
		requestEntity.setStaySignedIn(staySignedIn);
		requestEntity.setLocale(locale);
		final CHttpPostRequest request = new CHttpPostRequest(null);

		Type type = new TypeToken<CResponse<CLoginResponseContent>>() {
		}.getType();
		AServiceCall<CLoginRequest, CLoginResponseContent, CLoggedUser> call = new AServiceCall<CLoginRequest, CLoginResponseContent, CLoggedUser>() {

			@Override
			public CLoggedUser getContentObject(CLoginResponseContent content) {
				return new CLoggedUser(content.getUser(), content.getSecurityToken(), request.getCookies());
			}
		};
		return call.call(request, getUrl(CApiUrl.SECURITY_LOGIN), requestEntity, type); // CSecurityController.login()
	}

	@Override
	public CLoggedUser authenticateByAutoLoginToken(String token, String clientLocale) throws CBussinessDataException {
		CAutoLoginRequest requestEntity = new CAutoLoginRequest();
		requestEntity.setAutoLoginToken(token);
		requestEntity.setLocale(clientLocale);
		final CHttpPostRequest request = new CHttpPostRequest(null);

		Type type = new TypeToken<CResponse<CLoginResponseContent>>() {
		}.getType();
		AServiceCall<CAutoLoginRequest, CLoginResponseContent, CLoggedUser> call = new AServiceCall<CAutoLoginRequest, CLoginResponseContent, CLoggedUser>() {

			@Override
			public CLoggedUser getContentObject(CLoginResponseContent content) {
				return new CLoggedUser(content.getUser(), content.getSecurityToken(), request.getCookies());
			}
		};
		return call.call(request, getUrl(CApiUrl.SECURITY_AUTO_LOGIN), requestEntity, type); // CSecurityController.loginByAutoLoginToken()
	}

	@Override
	public void logout(String login) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CLogoutRequest requestEntity = new CLogoutRequest();
		requestEntity.setLogin(login);

		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CLogoutRequest, CEmptyResponseContent, Void> call = new AServiceCall<CLogoutRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.SECURITY_LOGOUT), requestEntity, type); // CSecurityController.logout()
	}
}
