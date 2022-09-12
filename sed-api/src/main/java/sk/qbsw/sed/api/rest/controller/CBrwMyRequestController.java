package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.request.CBrwMyRequestFetchRequest;
import sk.qbsw.sed.client.response.CBrwRequestFetchResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.brw.IBrwRequestService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/brwMyRequest")
public class CBrwMyRequestController extends AController {

	@Autowired
	private IBrwRequestService tableService;

	public CBrwMyRequestController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	/**
	 * !!! Volaju iOS a Andorid apps !!!
	 * 
	 * @param json
	 * @param response
	 * @throws CApiException
	 */
	@RequestMapping("/fetch")
	public void fetch(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwMyRequestFetchRequest request = fromJsonToObject(json, CBrwMyRequestFetchRequest.class);

		CResponse<CBrwRequestFetchResponseContent> responseData = new CResponse<>();
		CBrwRequestFetchResponseContent content = new CBrwRequestFetchResponseContent();

		List<CRequestRecord> result = null;

		try {
			result = tableService.fetch(0, Integer.MAX_VALUE, request.getCriteria());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
