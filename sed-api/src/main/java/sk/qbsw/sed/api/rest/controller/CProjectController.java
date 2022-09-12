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
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.codelist.CResultProjectsGroups;
import sk.qbsw.sed.client.request.CModifyProjectRequest;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CProjectRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CResultProjectsGroupsResponseContent;
import sk.qbsw.sed.client.service.codelist.IProjectService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.PROJECT)
public class CProjectController extends AController {

	@Autowired
	private IProjectService projectService;

	public CProjectController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("add")
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CProjectRecord request = fromJsonToObject(json, CProjectRecord.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long value = null;
		try {
			value = projectService.add(request);
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
			codeListRecord = projectService.getAllRecords();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getAllRecordsWithGroups")
	public void getAllRecordsWithGroups(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CResultProjectsGroupsResponseContent> responseData = new CResponse<>();
		CResultProjectsGroupsResponseContent content = new CResultProjectsGroupsResponseContent();

		CResultProjectsGroups resultProjectsGroups = null;

		try {
			resultProjectsGroups = projectService.getAllRecordsWithGroups();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResultProjectsGroups(resultProjectsGroups);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getDetail")
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CProjectRecordResponseContent> responseData = new CResponse<>();
		CProjectRecordResponseContent content = new CProjectRecordResponseContent();

		CProjectRecord projectRecord = projectService.getDetail(request);

		content.setProjectRecord(projectRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.PROJECT_GET_VALID_RECORDS)
	public void getValidRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = projectService.getValidRecords();
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
			codeListRecord = projectService.getValidRecords(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getValidRecordsForUser")
	public void getValidRecordsForUser(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CCodeListRecordResponseContent> responseData = new CResponse<>();
		CCodeListRecordResponseContent content = new CCodeListRecordResponseContent();

		List<CCodeListRecord> codeListRecord = null;

		try {
			codeListRecord = projectService.getValidRecordsForUser(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setCodeListRecord(codeListRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getValidRecordsWithGroups")
	public void getValidRecordsWithGroups(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CResponse<CResultProjectsGroupsResponseContent> responseData = new CResponse<>();
		CResultProjectsGroupsResponseContent content = new CResultProjectsGroupsResponseContent();

		CResultProjectsGroups resultProjectsGroups = null;

		try {
			resultProjectsGroups = projectService.getValidRecordsWithGroups();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResultProjectsGroups(resultProjectsGroups);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("modify")
	public void modify(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyProjectRequest request = fromJsonToObject(json, CModifyProjectRequest.class);

		try {
			projectService.modify(request.getId(), request.getNewRecord(), request.getTimestamp());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
