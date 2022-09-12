package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationClientRecord;
import sk.qbsw.sed.client.model.registration.CRegistrationUserRecord;
import sk.qbsw.sed.client.request.CRegisterOrganizationRequest;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IRegistrationClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;

@Service
public class CRegistrationClientService extends AClientService implements IRegistrationClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CRegistrationClientService() {
		super(CApiUrl.REGISTRATION);
	}

	@Override
	public void register(CRegistrationClientRecord org, CRegistrationUserRecord user) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(null);

		CRegisterOrganizationRequest requestEntity = new CRegisterOrganizationRequest(org, user);

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<CRegisterOrganizationRequest, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<CRegisterOrganizationRequest, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		call.call(request, getUrl(CApiUrl.REGISTER), requestEntity, type); // CRegistrationController.register()
	}
}
