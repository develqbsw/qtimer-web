package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.CStatsFilter;
import sk.qbsw.sed.client.model.CStatsRecord;
import sk.qbsw.sed.client.model.codelist.CAttendanceDuration;
import sk.qbsw.sed.client.model.codelist.CGetListOfUsersWithCorruptedSummaryReport;
import sk.qbsw.sed.client.model.codelist.CProjectDuration;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.timestamp.CPredefinedInteligentTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CPredefinedTimeStamp;
import sk.qbsw.sed.client.model.timestamp.CSubrodinateTimeStampBrwFilterCriteria;
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
import sk.qbsw.sed.client.request.CSplitTimestampRequest;
import sk.qbsw.sed.client.request.CStatsRequest;
import sk.qbsw.sed.client.request.CStopNonWorkingRequest;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CEmptyResponseContent;
import sk.qbsw.sed.client.response.CGetDataForGraphOfAttendanceResponseContent;
import sk.qbsw.sed.client.response.CGetDataForGraphOfProjectsResponseContent;
import sk.qbsw.sed.client.response.CGetDataForStatsResponseContent;
import sk.qbsw.sed.client.response.CGetInfoForMobileTimerResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeForUsersResponseContent;
import sk.qbsw.sed.client.response.CGetSumAndAverageTimeResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CLongResponseContent;
import sk.qbsw.sed.client.response.CPredefinedInteligentTimeStampResponseContent;
import sk.qbsw.sed.client.response.CPredefinedTimeStampResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CStringResponseContent;
import sk.qbsw.sed.client.response.CTimeStampRecordResponseContent;
import sk.qbsw.sed.client.response.CdniSNeukoncenouZnackouResponseContent;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.ITimesheetClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CTimesheetClientService extends AClientService implements ITimesheetClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CTimesheetClientService() {
		super(CApiUrl.TIMESHEET);
	}

	@Override
	public CTimeStampRecord getDetail(Long timeStampId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CTimeStampRecordResponseContent>>() {
		}.getType();
		AServiceCall<Long, CTimeStampRecordResponseContent, CTimeStampRecord> call = new AServiceCall<Long, CTimeStampRecordResponseContent, CTimeStampRecord>() {

			@Override
			public CTimeStampRecord getContentObject(CTimeStampRecordResponseContent content) {
				return content.getTimeStampRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_GET_DETAIL), timeStampId, type); // CTimesheetController.getDetail()
	}

	@Override
	public CLockRecord add(CTimeStampRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CTimeStampRecord, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CTimeStampRecord, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_ADD), record, type); // CTimesheetController.add()
	}

	@Override
	public CLockRecord modify(CTimeStampRecord newRecord) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyTimesheetRequest requestEntity = new CModifyTimesheetRequest();
		requestEntity.setId(newRecord.getId());
		requestEntity.setNewRecord(newRecord);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CModifyTimesheetRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CModifyTimesheetRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_MODIFY), requestEntity, type); // CTimesheetController.modify()
	}

	@Override
	public CLockRecord delete(Long id) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<Long, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<Long, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_DELETE), id, type); // CTimesheetController.delete()
	}

	@Override
	public CPredefinedTimeStamp loadPredefinedValue(Long userId, Boolean forSubordinateEmployee) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CLoadPredefinedTimestampRequest requestEntity = new CLoadPredefinedTimestampRequest(userId, forSubordinateEmployee);
		Type type = new TypeToken<CResponse<CPredefinedTimeStampResponseContent>>() {
		}.getType();
		AServiceCall<CLoadPredefinedTimestampRequest, CPredefinedTimeStampResponseContent, CPredefinedTimeStamp> call = new AServiceCall<CLoadPredefinedTimestampRequest, CPredefinedTimeStampResponseContent, CPredefinedTimeStamp>() {

			@Override
			public CPredefinedTimeStamp getContentObject(CPredefinedTimeStampResponseContent content) {
				return content.getPredefinedTimeStamp();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_LOAD_PREDEFINED_VALUE), requestEntity, type); // CTimesheetController.loadPredefinedTimestamp()
	}

	@Override
	public CPredefinedInteligentTimeStamp loadPredefinedInteligentValueForUserTimerPanel(Long userId, Date timeToPredefine) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CLoadPredefinedTimestampRequest requestEntity = new CLoadPredefinedTimestampRequest(userId, timeToPredefine);
		Type type = new TypeToken<CResponse<CPredefinedInteligentTimeStampResponseContent>>() {
		}.getType();
		AServiceCall<CLoadPredefinedTimestampRequest, CPredefinedInteligentTimeStampResponseContent, CPredefinedInteligentTimeStamp> call = new AServiceCall<CLoadPredefinedTimestampRequest, CPredefinedInteligentTimeStampResponseContent, CPredefinedInteligentTimeStamp>() {

			@Override
			public CPredefinedInteligentTimeStamp getContentObject(CPredefinedInteligentTimeStampResponseContent content) {
				return content.getPredefinedInteligentTimeStamp();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_LOAD_PREDEFINED_TIMESTAMP_FOR_TIMER_PANEL), requestEntity, type); // CTimesheetController.loadPredefinedTimestampForTimerPanel()
	}

	@Override
	public Long startNonWorking(CTimeStampAddRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CLongResponseContent>>() {
		}.getType();
		AServiceCall<CTimeStampAddRecord, CLongResponseContent, Long> call = new AServiceCall<CTimeStampAddRecord, CLongResponseContent, Long>() {

			@Override
			public Long getContentObject(CLongResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_START_NON_WORKING), record, type); // CTimesheetController.startNonWorking()
	}

	@Override
	public void startWorking(CTimeStampAddRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void> call = new AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.TIMESHEET_START_WORKING), record, type); // CTimesheetController.startWorking()
	}

	@Override
	public void modifyWorking(Long id, CTimeStampAddRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CModifyWorkingRequest requestEntity = new CModifyWorkingRequest(record, id);

		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CModifyWorkingRequest, CEmptyResponseContent, Void> call = new AServiceCall<CModifyWorkingRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.TIMESHEET_MODIFY_WORKING), requestEntity, type); // CTimesheetController.modifyWorking()
	}

	@Override
	public void stopWorking(CTimeStampAddRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void> call = new AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.TIMESHEET_STOP_WORKING), record, type); // CTimesheetController.stopWorking()
	}

	@Override
	public void stopNonWorking(CTimeStampAddRecord record, boolean continueWork) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CStopNonWorkingRequest requestEntity = new CStopNonWorkingRequest(record, continueWork);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CStopNonWorkingRequest, CEmptyResponseContent, Void> call = new AServiceCall<CStopNonWorkingRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.TIMESHEET_STOP_NON_WORKING_WITH_CONTINUE_WORK_FLAG), requestEntity, type); // CTimesheetController.stopNonWorkingWithContinueWorkFlag()
	}

	@Override
	public void stopNonWorking(CTimeStampAddRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void> call = new AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.TIMESHEET_STOP_NON_WORKING), record, type); // CTimesheetController.stopNonWorking()
	}

	@Override
	public void stopInteractiveWork(CTimeStampAddRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void> call = new AServiceCall<CTimeStampAddRecord, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.TIMESHEET_STOP_INTERACTIVE_WORK), record, type); // CTimesheetController.stopInteractiveWork()
	}

	@Override
	public Boolean generateApprovedEmployeesAbsenceRecords(Long userId, CGetListOfUsersWithCorruptedSummaryReport summary) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CGetListOfUsersWithCorruptedSummaryReportRequest requestEntity = new CGetListOfUsersWithCorruptedSummaryReportRequest(userId, summary.getFrom(), summary.getTo());

		Type type = new TypeToken<CResponse<CBooleanResponseContent>>() {
		}.getType();
		AServiceCall<CGetListOfUsersWithCorruptedSummaryReportRequest, CBooleanResponseContent, Boolean> call = new AServiceCall<CGetListOfUsersWithCorruptedSummaryReportRequest, CBooleanResponseContent, Boolean>() {

			@Override
			public Boolean getContentObject(CBooleanResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_APPROVED_EMPLOYEES_ABSENCE_RECORDS), requestEntity, type); // CTimesheetController.generateApprovedEmployeesAbsenceRecords()
	}

	@Override
	public String generateUserTimestampsFromPreparedItems(Long userId, Date dateFrom, Date dateTo, Long summaryWorkDurationInMinutes) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CGenerateUserTimestampsFromPreparedItemsRequest requestEntity = new CGenerateUserTimestampsFromPreparedItemsRequest(userId, dateFrom, dateTo, summaryWorkDurationInMinutes);

		Type type = new TypeToken<CResponse<CStringResponseContent>>() {
		}.getType();
		AServiceCall<CGenerateUserTimestampsFromPreparedItemsRequest, CStringResponseContent, String> call = new AServiceCall<CGenerateUserTimestampsFromPreparedItemsRequest, CStringResponseContent, String>() {

			@Override
			public String getContentObject(CStringResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_USER_TIMESTAMPS_FROM_PREPARED_ITEMS), requestEntity, type); // CTimesheetController.generateUserTimestampsFromPreparedItems()
	}

	@Override
	public List<Calendar> confirmTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors)
			throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CConfirmTimesheetRecordsRequest requestEntity = new CConfirmTimesheetRecordsRequest(screenType, users, dateFrom, dateTo, userId, alsoEmployees, alsoSuperiors);

		Type type = new TypeToken<CResponse<CdniSNeukoncenouZnackouResponseContent>>() {
		}.getType();
		AServiceCall<CConfirmTimesheetRecordsRequest, CdniSNeukoncenouZnackouResponseContent, List<Calendar>> call = new AServiceCall<CConfirmTimesheetRecordsRequest, CdniSNeukoncenouZnackouResponseContent, List<Calendar>>() {

			@Override
			public List<Calendar> getContentObject(CdniSNeukoncenouZnackouResponseContent content) {
				return content.getDniSNeukoncenouZnackou();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_CONFIRM_RECORDS), requestEntity, type); // CTimesheetController.confirmTimesheetRecords()
	}

	@Override
	public void cancelTimesheetRecords(String screenType, Set<Long> users, Date dateFrom, Date dateTo, Long userId, boolean alsoEmployees, boolean alsoSuperiors) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CConfirmTimesheetRecordsRequest requestEntity = new CConfirmTimesheetRecordsRequest(screenType, users, dateFrom, dateTo, userId, alsoEmployees, alsoSuperiors);
		Type type = new TypeToken<CResponse<CEmptyResponseContent>>() {
		}.getType();
		AServiceCall<CConfirmTimesheetRecordsRequest, CEmptyResponseContent, Void> call = new AServiceCall<CConfirmTimesheetRecordsRequest, CEmptyResponseContent, Void>() {

			@Override
			public Void getContentObject(CEmptyResponseContent content) {
				return null;
			}
		};
		call.call(request, getUrl(CApiUrl.TIMESHEET_CANCEL_RECORDS), requestEntity, type); // CTimesheetController.cancelTimesheetRecords()
	}

	@Override
	public List<CProjectDuration> getDataForGraphOfProjects(Calendar calendarFrom, Calendar calendarTo) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetDataForGraphProjectsRequest requestEntity = new CGetDataForGraphProjectsRequest(calendarFrom, calendarTo);

		Type type = new TypeToken<CResponse<CGetDataForGraphOfProjectsResponseContent>>() {
		}.getType();
		AServiceCall<CGetDataForGraphProjectsRequest, CGetDataForGraphOfProjectsResponseContent, List<CProjectDuration>> call = new AServiceCall<CGetDataForGraphProjectsRequest, CGetDataForGraphOfProjectsResponseContent, List<CProjectDuration>>() {

			@Override
			public List<CProjectDuration> getContentObject(CGetDataForGraphOfProjectsResponseContent content) {
				return content.getDataForGraph();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_DATA_FOR_GRAPH_OF_PROJECTS), requestEntity, type); // CTimesheetController.getDataForGraphOfProjects()
	}

	@Override
	public List<CAttendanceDuration> getDataForGraphOfAttendance(Calendar calendarFrom, Calendar calendarTo) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetDataForGraphOfAttendanceRequest requestEntity = new CGetDataForGraphOfAttendanceRequest(calendarFrom, calendarTo);

		Type type = new TypeToken<CResponse<CGetDataForGraphOfAttendanceResponseContent>>() {
		}.getType();
		AServiceCall<CGetDataForGraphOfAttendanceRequest, CGetDataForGraphOfAttendanceResponseContent, List<CAttendanceDuration>> call = new AServiceCall<CGetDataForGraphOfAttendanceRequest, CGetDataForGraphOfAttendanceResponseContent, List<CAttendanceDuration>>() {

			@Override
			public List<CAttendanceDuration> getContentObject(CGetDataForGraphOfAttendanceResponseContent content) {
				return content.getDataForGraph();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_DATA_FOR_ATTENDANCE), requestEntity, type); // CTimesheetController.getDataForGraphOfAttendance()
	}

	@Override
	public CGetInfoForMobileTimerResponseContent getInfoForMobileTimer(Boolean countToday) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CGetInfoForMobileTimerRequest requestEntity = new CGetInfoForMobileTimerRequest();
		requestEntity.setCountToday(countToday);

		Type type = new TypeToken<CResponse<CGetInfoForMobileTimerResponseContent>>() {
		}.getType();
		AServiceCall<CGetInfoForMobileTimerRequest, CGetInfoForMobileTimerResponseContent, CGetInfoForMobileTimerResponseContent> call = new AServiceCall<CGetInfoForMobileTimerRequest, CGetInfoForMobileTimerResponseContent, CGetInfoForMobileTimerResponseContent>() {

			@Override
			public CGetInfoForMobileTimerResponseContent getContentObject(CGetInfoForMobileTimerResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_INFO_FOR_MOBILE_TIMER), requestEntity, type); // CTimesheetController.getInfoForMobileTimer()
	}

	@Override
	public CGetSumAndAverageTimeResponseContent getSumAndAverageTime(Calendar dateFrom, Calendar dateTo) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CGetSumAndAverageTimeRequest requestEntity = new CGetSumAndAverageTimeRequest();
		requestEntity.setDateFrom(dateFrom);
		requestEntity.setDateTo(dateTo);

		Type type = new TypeToken<CResponse<CGetSumAndAverageTimeResponseContent>>() {
		}.getType();
		AServiceCall<CGetSumAndAverageTimeRequest, CGetSumAndAverageTimeResponseContent, CGetSumAndAverageTimeResponseContent> call = new AServiceCall<CGetSumAndAverageTimeRequest, CGetSumAndAverageTimeResponseContent, CGetSumAndAverageTimeResponseContent>() {

			@Override
			public CGetSumAndAverageTimeResponseContent getContentObject(CGetSumAndAverageTimeResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_SUM_AND_AVERAGE_TIME), requestEntity, type); // CTimesheetController.getSumAndAverageTime()
	}

	@Override
	public CGetSumAndAverageTimeForUsersResponseContent getSumAndAverageTimeForUsers(CSubrodinateTimeStampBrwFilterCriteria filter) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetSumAndAverageTimeForUsersRequest requestEntity = new CGetSumAndAverageTimeForUsersRequest(filter);

		Type type = new TypeToken<CResponse<CGetSumAndAverageTimeForUsersResponseContent>>() {
		}.getType();
		AServiceCall<CGetSumAndAverageTimeForUsersRequest, CGetSumAndAverageTimeForUsersResponseContent, CGetSumAndAverageTimeForUsersResponseContent> call = new AServiceCall<CGetSumAndAverageTimeForUsersRequest, CGetSumAndAverageTimeForUsersResponseContent, CGetSumAndAverageTimeForUsersResponseContent>() {

			@Override
			public CGetSumAndAverageTimeForUsersResponseContent getContentObject(CGetSumAndAverageTimeForUsersResponseContent content) {
				return content;
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_SUM_AND_AVERAGE_TIME_USERS), requestEntity, type); // CTimesheetController.getSumAndAverageTimeForUsers()
	}

	@Override
	public List<CStatsRecord> getDataForGraphOfStats(CStatsFilter filter) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CStatsRequest requestEntity = new CStatsRequest(filter);

		Type type = new TypeToken<CResponse<CGetDataForStatsResponseContent>>() {
		}.getType();
		AServiceCall<CStatsRequest, CGetDataForStatsResponseContent, List<CStatsRecord>> call = new AServiceCall<CStatsRequest, CGetDataForStatsResponseContent, List<CStatsRecord>>() {

			@Override
			public List<CStatsRecord> getContentObject(CGetDataForStatsResponseContent content) {
				return content.getDataForGraph();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_DATA_FOR_GRAPH_OF_STATS), requestEntity, type); // CTimesheetController.getDataForGraphOfStats()
	}

	@Override
	public CLockRecord split(CTimeStampRecord record, Date splitTime) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CSplitTimestampRequest splitTimeRequest = new CSplitTimestampRequest(record, splitTime);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CSplitTimestampRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CSplitTimestampRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.TIMESHEET_SPLIT), splitTimeRequest, type); // CTimesheetController.split()
	}
}
