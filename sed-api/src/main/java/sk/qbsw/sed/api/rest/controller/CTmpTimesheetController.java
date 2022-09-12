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
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.ITmpTimesheetService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/tmpTimesheet")
public class CTmpTimesheetController extends AController {

	@Autowired
	private ITmpTimesheetService tmpTimesheetService;

	public CTmpTimesheetController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/createCopyById")
	public void createCopyById(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		try {
			tmpTimesheetService.createCopyById(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/deleteAll")
	public void deleteAll(@RequestBody String json, HttpServletResponse response) throws CApiException {
		try {
			tmpTimesheetService.deleteAll();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/deleteById")
	public void deleteById(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		try {
			tmpTimesheetService.deleteById(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/findRealizedGenerationProcesses")
	public void findRealizedGenerationProcesses(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = tmpTimesheetService.findRealizedGenerationProcesses(request);

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/update")
	public void update(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTmpTimeSheet request = fromJsonToObject(json, CTmpTimeSheet.class);

		CResponse<CLockRecordResponseContent> responseData = new CResponse<>();
		CLockRecordResponseContent content = new CLockRecordResponseContent();

		CLockRecord lockRecord = null;

		try {
			lockRecord = tmpTimesheetService.update(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setLockRecord(lockRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/updateDurationCorrected")
	public void updateDurationCorrected(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTmpTimeSheet request = fromJsonToObject(json, CTmpTimeSheet.class);

		try {
			tmpTimesheetService.updateDurationCorrected(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
