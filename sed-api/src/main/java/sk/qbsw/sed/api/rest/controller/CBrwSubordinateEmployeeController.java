package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.detail.CUserDetailRecord;
import sk.qbsw.sed.client.response.CBrwEmployeeFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwSubordinateEmployeeService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/brwSubordinateEmployee")
public class CBrwSubordinateEmployeeController extends AController {

	@Autowired
	private IBrwSubordinateEmployeeService tableService;

	public CBrwSubordinateEmployeeController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/fetch")
	public void fetch(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CResponse<CBrwEmployeeFetchResponseContent> responseData = new CResponse<>();
		CBrwEmployeeFetchResponseContent content = new CBrwEmployeeFetchResponseContent();

		List<CUserDetailRecord> result = null;

		try {
			result = tableService.fetch();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
