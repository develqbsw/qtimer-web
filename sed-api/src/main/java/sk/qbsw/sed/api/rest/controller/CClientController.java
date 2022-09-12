package sk.qbsw.sed.api.rest.controller;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.client.model.security.CClientInfo;
import sk.qbsw.sed.client.response.CClientDetailRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.IClientService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/client")
public class CClientController extends AController {

	@Autowired
	private IClientService clientService;

	public CClientController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("getDetail")
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CClientDetailRecordResponseContent> responseData = new CResponse<>();
		CClientDetailRecordResponseContent content = new CClientDetailRecordResponseContent();

		CClientDetailRecord record = null;

		try {
			record = clientService.getDetail(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setRecord(record);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("updateDetail")
	public void updateDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CClientDetailRecord request = fromJsonToObject(json, CClientDetailRecord.class);

		try {
			clientService.updateDetail(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("updateFlags")
	public void updateFlags(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CClientInfo request = fromJsonToObject(json, CClientInfo.class);

		try {
			clientService.updateFlags(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
