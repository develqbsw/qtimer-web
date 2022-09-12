package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.model.request.CRequestRecordForGraph;
import sk.qbsw.sed.client.request.CBrwSubordinateRequestCountRequest;
import sk.qbsw.sed.client.request.CBrwSubordinateRequestFetchRequest;
import sk.qbsw.sed.client.response.CBrwRequestFetchResponseContent;
import sk.qbsw.sed.client.response.CRequestForGraphResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwRequestService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/brwSubordinateRequest")
public class CBrwSubordinateRequestController extends AController {

	@Autowired
	private IBrwRequestService tableService;

	public CBrwSubordinateRequestController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/count")
	public void count(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwSubordinateRequestCountRequest request = fromJsonToObject(json, CBrwSubordinateRequestCountRequest.class);
		count(request.getCriteria(), tableService, response);
	}

	@RequestMapping("/loadData")
	public void loadData(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwSubordinateRequestFetchRequest request = fromJsonToObject(json, CBrwSubordinateRequestFetchRequest.class);

		CResponse<CBrwRequestFetchResponseContent> responseData = new CResponse<>();
		CBrwRequestFetchResponseContent content = new CBrwRequestFetchResponseContent();

		List<CRequestRecord> result = null;

		try {
			result = tableService.loadData(request.getStartRow(), request.getEndRow(), request.getSortProperty(), request.isSortAsc(), request.getCriteria());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/loadDataForGraph")
	public void loadDataForGraph(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwSubordinateRequestFetchRequest request = fromJsonToObject(json, CBrwSubordinateRequestFetchRequest.class);

		CResponse<CRequestForGraphResponseContent> responseData = new CResponse<>();
		CRequestForGraphResponseContent content = new CRequestForGraphResponseContent();

		List<CRequestRecordForGraph> result = null;

		try {
			result = tableService.loadDataForGraph(request.getCriteria());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
