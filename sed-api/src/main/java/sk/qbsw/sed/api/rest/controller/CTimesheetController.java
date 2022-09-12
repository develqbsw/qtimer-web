package sk.qbsw.sed.api.rest.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.gson.GsonBuilder;

import sk.qbsw.sed.api.controller.CUsersPanelWebSocket;
import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.exception.CApiException;
import sk.qbsw.sed.client.model.CStatsRecord;
import sk.qbsw.sed.client.model.IStatusConstants;
import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;
import sk.qbsw.sed.client.model.codelist.CProjectDuration;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.security.CLoggedUserRecord;
import sk.qbsw.sed.client.model.timestamp.CLastExternaProjectActivity;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CPredefinedTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CTimeStampAddRecord;
import sk.qbsw.sed.client.model.timestamp.CTimeStampRecord;
import sk.qbsw.sed.client.request.CConfirmTimesheetRecordsRequest;
import sk.qbsw.sed.client.request.CGenerateUserTimestampsFromPreparedItemsRequest;
import sk.qbsw.sed.client.request.CGetDataForGraphOfAttendanceRequest;
import sk.qbsw.sed.client.request.CGetDataForGraphProjectsRequest;
import sk.qbsw.sed.client.request.CGetInfoForMobileTimerRequest;
import sk.qbsw.sed.client.request.CGetListOfUsersWithCorruptedSummaryReportRequest;
import sk.qbsw.sed.client.request.CGetSumAndAverageTimeForUsersRequest;
import sk.qbsw.sed.client.request.CGetSumAndAverageTimeRequest;
import sk.qbsw.sed.client.request.CLoadPredefinedTimestampRequest;
import sk.qbsw.sed.client.request.CModifyTimesheetRequest;
import sk.qbsw.sed.client.request.CModifyWorkingRequest;
import sk.qbsw.sed.client.request.CMultipleActivityRequest;
import sk.qbsw.sed.client.request.CSplitTimestampRequest;
import sk.qbsw.sed.client.request.CStatsRequest;
import sk.qbsw.sed.client.request.CStopNonWorkingRequest;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CGetDataForGraphOfAttendanceResponseContent;
import sk.qbsw.sed.client.response.CGetDataForGraphOfProjectsResponseContent;
import sk.qbsw.sed.client.response.CGetDataForStatsResponseContent;
import sk.qbsw.sed.client.response.CGetInfoForMobileTimerResponseContent;
import sk.qbsw.sed.client.response.CGetLastProjectToActivityRelationMapResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeForUsersResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeResponseContent;
import sk.qbsw.sed.client.response.CLastExternaProjectActivityResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CMultipleActivityResponseContent;
import sk.qbsw.sed.client.response.CPredefinedInteligentTimeStampResponseContent;
import sk.qbsw.sed.client.response.CPredefinedTimeStampResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringListResponseContent;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.client.response.CTimeStampRecordResponseContent;
import sk.qbsw.sed.client.response.CdniSNeukoncenouZnackouResponseContent;
import sk.qbsw.sed.client.service.business.IEmployeesStatusService;
import sk.qbsw.sed.client.service.business.ITimesheetService;
import sk.qbsw.sed.framework.security.exception.CBusinessException;
import sk.qbsw.sed.server.service.codelist.IActivityConstant;
import sk.qbsw.sed.server.util.CAuthorizationUtils;

@Controller
@RequestMapping(value = CApiUrl.TIMESHEET)
public class CTimesheetController extends AController {

	@Autowired
	private ITimesheetService timesheetService;

	@Autowired
	private CAuthorizationUtils authorizationUtils;

	@Autowired
	private IEmployeesStatusService employeesStatusService;

