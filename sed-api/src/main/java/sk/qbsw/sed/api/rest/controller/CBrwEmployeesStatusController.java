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
import sk.qbsw.sed.client.model.CEmployeesStatusNew;
import sk.qbsw.sed.client.model.brw.CEmployeesStatus;
import sk.qbsw.sed.client.request.CBrwEmployeesStatusFetchRequest;
import sk.qbsw.sed.client.response.CBrwEmployeesStatusFetchResponseContent;
import sk.qbsw.sed.client.response.CBrwEmployeesStatusNewFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.IEmployeesStatusService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.BRW_EMPLOYEES_STATUS)
public class CBrwEmployeesStatusController extends AController {

	@Autowired
	private IEmployeesStatusService employeesStatusService;

	public CBrwEmployeesStatusController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping(CApiUrl.BRW_EMPLOYEES_STATUS_FETCH)
	public void fetch(@RequestBody String json, HttpServletResponse response) throws CApiException {
		// túto metódu volá Q-Timer Reception

		CBrwEmployeesStatusFetchRequest request = fromJsonToObject(json, CBrwEmployeesStatusFetchRequest.class);

		CResponse<CBrwEmployeesStatusFetchResponseContent> responseData = new CResponse<>();
		CBrwEmployeesStatusFetchResponseContent content = new CBrwEmployeesStatusFetchResponseContent();

		List<CEmployeesStatus> result = null;

		try {
			result = employeesStatusService.fetchByCriteria(request.getCriteria());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(CApiUrl.BRW_EMPLOYEES_STATUS_FETCH_NEW)
	public void fetchCached(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CResponse<CBrwEmployeesStatusNewFetchResponseContent> responseData = new CResponse<>();
		CBrwEmployeesStatusNewFetchResponseContent content = new CBrwEmployeesStatusNewFetchResponseContent();

		List<CEmployeesStatusNew> result = null;

		try {
			result = employeesStatusService.fetch();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
