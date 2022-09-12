package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.communication.http.CHttpGetRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.ISystemInfoClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

@Service
public class CSystemInfoClientService extends AClientService implements ISystemInfoClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CSystemInfoClientService() {
		super(CApiUrl.SYSTEM_INFO);
	}

	@Override
	public String getVersion() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(null);

		Type type = new TypeToken<CResponse<CStringResponseContent>>() {
		}.getType();

		AServiceCall<Void, CStringResponseContent, String> call = new AServiceCall<Void, CStringResponseContent, String>() {

			@Override
			public String getContentObject(CStringResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.SYSTEM_INFO_GET_VERSION), null, type); // CSystemInfoController.getVersion()
	}
}
