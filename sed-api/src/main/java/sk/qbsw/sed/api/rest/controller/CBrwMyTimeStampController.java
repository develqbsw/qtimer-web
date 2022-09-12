package sk.qbsw.sed.api.rest.controller;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.api.controller.CUsersPanelWebSocket;
import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.request.CBrwTimeStampCountRequest;
import sk.qbsw.sed.client.request.CBrwTimeStampFetchRequest;
import sk.qbsw.sed.client.request.CBrwTimeStampMassChangeRequest;
import sk.qbsw.sed.client.request.CGetWorkTimeInIntervalRequest;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CBrwTimeStampFetchResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CTimeStampRecordResponseContent;
import sk.qbsw.sed.client.service.brw.IBrwTimeStampService;
import sk.qbsw.sed.client.service.business.IEmployeesStatusService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.util.CAuthorizationUtils;

@Controller
@RequestMapping(value = CApiUrl.BRW_MY_TIME_STAMP)
public class CBrwMyTimeStampController extends AController {

	@Autowired
	private IBrwTimeStampService tableService;

	@Autowired
	private IEmployeesStatusService employeesStatusService;

	@Autowired
	private CAuthorizationUtils authorizationUtils;

	public CBrwMyTimeStampController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping("/add")
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampRecord request = fromJsonToObject(json, CTimeStampRecord.class);

		CResponse<CTimeStampRecordResponseContent> responseData = new CResponse<>();
		CTimeStampRecordResponseContent content = new CTimeStampRecordResponseContent();

		CTimeStampRecord timeStampRecord = null;

		try {
			timeStampRecord = tableService.add(request);
			String newStatus = employeesStatusService.checkStatus(request.getEmployeeId());
			if (newStatus != null) {
				CUsersPanelWebSocket.sendAll(request.getEmployeeId() + "*" + newStatus);
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setTimeStampRecord(timeStampRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.BRW_MY_TIME_STAMP_COUNT)
	public void count(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwTimeStampCountRequest request = fromJsonToObject(json, CBrwTimeStampCountRequest.class);

		count(request.getCriteria(), tableService, response);
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_GET_DETAIL)
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CTimeStampRecordResponseContent> responseData = new CResponse<>();
		CTimeStampRecordResponseContent content = new CTimeStampRecordResponseContent();

		try {
			authorizationUtils.authorizationForTimeStampId(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}
		CTimeStampRecord timeStampRecord = tableService.getDetail(request);

		content.setTimeStampRecord(timeStampRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getWorkTimeInInterval")
	public void getWorkTimeInInterval(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetWorkTimeInIntervalRequest request = fromJsonToObject(json, CGetWorkTimeInIntervalRequest.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		long date = tableService.getWorkTimeInInterval(request.getUserId(), request.getDateFrom(), request.getDateTo());

		content.setValue(date);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.BRW_MY_TIME_STAMP_LOAD_DATA)
	public void loadData(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CBrwTimeStampFetchRequest request = fromJsonToObject(json, CBrwTimeStampFetchRequest.class);

		CResponse<CBrwTimeStampFetchResponseContent> responseData = new CResponse<>();
		CBrwTimeStampFetchResponseContent content = new CBrwTimeStampFetchResponseContent();

		List<CTimeStampRecord> result = null;

		try {
			result = tableService.loadData(request.getStartRow(), request.getEndRow(), request.getSortProperty(), request.isSortAsc(), request.getCriteria());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_SHOW_EDIT_BUTTON_ON_DETAIL)
	public void showEditButtonOnDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CBooleanResponseContent> responseData = new CResponse<>();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean showEditButtonOnDetail = Boolean.TRUE;
		try {
			// editačné tlačidlá na detaile zobrazím len ak prezerám moju ČZ ja,
			// nadriadený alebo admin
			showEditButtonOnDetail = authorizationUtils.authorizationForShowEditButtonOnDetail(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(showEditButtonOnDetail);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/update")
	public void update(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampRecord request = fromJsonToObject(json, CTimeStampRecord.class);

		CResponse<CTimeStampRecordResponseContent> responseData = new CResponse<>();
		CTimeStampRecordResponseContent content = new CTimeStampRecordResponseContent();

		CTimeStampRecord timeStampRecord = null;

		try {
			timeStampRecord = tableService.update(request);
			String newStatus = employeesStatusService.checkStatus(request.getEmployeeId());
			if (newStatus != null) {
				CUsersPanelWebSocket.sendAll(request.getEmployeeId() + "*" + newStatus);
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setTimeStampRecord(timeStampRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/massChangeTimestamps")
	public void massChangeTimestamps(@RequestBody String json, HttpServletResponse response) throws CApiException, CBusinessException {
		CBrwTimeStampMassChangeRequest request = fromJsonToObject(json, CBrwTimeStampMassChangeRequest.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long numberOfChangedRecords = 0L;

		try {
			numberOfChangedRecords = tableService.massChangeTimestamps(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}
		content.setValue(numberOfChangedRecords);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}
}
