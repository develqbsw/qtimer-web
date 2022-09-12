package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CRequestReasonDataResponseContent;
import sk.qbsw.sed.client.response.CRequestReasonListsDataResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.IRequestReasonService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.REQUEST_REASON)
public class CRequestReasonController extends AController {

	@Autowired
	private IRequestReasonService requestReasonService;

	public CRequestReasonController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/getDetail")
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CRequestReasonDataResponseContent> responseData = new CResponse<>();
		CRequestReasonDataResponseContent content = new CRequestReasonDataResponseContent();

		CRequestReasonData requestReasonData = requestReasonService.getDetail(request);

		content.setRequestReasonData(requestReasonData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getReasonLists")
	public void getReasonLists(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CRequestReasonListsDataResponseContent> responseData = new CResponse<>();
		CRequestReasonListsDataResponseContent content = new CRequestReasonListsDataResponseContent();

		CRequestReasonListsData requestReasonListsData;
		try {
			requestReasonListsData = requestReasonService.getReasonLists(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setRequestReasonListsData(requestReasonListsData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.REQUEST_REASON_LIST_FOR_LISTBOX)
	public void getReasonListsForListbox(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord;
		try {
			codeListRecord = requestReasonService.getReasonListsForListbox();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/save")
	public void save(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRequestReasonData request = fromJsonToObject(json, CRequestReasonData.class);

		CResponse<CRequestReasonDataResponseContent> responseData = new CResponse<>();
		CRequestReasonDataResponseContent content = new CRequestReasonDataResponseContent();

		CRequestReasonData requestReasonData;
		try {
			requestReasonData = requestReasonService.save(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setRequestReasonData(requestReasonData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
