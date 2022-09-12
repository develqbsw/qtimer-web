package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.brw.CTmpTimeSheet;
import sk.qbsw.sed.client.request.CBrwMyTimeStampGenerateFetchRequest;
import sk.qbsw.sed.client.response.CBrwTimeStampGenerateFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CTmpTimeSheetResponseContent;
import sk.qbsw.sed.client.service.brw.IBrwMyTimeStampGenerateService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.exception.CJiraException;

@Controller
@RequestMapping(value = "/brwMyTimeStampGenerate")
public class CBrwMyTimeStampGenerateController extends AController {

	@Autowired
	private IBrwMyTimeStampGenerateService tableService;

	public CBrwMyTimeStampGenerateController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/add")
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTmpTimeSheet request = fromJsonToObject(json, CTmpTimeSheet.class);

		CResponse<CTmpTimeSheetResponseContent> responseData = new CResponse<>();
		CTmpTimeSheetResponseContent content = new CTmpTimeSheetResponseContent();

		CTmpTimeSheet tmpTimeSheet = null;

		try {
			tmpTimeSheet = tableService.add(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setTmpTimeSheet(tmpTimeSheet);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/delete")
	public void delete(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTmpTimeSheet request = fromJsonToObject(json, CTmpTimeSheet.class);

		CResponse<CTmpTimeSheetResponseContent> responseData = new CResponse<>();
		CTmpTimeSheetResponseContent content = new CTmpTimeSheetResponseContent();

		CTmpTimeSheet tmpTimeSheet = null;

		try {
			tableService.delete(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setTmpTimeSheet(tmpTimeSheet);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/deleteAll")
	public void deleteAll(@RequestBody String json, HttpServletResponse response) throws CApiException {

		try {
			tableService.deleteAllForUser();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/fetch")
	public void fetch(@RequestBody String json, HttpServletResponse response) throws CApiException, CJiraException {
		CBrwMyTimeStampGenerateFetchRequest request = fromJsonToObject(json, CBrwMyTimeStampGenerateFetchRequest.class);

		CResponse<CBrwTimeStampGenerateFetchResponseContent> responseData = new CResponse<>();
		CBrwTimeStampGenerateFetchResponseContent content = new CBrwTimeStampGenerateFetchResponseContent();

		List<CTmpTimeSheet> result = null;

		try {
			result = tableService.fetch(request.getStartRow(), request.getEndRow(), request.getSortProperty(), request.isSortAsc(), request.getCriteria());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/update")
	public void update(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTmpTimeSheet request = fromJsonToObject(json, CTmpTimeSheet.class);

		CResponse<CTmpTimeSheetResponseContent> responseData = new CResponse<>();
		CTmpTimeSheetResponseContent content = new CTmpTimeSheetResponseContent();

		CTmpTimeSheet tmpTimeSheet = null;

		try {
			tmpTimeSheet = tableService.update(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setTmpTimeSheet(tmpTimeSheet);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
