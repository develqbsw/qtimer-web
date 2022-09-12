package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.request.CBrwHolidayCountRequest;
import sk.qbsw.sed.client.request.CBrwHolidayFetchRequest;
import sk.qbsw.sed.client.response.CBrwHolidayFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwHolidayService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/brwHoliday")
public class CBrwHolidayController extends AController {

	@Autowired
	private IBrwHolidayService tableService;

	public CBrwHolidayController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/count")
	public void count(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwHolidayCountRequest request = fromJsonToObject(json, CBrwHolidayCountRequest.class);

		count(request.getCriteria(), tableService, response);
	}

	@RequestMapping("/loadData")
	public void loadData(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwHolidayFetchRequest request = fromJsonToObject(json, CBrwHolidayFetchRequest.class);

		CResponse<CBrwHolidayFetchResponseContent> responseData = new CResponse<>();
		CBrwHolidayFetchResponseContent content = new CBrwHolidayFetchResponseContent();

		List<CHolidayRecord> result = null;

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
}
