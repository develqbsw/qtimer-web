package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CJiraTokenGenerationRecord;
import sk.qbsw.sed.client.request.CJiraTokenGenerationRequest;
import sk.qbsw.sed.client.response.CJiraTokenGenerationResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.client.service.business.IJiraTokenGenerationService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.JIRA_TOKEN_GENERATION)
public class CJiraTokenGenerationController extends AController {

	@Autowired
	private IJiraTokenGenerationService jiraTokenGenerationService;

	public CJiraTokenGenerationController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping(CApiUrl.JIRA_TOKEN_GENERATION_LINK)
	public void getJiraTokenGenerationLink(@RequestBody String json, HttpServletResponse response) throws CApiException {
		
		CResponse<CJiraTokenGenerationResponseContent> responseData = new CResponse<>();
		CJiraTokenGenerationResponseContent content = new CJiraTokenGenerationResponseContent();

		CJiraTokenGenerationRecord record = null;

		try {
			record = jiraTokenGenerationService.getJiraTokenGenerationLink();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setRecord(record);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
	
	@RequestMapping(CApiUrl.JIRA_GENERATE_ACCESS_TOKEN)
	public void generateJiraAccessToken(@RequestBody String json, HttpServletResponse response) throws CApiException {
		
		CJiraTokenGenerationRequest request = fromJsonToObject(json, CJiraTokenGenerationRequest.class);
		CJiraTokenGenerationRecord record = request.getRecord();
		
		CResponse<CStringResponseContent> responseData = new CResponse<>();
		CStringResponseContent content = new CStringResponseContent();
		String jiraAccessToken = null;
		
		try {
			jiraAccessToken = jiraTokenGenerationService.generateJiraAccessToken(record);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(jiraAccessToken);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
