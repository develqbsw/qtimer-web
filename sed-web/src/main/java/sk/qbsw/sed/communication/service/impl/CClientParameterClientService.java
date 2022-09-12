package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.params.CParameter;
import sk.qbsw.sed.client.request.CGetClientParameterRequest;
import sk.qbsw.sed.client.response.CParameterResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IClientParameterClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author lobb
 *
 */
@Service
public class CClientParameterClientService extends AClientService implements IClientParameterClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CClientParameterClientService() {
		super(CApiUrl.CLIENT_PARAMETER);
	}

	@Override
	public CParameter getClientParameter(Long clientId, String name) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetClientParameterRequest requestEntity = new CGetClientParameterRequest(clientId, name);

		Type type = new TypeToken<CResponse<CParameterResponseContent>>() {
		}.getType();
		AServiceCall<CGetClientParameterRequest, CParameterResponseContent, CParameter> call = new AServiceCall<CGetClientParameterRequest, CParameterResponseContent, CParameter>() {

			@Override
			public CParameter getContentObject(CParameterResponseContent content) {
				return content.getParameter();
			}
		};
		return call.call(request, getUrl(CApiUrl.CLIENT_PARAMETER_GET), requestEntity, type); // CClientParameterController.getClientParameter()
	}
}
