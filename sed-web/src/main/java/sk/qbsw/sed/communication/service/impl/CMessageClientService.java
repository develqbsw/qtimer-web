package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.message.CMessageRecord;
import sk.qbsw.sed.client.request.CGetMessagesRequest;
import sk.qbsw.sed.client.response.CMessagesResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IMessageClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CMessageClientService extends AClientService implements IMessageClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CMessageClientService() {
		super(CApiUrl.MESSAGE);
	}

	@Override
	public List<CMessageRecord> getMessages(Locale locale) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetMessagesRequest requestEntity = new CGetMessagesRequest(locale);
		Type type = new TypeToken<CResponse<CMessagesResponseContent>>() {
		}.getType();
		AServiceCall<CGetMessagesRequest, CMessagesResponseContent, List<CMessageRecord>> call = new AServiceCall<CGetMessagesRequest, CMessagesResponseContent, List<CMessageRecord>>() {

			@Override
			public List<CMessageRecord> getContentObject(CMessagesResponseContent content) {
				return content.getMessages();
			}
		};
		return call.call(request, getUrl(CApiUrl.MESSAGE_GET_MESSAGES), requestEntity, type); // CMessageController.getMessages()
	}

	@Override
	public String getNamesday(Locale locale) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetMessagesRequest requestEntity = new CGetMessagesRequest(locale);
		Type type = new TypeToken<CResponse<CStringResponseContent>>() {
		}.getType();
		AServiceCall<CGetMessagesRequest, CStringResponseContent, String> call = new AServiceCall<CGetMessagesRequest, CStringResponseContent, String>() {

			@Override
			public String getContentObject(CStringResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.MESSAGE_GET_NAMESDAY), requestEntity, type); // CMessageController.getNamesday()
	}
}
