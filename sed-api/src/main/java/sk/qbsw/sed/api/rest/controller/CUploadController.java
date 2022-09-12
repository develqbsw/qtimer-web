package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.request.CUploadRequest;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CUploadResponseContent;
import sk.qbsw.sed.server.service.upload.CActivitytUploadProcess;
import sk.qbsw.sed.server.service.upload.CProjectUploadProcess;
import sk.qbsw.sed.server.service.upload.CUserUploadProcess;

@Controller
@RequestMapping(value = CApiUrl.UPLOAD)
public class CUploadController extends AController {
	@Autowired
	private CActivitytUploadProcess activitytUploadProcess;

	@Autowired
	private CProjectUploadProcess projectUploadProcess;

	@Autowired
	private CUserUploadProcess userUploadProcess;

	public CUploadController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping(CApiUrl.UPLOAD_ACTIVITIES)
	public void uploadActivities(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CUploadRequest request = fromJsonToObject(json, CUploadRequest.class);

		CResponse<CUploadResponseContent> responseData = new CResponse<>();
		CUploadResponseContent content = new CUploadResponseContent();

		try {
			content = activitytUploadProcess.upload(request.getFileRows());
		} catch (Exception e) {
			// handle exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(CApiUrl.UPLOAD_EMPLOYEES)
	public void uploadEmployees(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CUploadRequest request = fromJsonToObject(json, CUploadRequest.class);

		CResponse<CUploadResponseContent> responseData = new CResponse<>();
		CUploadResponseContent content = new CUploadResponseContent();

		try {
			content = userUploadProcess.upload(request.getFileRows());
		} catch (Exception e) {
			// handle exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(CApiUrl.UPLOAD_PROJECTS)
	public void uploadProjects(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CUploadRequest request = fromJsonToObject(json, CUploadRequest.class);

		CResponse<CUploadResponseContent> responseData = new CResponse<>();
		CUploadResponseContent content = new CUploadResponseContent();

		try {
			content = projectUploadProcess.upload(request.getFileRows());
		} catch (Exception e) {
			// handle exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
