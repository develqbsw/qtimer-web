package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.request.CGetGeneratedPINRequest;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IPinCodeGeneratorClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author moravcik
 *
 */
@Service
public class CPinCodeGeneratorClientService extends AClientService implements IPinCodeGeneratorClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CPinCodeGeneratorClientService() {
		super(CApiUrl.PIN_CODE_GENERATOR);
	}

	/**
	 * @see IPinCodeGeneratorClientService#getGeneratedPIN(String, String)
	 */
	@Override
	public String getGeneratedPin(String login, String olidPin) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetGeneratedPINRequest requestEntity = new CGetGeneratedPINRequest(login, olidPin);
		Type type = new TypeToken<CResponse<CStringResponseContent>>() {
		}.getType();
		AServiceCall<CGetGeneratedPINRequest, CStringResponseContent, String> call = new AServiceCall<CGetGeneratedPINRequest, CStringResponseContent, String>() {

			@Override
			public String getContentObject(CStringResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.PIN_CODE_GENERATOR_GET_GENERATED_PIN), requestEntity, type); // CPinCodeGeneratorController.getGeneratedPIN()
	}
}
