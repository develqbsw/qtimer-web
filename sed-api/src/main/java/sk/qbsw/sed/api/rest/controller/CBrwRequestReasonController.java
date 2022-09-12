package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CRequestReasonRecord;
import sk.qbsw.sed.client.request.CBrwLoadDataRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CBrwRequestReasonFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwRequestReasonService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/brwRequestReason")
public class CBrwRequestReasonController extends AController {

	@Autowired
	private IBrwRequestReasonService tableService;

	public CBrwRequestReasonController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/count")
	public void count(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CResponse<CBrwCountResponseContent> responseData = new CResponse<>();
		CBrwCountResponseContent content = new CBrwCountResponseContent();

		Long result = null;

		try {
			result = tableService.count();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCount(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/loadData")
	public void loadData(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwLoadDataRequest request = fromJsonToObject(json, CBrwLoadDataRequest.class);

		CResponse<CBrwRequestReasonFetchResponseContent> responseData = new CResponse<>();
		CBrwRequestReasonFetchResponseContent content = new CBrwRequestReasonFetchResponseContent();

		List<CRequestReasonRecord> result = null;

		try {
			result = tableService.loadData(request.getStartRow(), request.getEndRow(), request.getSortProperty(), request.isSortAsc());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
