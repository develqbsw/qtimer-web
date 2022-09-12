package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.brw.CUserProjectRecord;
import sk.qbsw.sed.client.request.CBrwProjectCountRequest;
import sk.qbsw.sed.client.request.CBrwProjectFetchRequest;
import sk.qbsw.sed.client.response.CBrwUserProjectsFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwUserProjectService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/brwUserProject")
public class CBrwUserProjectController extends AController {

	@Autowired
	private IBrwUserProjectService tableService;

	public CBrwUserProjectController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/count")
	public void count(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwProjectCountRequest request = fromJsonToObject(json, CBrwProjectCountRequest.class);
		count(request.getCriteria(), tableService, response);
	}

	@RequestMapping("/loadData")
	public void loadData(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwProjectFetchRequest request = fromJsonToObject(json, CBrwProjectFetchRequest.class);

		CResponse<CBrwUserProjectsFetchResponseContent> responseData = new CResponse<>();
		CBrwUserProjectsFetchResponseContent content = new CBrwUserProjectsFetchResponseContent();

		List<CUserProjectRecord> result = null;

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
