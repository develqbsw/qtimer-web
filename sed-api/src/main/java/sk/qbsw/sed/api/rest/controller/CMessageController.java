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
import sk.qbsw.sed.client.model.message.CMessageRecord;
import sk.qbsw.sed.client.request.CGetMessagesRequest;
import sk.qbsw.sed.client.response.CMessagesResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.client.service.business.IMessageService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = CApiUrl.MESSAGE)
public class CMessageController extends AController {

	@Autowired
	IMessageService messageService;

	public CMessageController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping(CApiUrl.MESSAGE_GET_MESSAGES)
	public void getMessages(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetMessagesRequest request = fromJsonToObject(json, CGetMessagesRequest.class);

		CResponse<CMessagesResponseContent> responseData = new CResponse<>();
		CMessagesResponseContent content = new CMessagesResponseContent();

		List<CMessageRecord> messages;
		try {
			messages = messageService.getMessages(request.getLocale());
		} catch (CBusinessException e) {
			throw new CApiException(e.getMessage(), e);
		}

		content.setMessages(messages);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(CApiUrl.MESSAGE_GET_NAMESDAY)
	public void getNamesday(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CGetMessagesRequest request = fromJsonToObject(json, CGetMessagesRequest.class);

		CResponse<CStringResponseContent> responseData = new CResponse<>();
		CStringResponseContent content = new CStringResponseContent();

		String value = "";
		try {
			value = messageService.getNamesDayMessage(request.getLocale());
		} catch (CBusinessException e) {
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
