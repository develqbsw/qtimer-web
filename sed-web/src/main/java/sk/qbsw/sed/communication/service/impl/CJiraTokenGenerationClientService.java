package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.client.request.CJiraTokenGenerationRequest;
import sk.qbsw.sed.client.request.CLongRequest;
import sk.qbsw.sed.client.response.CJiraTokenGenerationResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IJiraTokenGenerationClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CJiraTokenGenerationClientService extends AClientService implements IJiraTokenGenerationClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CJiraTokenGenerationClientService() {
		super(CApiUrl.JIRA_TOKEN_GENERATION);
	}

	@Override
	public CJiraTokenGenerationRecord getJiraTokenGenerationLink() throws CBussinessDataException {
		
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		
		CLongRequest requestEntity = new CLongRequest(null);

		Type type = new TypeToken<CResponse<CJiraTokenGenerationResponseContent>>() {
		}.getType();
		AServiceCall<CLongRequest, CJiraTokenGenerationResponseContent, CJiraTokenGenerationRecord> call = new AServiceCall<CLongRequest, CJiraTokenGenerationResponseContent, CJiraTokenGenerationRecord>() {

			@Override
			public CJiraTokenGenerationRecord getContentObject(CJiraTokenGenerationResponseContent content) {
				return content.getRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.JIRA_TOKEN_GENERATION_LINK), requestEntity, type); // CJiraTokenGenerationController.getJiraTokenGenerationLink()
	}

	@Override
	public String generateJiraAccessToken(final CJiraTokenGenerationRecord jiraTokenGenerationRecord) throws CBussinessDataException {
		
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		
		CJiraTokenGenerationRequest requestEntity = new CJiraTokenGenerationRequest(jiraTokenGenerationRecord);

		Type type = new TypeToken<CResponse<CStringResponseContent>>() {
		}.getType();
		AServiceCall<CJiraTokenGenerationRequest, CStringResponseContent, String> call = new AServiceCall<CJiraTokenGenerationRequest, CStringResponseContent, String>() {

			@Override
			public String getContentObject(CStringResponseContent content) {
				return content.getValue();
			}
		};
		
		return call.call(request, getUrl(CApiUrl.JIRA_GENERATE_ACCESS_TOKEN), requestEntity, type); // CJiraTokenGenerationController.generateJiraAccessToken()

	}

}
