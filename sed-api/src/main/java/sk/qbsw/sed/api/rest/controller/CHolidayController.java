package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.request.CGetClientRecordsForTheYearRequest;
import sk.qbsw.sed.client.request.CModifyHolidayRequest;
import sk.qbsw.sed.client.response.CHolidayRecordListResponseContent;
import sk.qbsw.sed.client.response.CHolidayRecordResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.service.codelist.IHolidayService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;

@Controller
@RequestMapping(value = "/holiday")
public class CHolidayController extends AController {

	@Autowired
	private IHolidayService holidayService;

	public CHolidayController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("add")
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CHolidayRecord request = fromJsonToObject(json, CHolidayRecord.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long value = null;

		try {
			value = holidayService.add(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(value);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("cloneCurrentYearClientRecordsForNextYear")
	public void cloneCurrentYearClientRecordsForNextYear(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CHolidayRecordListResponseContent> responseData = new CResponse<>();
		CHolidayRecordListResponseContent content = new CHolidayRecordListResponseContent();

		List<CHolidayRecord> list = null;

		try {
			list = holidayService.cloneCurrentYearClientRecordsForNextYear(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setList(list);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getClientRecordsForTheYear")
	public void getClientRecordsForTheYear(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetClientRecordsForTheYearRequest request = fromJsonToObject(json, CGetClientRecordsForTheYearRequest.class);

		CResponse<CHolidayRecordListResponseContent> responseData = new CResponse<>();
		CHolidayRecordListResponseContent content = new CHolidayRecordListResponseContent();

		List<CHolidayRecord> list = null;

		try {
			list = holidayService.getClientRecordsForTheYear(request.getClientId(), request.getSelectedYearDate());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setList(list);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("getDetail")
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CHolidayRecordResponseContent> responseData = new CResponse<>();
		CHolidayRecordResponseContent content = new CHolidayRecordResponseContent();

		CHolidayRecord holidayRecord = holidayService.getDetail(request);

		content.setHolidayRecord(holidayRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("modify")
	public void modify(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyHolidayRequest request = fromJsonToObject(json, CModifyHolidayRequest.class);

		try {
			holidayService.modify(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
