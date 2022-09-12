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
import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.request.CModifyActivityRequest;
import sk.qbsw.sed.client.response.CActivityRecordResponseContent;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.codelist.IActivityService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.ACTIVITY)
public class CActivityController extends AController {

	@Autowired
	private IActivityService activityService;

	public CActivityController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("add")
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CActivityRecord request = fromJsonToObject(json, CActivityRecord.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long value = null;

		try {
			value = activityService.add(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getAllRecords")
	public void getAllRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityService.getAllRecords();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getDetail")
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CActivityRecordResponseContent> responseData = new CResponse<>();
		CActivityRecordResponseContent content = new CActivityRecordResponseContent();

		CActivityRecord activityRecord = activityService.getDetail(request);

		content.setActivityRecord(activityRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getValidNonWorkingRecords")
	public void getValidNonWorkingRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityService.getValidNonWorkingRecords();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.ACTIVITY_GET_VALID_RECORDS)
	public void getValidRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityService.getValidRecords();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getValidRecordsForLimits")
	public void getValidRecordsForLimits(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityService.getValidRecordsForLimits();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getValidRecordsForTimesheet")
	public void getValidRecordsForTimesheet(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityService.getValidRecords(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getValidWorkingRecords")
	public void getValidWorkingRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityService.getValidWorkingRecords();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.ACTIVITY_GET_VALID_WORKING_RECORDS_FOR_USER)
	public void getValidWorkingRecordsForUser(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityService.getValidWorkingRecordsForUser();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("modify")
	public void modify(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyActivityRequest request = fromJsonToObject(json, CModifyActivityRequest.class);

		try {
			activityService.modify(request.getId(), request.getNewRecord());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
