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
import sk.qbsw.sed.client.model.brw.CUserActivityRecord;
import sk.qbsw.sed.client.request.CBrwActivityFetchRequest;
import sk.qbsw.sed.client.response.CBrwUserActivitiesFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwUserActivityService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.BRW_USER_ACTIVITY)
public class CBrwUserActivityController extends AController {

	@Autowired
	private IBrwUserActivityService tableService;

	public CBrwUserActivityController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping(CApiUrl.BRW_USER_ACTIVITY_COUNT)
	public void count(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwActivityFetchRequest request = fromJsonToObject(json, CBrwActivityFetchRequest.class);

		count(request.getCriteria(), tableService, response);
	}

	@RequestMapping(CApiUrl.BRW_USER_ACTIVITY_LOAD_DATA)
	public void loadData(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwActivityFetchRequest request = fromJsonToObject(json, CBrwActivityFetchRequest.class);

		CResponse<CBrwUserActivitiesFetchResponseContent> responseData = new CResponse<>();
		CBrwUserActivitiesFetchResponseContent content = new CBrwUserActivitiesFetchResponseContent();

		List<CUserActivityRecord> result = null;

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
