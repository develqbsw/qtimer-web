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
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.client.request.CGetValidActivityGroupsRequest;
import sk.qbsw.sed.client.response.CActivityIntervalDataResponseContent;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CEmployeeActivityLimitsDataResponseContent;
import sk.qbsw.sed.client.response.CGroupsAIDataResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.IActivityTimeLimitsService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/activityTimeLimits")
public class CActivityTimeLimitsController extends AController {

	@Autowired
	private IActivityTimeLimitsService activityTimeLimitsService;

	public CActivityTimeLimitsController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/deleteGroup")
	public void deleteGroup(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CBooleanResponseContent> responseData = new CResponse<>();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean value = null;

		try {
			value = activityTimeLimitsService.deleteGroup(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getValidActivityGroups")
	public void getValidActivityGroups(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetValidActivityGroupsRequest request = fromJsonToObject(json, CGetValidActivityGroupsRequest.class);

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = activityTimeLimitsService.getValidActivityGroups(request.getClientId(), request.getActivityId(), request.getValidFlag());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/loadEmployeeLimitsDetail")
	public void loadEmployeeLimitsDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CEmployeeActivityLimitsDataResponseContent> responseData = new CResponse<>();
		CEmployeeActivityLimitsDataResponseContent content = new CEmployeeActivityLimitsDataResponseContent();

		CEmployeeActivityLimitsData employeeActivityLimitsData = null;

		try {
			employeeActivityLimitsData = activityTimeLimitsService.loadEmployeeLimitsDetail(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setEmployeeActivityLimitsData(employeeActivityLimitsData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/loadGroupDetail")
	public void loadGroupDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CGroupsAIDataResponseContent> responseData = new CResponse<>();
		CGroupsAIDataResponseContent content = new CGroupsAIDataResponseContent();

		CGroupsAIData groupsAIData = null;

		try {
			groupsAIData = activityTimeLimitsService.loadGroupDetail(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setGroupsAIData(groupsAIData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/loadIntervalDetail")
	public void loadIntervalDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CActivityIntervalDataResponseContent> responseData = new CResponse<>();
		CActivityIntervalDataResponseContent content = new CActivityIntervalDataResponseContent();

		CActivityIntervalData activityIntervalData = null;

		try {
			activityIntervalData = activityTimeLimitsService.loadIntervalDetail(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setActivityIntervalData(activityIntervalData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/saveEmployeeLimits")
	public void saveEmployeeLimits(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CEmployeeActivityLimitsData request = fromJsonToObject(json, CEmployeeActivityLimitsData.class);

		CResponse<CEmployeeActivityLimitsDataResponseContent> responseData = new CResponse<>();
		CEmployeeActivityLimitsDataResponseContent content = new CEmployeeActivityLimitsDataResponseContent();

		CEmployeeActivityLimitsData employeeActivityLimitsData = null;

		try {
			employeeActivityLimitsData = activityTimeLimitsService.saveEmployeeLimits(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setEmployeeActivityLimitsData(employeeActivityLimitsData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/saveGroup")
	public void saveGroup(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGroupsAIData request = fromJsonToObject(json, CGroupsAIData.class);

		CResponse<CGroupsAIDataResponseContent> responseData = new CResponse<>();
		CGroupsAIDataResponseContent content = new CGroupsAIDataResponseContent();

		CGroupsAIData groupsAIData = null;

		try {
			groupsAIData = activityTimeLimitsService.saveGroup(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setGroupsAIData(groupsAIData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/saveInterval")
	public void saveInterval(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CActivityIntervalData request = fromJsonToObject(json, CActivityIntervalData.class);

		CResponse<CActivityIntervalDataResponseContent> responseData = new CResponse<>();
		CActivityIntervalDataResponseContent content = new CActivityIntervalDataResponseContent();

		CActivityIntervalData activityIntervalData = null;

		try {
			activityIntervalData = activityTimeLimitsService.saveInterval(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setActivityIntervalData(activityIntervalData);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