	public CTimesheetController() {
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_ADD)
	public void add(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampRecord request = fromJsonToObject(json, CTimeStampRecord.class);

		CResponse<CLockRecordResponseContent> responseData = new CResponse<>();
		CLockRecordResponseContent content = new CLockRecordResponseContent();

		CLockRecord lockRecord = null;

		try {
			authorizationUtils.authorizationForEmployeeId(request.getEmployeeId());
			lockRecord = timesheetService.add(request);

			String newStatus = employeesStatusService.checkStatus(request.getEmployeeId());
			if (newStatus != null) {
				CUsersPanelWebSocket.sendAll(request.getEmployeeId() + "*" + newStatus);
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setLockRecord(lockRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/cancelTimesheetRecords")
	public void cancelTimesheetRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CConfirmTimesheetRecordsRequest request = fromJsonToObject(json, CConfirmTimesheetRecordsRequest.class);

		try {
			for (Long employeeId : request.getUsers()) {
				authorizationUtils.authorizationForEmployeeId(employeeId);
			}

			timesheetService.cancelTimesheetRecords(request.getScreenType(), request.getUsers(), request.getDateFrom(), request.getDateTo(), request.getUserId(), request.isAlsoEmployees(),
					request.isAlsoSuperiors());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/confirmTimesheetRecords")
	public void confirmTimesheetRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CConfirmTimesheetRecordsRequest request = fromJsonToObject(json, CConfirmTimesheetRecordsRequest.class);

		CResponse<CdniSNeukoncenouZnackouResponseContent> responseData = new CResponse<>();
		CdniSNeukoncenouZnackouResponseContent content = new CdniSNeukoncenouZnackouResponseContent();

		try {
			for (Long employeeId : request.getUsers()) {
				authorizationUtils.authorizationForEmployeeId(employeeId);
			}

			List<Calendar> list = timesheetService.confirmTimesheetRecords(request.getScreenType(), request.getUsers(), request.getDateFrom(), request.getDateTo(), request.getUserId(),
					request.isAlsoEmployees(), request.isAlsoSuperiors());

			content.setDniSNeukoncenouZnackou(list);

			responseData.setContent(content);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_DELETE)
	public void delete(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CLockRecordResponseContent> responseData = new CResponse<>();
		CLockRecordResponseContent content = new CLockRecordResponseContent();

		CLockRecord lockRecord = null;

		try {
			authorizationUtils.authorizationForTimeStampId(request);
			lockRecord = timesheetService.delete(request);

			CTimeStampRecord record = timesheetService.getDetail(lockRecord.getId());

			String newStatus = employeesStatusService.checkStatus(record);
			if (newStatus != null) {
				CUsersPanelWebSocket.sendAll(record.getEmployeeId() + "*" + newStatus);
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setLockRecord(lockRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/findUserLastExternalProgramActivity")
	public void findUserLastExternalProgramActivity(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CLastExternaProjectActivityResponseContent> responseData = new CResponse<>();
		CLastExternaProjectActivityResponseContent content = new CLastExternaProjectActivityResponseContent();

		CLastExternaProjectActivity lastExternaProjectActivity = null;

		try {
			lastExternaProjectActivity = timesheetService.findUserLastExternalProgramActivity(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setLastExternaProjectActivity(lastExternaProjectActivity);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/generateApprovedEmployeesAbsenceRecords")
	public void generateApprovedEmployeesAbsenceRecords(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetListOfUsersWithCorruptedSummaryReportRequest request = fromJsonToObject(json, CGetListOfUsersWithCorruptedSummaryReportRequest.class);

		CResponse<CBooleanResponseContent> responseData = new CResponse<>();
		CBooleanResponseContent content = new CBooleanResponseContent();

		Boolean result = null;

		try {
			result = timesheetService.generateApprovedEmployeesAbsenceRecords(request.getUserId(), request.getFrom(), request.getTo());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/generateUserTimestampsFromPreparedItems")
	public void generateUserTimestampsFromPreparedItems(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGenerateUserTimestampsFromPreparedItemsRequest request = fromJsonToObject(json, CGenerateUserTimestampsFromPreparedItemsRequest.class);

		CResponse<CStringResponseContent> responseData = new CResponse<>();
		CStringResponseContent content = new CStringResponseContent();

		String result = null;

		try {
			result = timesheetService.generateUserTimestampsFromPreparedItems(request.getUserId(), request.getFrom(), request.getTo(), request.getSummaryWorkDurationInMinutes());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_DATA_FOR_ATTENDANCE)
	public void getDataForGraphOfAttendance(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CGetDataForGraphOfAttendanceRequest request;

		try {
			request = fromJsonToObject(json, CGetDataForGraphOfAttendanceRequest.class);
		} catch (CApiException e) {
			request = null; // error parsing JSON, set null so data for current
			// month will be returned
		}

		CResponse<CGetDataForGraphOfAttendanceResponseContent> responseData = new CResponse<>();
		CGetDataForGraphOfAttendanceResponseContent content = new CGetDataForGraphOfAttendanceResponseContent();

		List<CAttendanceDuration> dataForGraph = null;

		try {
			// no parameters in url
			if (request == null || request.getCalendarFrom() == null || request.getCalendarTo() == null) {

				Calendar calendarFrom = Calendar.getInstance();
				calendarFrom.set(Calendar.DAY_OF_MONTH, 1); // this take first
				// day of month
				Calendar calendarTo = Calendar.getInstance(); // this last day
				// of month today
				calendarTo.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));

				dataForGraph = timesheetService.getDataForGraphOfAttendance(calendarFrom, calendarTo);
			} else { // parameters set
				dataForGraph = timesheetService.getDataForGraphOfAttendance(request.getCalendarFrom(), request.getCalendarTo());
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setDataForGraph(dataForGraph);
		content.setActualDate(Calendar.getInstance());
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_DATA_FOR_GRAPH_OF_PROJECTS, method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public void getDataForGraphOfProjects(@RequestBody String json, HttpServletResponse response) throws CApiException {

		CGetDataForGraphProjectsRequest request;

		try {
			request = fromJsonToObject(json, CGetDataForGraphProjectsRequest.class);
		} catch (CApiException e) {
			request = null; // error parsing JSON, set null so data for current
			// month will be returned
		}

		CResponse<CGetDataForGraphOfProjectsResponseContent> responseData = new CResponse<>();
		CGetDataForGraphOfProjectsResponseContent content = new CGetDataForGraphOfProjectsResponseContent();

		List<CProjectDuration> dataForGraph = null;

		try {

			// no parameters in url
			if (request == null || request.getCalendarFrom() == null || request.getCalendarTo() == null) {

				Calendar calendarFrom = Calendar.getInstance();
				calendarFrom.set(Calendar.DAY_OF_MONTH, 1); // this take first
				// day of month
				Calendar calendarTo = Calendar.getInstance(); // this last day
				// of month today
				calendarTo.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));

				dataForGraph = timesheetService.getDataForGraphOfProjects(calendarFrom, calendarTo);
			} else { // parameters set
				dataForGraph = timesheetService.getDataForGraphOfProjects(request.getCalendarFrom(), request.getCalendarTo());
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setDataForGraph(dataForGraph);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_DATA_FOR_GRAPH_OF_STATS, method = RequestMethod.POST, produces = { "application/json; charset=UTF-8" })
	public void getDataForGraphOfStats(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CStatsRequest request = fromJsonToObject(json, CStatsRequest.class);

		CResponse<CGetDataForStatsResponseContent> responseData = new CResponse<>();
		CGetDataForStatsResponseContent content = new CGetDataForStatsResponseContent();

		List<CStatsRecord> dataForGraph = null;

		try {
			dataForGraph = timesheetService.getDataForGraphOfStats(request.getFilter());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setDataForGraph(dataForGraph);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_GET_DETAIL)
	public void getDetail(@RequestBody String json, HttpServletResponse response) throws CApiException {
		Long request = fromJsonToObject(json, Long.class);

		CResponse<CTimeStampRecordResponseContent> responseData = new CResponse<>();
		CTimeStampRecordResponseContent content = new CTimeStampRecordResponseContent();

		try {
			/*
			 * v autorizácií zohľadňujeme okrem mňa, admina a podriadených aj prístupných
			 * userov aby bolo možné zobraziť aj detail prístupných userov
			 */
			authorizationUtils.authorizationForTimeStampIdExtended(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		CTimeStampRecord timeStampRecord = timesheetService.getDetail(request);

		content.setTimeStampRecord(timeStampRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getInfoForMobileTimer")
	public void getInfoForMobileTimer(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetInfoForMobileTimerRequest request = fromJsonToObject(json, CGetInfoForMobileTimerRequest.class);

		CResponse<CGetInfoForMobileTimerResponseContent> responseData = new CResponse<>();

		CGetInfoForMobileTimerResponseContent content = null;

		try {
			content = timesheetService.getInfoForMobileTimer(request.getCountToday());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getLastProjectToActivityRelationMap")
	public void getLastProjectToActivityRelationMap(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CResponse<CGetLastProjectToActivityRelationMapResponseContent> responseData = new CResponse<>();
		CGetLastProjectToActivityRelationMapResponseContent content = new CGetLastProjectToActivityRelationMapResponseContent();

		Map<Long, Long> result = null;

		try {
			result = timesheetService.getLastProjectToActivityRelationMap();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setResult(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getListOfUsersWithCorruptedSummaryReport")
	public void getListOfUsersWithCorruptedSummaryReport(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetListOfUsersWithCorruptedSummaryReportRequest request = fromJsonToObject(json, CGetListOfUsersWithCorruptedSummaryReportRequest.class);

		CResponse<CStringListResponseContent> responseData = new CResponse<>();
		CStringListResponseContent content = new CStringListResponseContent();

		List<String> result = null;

		try {
			result = timesheetService.getListOfUsersWithCorruptedSummaryReport(request.getUserId(), request.getFrom(), request.getTo());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setList(result);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getSumAndAverageTime")
	public void getSumAndAverageTime(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetSumAndAverageTimeRequest request = fromJsonToObject(json, CGetSumAndAverageTimeRequest.class);

		CResponse<CGetSumAndAverageTimeResponseContent> responseData = new CResponse<>();

		CGetSumAndAverageTimeResponseContent content = null;

		try {
			content = timesheetService.getSumAndAverageTimeInTimeInterval(request.getDateFrom(), request.getDateTo());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/getSumAndAverageTimeForUsers")
	public void getSumAndAverageTimeForUsers(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CGetSumAndAverageTimeForUsersRequest request = fromJsonToObject(json, CGetSumAndAverageTimeForUsersRequest.class);

		CResponse<CGetSumAndAverageTimeForUsersResponseContent> responseData = new CResponse<>();
		CGetSumAndAverageTimeForUsersResponseContent content = null;

		try {
			content = timesheetService.getSumAndAverageTimeInTimeIntervalForUsers(request.getFilter());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_LOAD_PREDEFINED_VALUE)
	public void loadPredefinedTimestamp(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLoadPredefinedTimestampRequest request = fromJsonToObject(json, CLoadPredefinedTimestampRequest.class);

		CResponse<CPredefinedTimeStampResponseContent> responseData = new CResponse<>();
		CPredefinedTimeStampResponseContent content = new CPredefinedTimeStampResponseContent();

		CPredefinedTimeStamp predefinedTimeStamp = null;

		try {
			predefinedTimeStamp = timesheetService.loadPredefinedValue(request.getUserId(), request.getForSubordinateEmployee());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setPredefinedTimeStamp(predefinedTimeStamp);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/loadPredefinedTimestampForReceptionPanel")
	public void loadPredefinedTimestampForReceptionPanel(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLoadPredefinedTimestampRequest request = fromJsonToObject(json, CLoadPredefinedTimestampRequest.class);

		CResponse<CPredefinedInteligentTimeStampResponseContent> responseData = new CResponse<>();
		CPredefinedInteligentTimeStampResponseContent content = new CPredefinedInteligentTimeStampResponseContent();

		CPredefinedInteligentTimeStamp predefinedTimeStamp = null;

		try {
			predefinedTimeStamp = timesheetService.loadPredefinedInteligentValueForReceptionPanel(request.getUserId(), request.getTimeToPredefine());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setPredefinedInteligentTimeStamp(predefinedTimeStamp);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/loadPredefinedTimestampForTimerPanel")
	public void loadPredefinedTimestampForTimerPanel(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CLoadPredefinedTimestampRequest request = fromJsonToObject(json, CLoadPredefinedTimestampRequest.class);

		CResponse<CPredefinedInteligentTimeStampResponseContent> responseData = new CResponse<>();
		CPredefinedInteligentTimeStampResponseContent content = new CPredefinedInteligentTimeStampResponseContent();

		CPredefinedInteligentTimeStamp predefinedTimeStamp = null;

		try {
			predefinedTimeStamp = timesheetService.loadPredefinedInteligentValueForUserTimerPanel(request.getUserId(), request.getTimeToPredefine());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setPredefinedInteligentTimeStamp(predefinedTimeStamp);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_MODIFY)
	public void modify(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyTimesheetRequest request = fromJsonToObject(json, CModifyTimesheetRequest.class);

		CResponse<CLockRecordResponseContent> responseData = new CResponse<>();
		CLockRecordResponseContent content = new CLockRecordResponseContent();

		CLockRecord lockRecord = null;

		try {
			authorizationUtils.authorizationForTimeStampId(request.getId());
			lockRecord = timesheetService.modify(request.getNewRecord());

			String newStatus = employeesStatusService.checkStatus(request.getNewRecord().getEmployeeId());

			if (newStatus != null) {
				CUsersPanelWebSocket.sendAll(request.getNewRecord().getEmployeeId() + "*" + newStatus);
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setLockRecord(lockRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/modifyWorking")
	public void modifyWorking(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CModifyWorkingRequest request = fromJsonToObject(json, CModifyWorkingRequest.class);

		try {
			authorizationUtils.authorizationForEmployeeId(request.getRecord().getEmployeeId());
			timesheetService.modifyWorking(request.getId(), request.getRecord());
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/multipleActivity")
	public void multipleActivity(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CMultipleActivityRequest request = fromJsonToObject(json, CMultipleActivityRequest.class);

		try {
			CAuthorizationUtils.authorizeReceptionUser();
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		CResponse<CMultipleActivityResponseContent> responseData = new CResponse<>();

		String activity = request.getActivity();

		Logger.getLogger(this.getClass()).info("Multiple activity start. activity: " + activity);

		List<Long> usersSuccessful = new ArrayList<>();
		List<Long> usersFail = new ArrayList<>();

		for (CLoggedUserRecord loggedUserRecord : request.getUsers()) {

			try {
				timesheetService.executeActivity(loggedUserRecord, activity);

				usersSuccessful.add(loggedUserRecord.getUserId());
			} catch (CBusinessException e) {
				Logger.getLogger(this.getClass()).info("Execute activity failed. activity: " + activity + " user: " + loggedUserRecord.getSurname(), e);
				usersFail.add(loggedUserRecord.getUserId());
			}
		}

		CMultipleActivityResponseContent content = new CMultipleActivityResponseContent();
		content.setUsersSuccessful(usersSuccessful);
		content.setUsersFail(usersFail);

		Logger.getLogger(this.getClass()).info("Multiple activity finish. usersSuccessful: " + usersSuccessful + " usersFail: " + usersFail);

		responseData.setContent(content);

		for (Long userId : usersSuccessful) {
			String status = "";

			if (activity.equals(IActivityConstant.ACTIVITY_WORK_START)
					|| activity.equals(IActivityConstant.ACTIVITY_BREAK_STOP)
					|| activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_STOP)) {
				status = IStatusConstants.IN_WORK;
			} else if (activity.equals(IActivityConstant.ACTIVITY_WORK_OUTSIDE_START)) {
				status = IStatusConstants.MEETING;
			} else if (activity.equals(IActivityConstant.ACTIVITY_WORK_STOP)) {
				status = IStatusConstants.OUT_OF_WORK;
			} else if (activity.equals(IActivityConstant.ACTIVITY_BREAK_START)) {
				status = IStatusConstants.WORK_BREAK;
			}

			CUsersPanelWebSocket.sendAll(userId + "*" + status);

			try {
				employeesStatusService.setStatus(userId, status);
			} catch (CBusinessException e) {
				// handle security exception to api security exception
				throw new CApiException(e.getMessage(), e);
			}
		}

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping(value = CApiUrl.TIMESHEET_SPLIT)
	public void split(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CSplitTimestampRequest request = fromJsonToObject(json, CSplitTimestampRequest.class);

		CResponse<CLockRecordResponseContent> responseData = new CResponse<>();
		CLockRecordResponseContent content = new CLockRecordResponseContent();

		CLockRecord lockRecord = null;

		try {
			authorizationUtils.authorizationForTimeStampId(request.getRecord().getId());
			lockRecord = timesheetService.split(request.getRecord(), request.getSplitTime());

			String newStatus = employeesStatusService.checkStatus(request.getRecord().getEmployeeId());
			if (newStatus != null) {
				CUsersPanelWebSocket.sendAll(request.getRecord().getEmployeeId() + "*" + newStatus);
			}
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setLockRecord(lockRecord);
		responseData.setContent(content);

		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/startNonWorking")
	public void startNonWorking(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampAddRecord request = fromJsonToObject(json, CTimeStampAddRecord.class);

		CResponse<CLongResponseContent> responseData = new CResponse<>();
		CLongResponseContent content = new CLongResponseContent();

		Long message = null;
		
		if (request.getTime() == null) {
			// mobila appka neposiela cas, musime robit so serverovym
			request.setTime(new Date());
		}

		try {
			authorizationUtils.authorizationForEmployeeId(request.getEmployeeId());
			message = timesheetService.startNonWorking(request);

			String status;
			if (request.getNonWorkingActivityId() != null
					&& (CPredefinedInteligentTimeStamp.ACTIVITY_WORK_ALERTNESS == request.getNonWorkingActivityId().intValue()
							|| CPredefinedInteligentTimeStamp.ACTIVITY_WORK_INTERACTIVE == request.getNonWorkingActivityId().intValue())) {
				status = IStatusConstants.OUT_OF_WORK;
			} else {
				status = IStatusConstants.WORK_BREAK;
			}

			CUsersPanelWebSocket.sendAll(request.getEmployeeId() + "*" + status);
			employeesStatusService.setStatus(request.getEmployeeId(), status);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		content.setValue(message);
		responseData.setContent(content);
		
		responseService.writeToResponse(response, responseService.createResponse(responseData.getContent(), gson));
	}

	@RequestMapping("/startWorking")
	public void startWorking(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampAddRecord request = fromJsonToObject(json, CTimeStampAddRecord.class);

		if (request.getTime() == null) {
			// mobila appka neposiela cas, musime robit so serverovym
			request.setTime(new Date());
		}

		try {
			authorizationUtils.authorizationForEmployeeId(request.getEmployeeId());
			timesheetService.startWorking(request);

			String status;

			if (request.getOutside() != null && request.getOutside()) {
				status = IStatusConstants.MEETING;
			} else if (request.getHomeOffice() != null && request.getHomeOffice()) {
				status = IStatusConstants.HOME_OFFICE;
			} else {
				status = IStatusConstants.IN_WORK;
			}

			CUsersPanelWebSocket.sendAll(request.getEmployeeId() + "*" + status);
			employeesStatusService.setStatus(request.getEmployeeId(), status);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/stopInteractiveWork")
	public void stopInteractiveWork(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampAddRecord request = fromJsonToObject(json, CTimeStampAddRecord.class);

		try {
			authorizationUtils.authorizationForEmployeeId(request.getEmployeeId());
			timesheetService.stopInteractiveWork(request);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/stopNonWorking")
	public void stopNonWorking(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampAddRecord request = fromJsonToObject(json, CTimeStampAddRecord.class);

		if (request.getTime() == null) {
			// mobila appka neposiela cas, musime robit so serverovym
			request.setTime(new Date());
		}

		try {
			authorizationUtils.authorizationForEmployeeId(request.getEmployeeId());
			timesheetService.stopNonWorking(request);

			String status;

			if (request.getOutside() != null && request.getOutside()) {
				status = IStatusConstants.MEETING;
			} else if (request.getHomeOffice() != null && request.getHomeOffice()) {
				status = IStatusConstants.HOME_OFFICE;
			} else {
				status = IStatusConstants.IN_WORK;
			}

			CUsersPanelWebSocket.sendAll(request.getEmployeeId() + "*" + status);
			employeesStatusService.setStatus(request.getEmployeeId(), status);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/stopNonWorkingWithContinueWorkFlag")
	public void stopNonWorkingWithContinueWorkFlag(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CStopNonWorkingRequest request = fromJsonToObject(json, CStopNonWorkingRequest.class);

		try {
			authorizationUtils.authorizationForEmployeeId(request.getRecord().getEmployeeId());
			timesheetService.stopNonWorking(request.getRecord(), request.isContinueWork());

			String status = IStatusConstants.OUT_OF_WORK;

			if (request.isContinueWork()) {
				status = IStatusConstants.OUT_OF_WORK;
				if (request.getRecord().getOutside() != null && request.getRecord().getOutside()) {
					status = IStatusConstants.MEETING;
				} else if (request.getRecord().getHomeOffice() != null && request.getRecord().getHomeOffice()) {
					status = IStatusConstants.HOME_OFFICE;
				} else {
					status = IStatusConstants.IN_WORK;
				}
			}

			CUsersPanelWebSocket.sendAll(request.getRecord().getEmployeeId() + "*" + status);
			employeesStatusService.setStatus(request.getRecord().getEmployeeId(), status);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}

	@RequestMapping("/stopWorking")
	public void stopWorking(@RequestBody String json, HttpServletResponse response) throws CApiException {
		CTimeStampAddRecord request = fromJsonToObject(json, CTimeStampAddRecord.class);

		if (request.getTime() == null) {
			// mobila appka neposiela cas, musime robit so serverovym
			request.setTime(new Date());
		}

		try {
			authorizationUtils.authorizationForEmployeeId(request.getEmployeeId());
			timesheetService.stopWorking(request);

			CUsersPanelWebSocket.sendAll(request.getEmployeeId() + "*" + IStatusConstants.OUT_OF_WORK);
			employeesStatusService.setStatus(request.getEmployeeId(), IStatusConstants.OUT_OF_WORK);
		} catch (CBusinessException e) {
			// handle security exception to api security exception
			throw new CApiException(e.getMessage(), e);
		}

		responseService.writeToResponse(response, responseService.createEmptyResponse(gson));
	}
}
