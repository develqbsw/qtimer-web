package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.codelist.IRequestStatusService;

@Controller
@RequestMapping(value = "/requestStatus")
public class CRequestStatusController extends AController {

	@Autowired
	private IRequestStatusService requestStatusService;

	public CRequestStatusController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("getValidRecords")
	public void getValidRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = requestStatusService.getValidRecords(false);

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
