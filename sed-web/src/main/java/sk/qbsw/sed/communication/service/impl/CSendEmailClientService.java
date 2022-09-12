package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CUserSystemEmailContainer;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.ISendEmailClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CSendEmailClientService extends AClientService implements ISendEmailClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CSendEmailClientService() {
		super(CApiUrl.SEND_EMAIL);
	}

	@Override
	public Long sendMissingEmployeesEmail(CUserSystemEmailContainer data) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CUserSystemEmailContainer requestEntity = data;

		Type type = new TypeToken<CResponse<CLongResponseContent>>() {
		}.getType();
		AServiceCall<CUserSystemEmailContainer, CLongResponseContent, Long> call = new AServiceCall<CUserSystemEmailContainer, CLongResponseContent, Long>() {

			@Override
			public Long getContentObject(CLongResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.SEND_MISSING_EMPLOYEES_EMAILS), requestEntity, type); // CSendEmailController.sendMissingEmployeesEmails()
	}
}
