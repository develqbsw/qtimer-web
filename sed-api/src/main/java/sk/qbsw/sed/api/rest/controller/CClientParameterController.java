package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.params.CParameter;
import sk.qbsw.sed.client.request.CGetClientParameterRequest;
import sk.qbsw.sed.client.response.CParameterListResponseContent;
import sk.qbsw.sed.client.response.CParameterResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.business.IClientParameterService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/clientParameter")
public class CClientParameterController extends AController {

	@Autowired
	private IClientParameterService clientParameterService;

	public CClientParameterController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/getClientParameter")
	public void getClientParameter(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetClientParameterRequest request = fromJsonToObject(json, CGetClientParameterRequest.class);

		CResponse<CParameterResponseContent> responseData = new CResponse<>();
		CParameterResponseContent content = new CParameterResponseContent();

		CParameter parameter = null;

		try {
			parameter = clientParameterService.getClientParameter(request.getClientId(), request.getName());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setParameter(parameter);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getClientParameters")
	public void getClientParameters(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CParameterListResponseContent> responseData = new CResponse<>();
		CParameterListResponseContent content = new CParameterListResponseContent();

		List<CParameter> list = null;

		try {
			list = clientParameterService.getClientParameters(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setList(list);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
