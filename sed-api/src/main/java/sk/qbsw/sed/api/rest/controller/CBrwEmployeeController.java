package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.request.CBrwEmployeeCountRequest;
import sk.qbsw.sed.client.request.CBrwEmployeeLoadDataRequest;
import sk.qbsw.sed.client.response.CBrwCountResponseContent;
import sk.qbsw.sed.client.response.CEmployeeRecordListResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwEmployeeService;
import sk.qbsw.sed.client.ui.screen.restriction.users.CEmployeeRecord;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/brwEmployee")
public class CBrwEmployeeController extends AController {

	@Autowired
	private IBrwEmployeeService tableService;

	public CBrwEmployeeController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/count")
	public void count(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwEmployeeCountRequest request = fromJsonToObject(json, CBrwEmployeeCountRequest.class);

		CResponse<CBrwCountResponseContent> responseData = new CResponse<>();
		CBrwCountResponseContent content = new CBrwCountResponseContent();

		Long result = null;

		try {
			result = tableService.count(request.getCriteria());
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
		CBrwEmployeeLoadDataRequest request = fromJsonToObject(json, CBrwEmployeeLoadDataRequest.class);

		CResponse<CEmployeeRecordListResponseContent> responseData = new CResponse<>();
		CEmployeeRecordListResponseContent content = new CEmployeeRecordListResponseContent();

		List<CEmployeeRecord> result = null;

		try {
			result = tableService.loadData(request.getStartRow(), request.getEndRow(), request.getSortProperty(), request.isSortAsc(), request.getCriteria());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setEmployeeRecordList(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
