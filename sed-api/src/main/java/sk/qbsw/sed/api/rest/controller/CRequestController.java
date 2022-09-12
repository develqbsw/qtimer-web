package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.request.CAddRequestRequest;
import sk.qbsw.sed.client.request.CApproveRequestFromEmailRequest;
import sk.qbsw.sed.client.request.CHomeOfficePermissionIntervalRequest;
import sk.qbsw.sed.client.request.CHomeOfficePermissionRequest;
import sk.qbsw.sed.client.request.CModifyRequestRequest;
import sk.qbsw.sed.client.request.CRejectRequestFromEmailRequest;
import sk.qbsw.sed.client.request.CRequestIdRequest;
import sk.qbsw.sed.client.response.CBooleanResponse;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CRequestRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.client.service.business.IRequestService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.util.CAuthorizationUtils;

@Controller
@RequestMapping(value = "/request")
public class CRequestController extends AController {

	@Autowired
	private IRequestService requestService;

	@Autowired
	private CAuthorizationUtils authorizationUtils;

	public CRequestController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/add")
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CAddRequestRequest request = fromJsonToObject(json, CAddRequestRequest.class);

		CResponse<CStringResponseContent> responseData = new CResponse<>();
		CStringResponseContent content = new CStringResponseContent();

		String value = null;

		try {
			authorizationUtils.authorizationForEmployeeId(request.getModel().getOwnerId());
			value = requestService.add(request.getType(), request.getModel(), request.isIgnoreDuplicity());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/approve")
	public void approve(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRequestIdRequest request = fromJsonToObject(json, CRequestIdRequest.class);

		try {
			authorizationUtils.authorizationForRequestId(request.getRequestId());
			requestService.approve(request.getRequestId());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/approveRequestFromEmail")
	public void approveRequestFromEmail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CApproveRequestFromEmailRequest request = fromJsonToObject(json, CApproveRequestFromEmailRequest.class);

		CBooleanResponse responseData = new CBooleanResponse();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean value = requestService.approveRequestFromEmail(request.getRequestId(), request.getRequestCode());

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/cancel")
	public void cancel(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRequestIdRequest request = fromJsonToObject(json, CRequestIdRequest.class);

		try {
			authorizationUtils.authorizationForRequestId(request.getRequestId());
			requestService.cancel(request.getRequestId());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/getDetail")
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRequestIdRequest request = fromJsonToObject(json, CRequestIdRequest.class);

		CResponse<CRequestRecordResponseContent> responseData = new CResponse<>();
		CRequestRecordResponseContent content = new CRequestRecordResponseContent();

		CRequestRecord requset = null;

		try {
			requset = requestService.getDetail(request.getRequestId());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setRequset(requset);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/isAllowedHomeOfficeForToday")
	public void isAllowedHomeOfficeForToday(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CHomeOfficePermissionRequest request = fromJsonToObject(json, CHomeOfficePermissionRequest.class);
		CResponse<CBooleanResponseContent> responseData = new CResponse<>();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean allowedHomeOffice = Boolean.FALSE;

		try {
			allowedHomeOffice = requestService.isAllowedHomeOfficeForToday(request.getUserId(), request.getDate());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(allowedHomeOffice);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/isAllowedHomeOfficeInInterval")
	public void isAllowedHomeOfficeInInterval(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CHomeOfficePermissionIntervalRequest request = fromJsonToObject(json, CHomeOfficePermissionIntervalRequest.class);
		CResponse<CBooleanResponseContent> responseData = new CResponse<>();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean allowedHomeOffice = Boolean.FALSE;

		try {
			allowedHomeOffice = requestService.isAllowedHomeOfficeInInterval(request.getUserId(), request.getClientId(), request.getDateFrom(), request.getDateTo());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(allowedHomeOffice);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/modify")
	public void modify(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyRequestRequest request = fromJsonToObject(json, CModifyRequestRequest.class);

		try {
			authorizationUtils.authorizationForEmployeeId(request.getModel().getOwnerId());
			requestService.modify(request.getId(), request.getModel(), request.isIgnoreDuplicity());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/reject")
	public void reject(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRequestIdRequest request = fromJsonToObject(json, CRequestIdRequest.class);

		try {
			authorizationUtils.authorizationForRequestId(request.getRequestId());
			requestService.reject(request.getRequestId());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/rejectRequestFromEmail")
	public void rejectRequestFromEmail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CRejectRequestFromEmailRequest request = fromJsonToObject(json, CRejectRequestFromEmailRequest.class);

		CBooleanResponse responseData = new CBooleanResponse();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean value = requestService.rejectRequestFromEmail(request.getRequestId(), request.getRequestCode());

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
