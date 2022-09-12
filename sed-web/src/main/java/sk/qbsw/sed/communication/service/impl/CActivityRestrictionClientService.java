package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.restriction.CActivityIntervalData;
import sk.qbsw.sed.client.model.restriction.CEmployeeActivityLimitsData;
import sk.qbsw.sed.client.model.restriction.CGroupsAIData;
import sk.qbsw.sed.client.request.CGetValidActivityGroupsRequest;
import sk.qbsw.sed.client.response.CActivityIntervalDataResponseContent;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CEmployeeActivityLimitsDataResponseContent;
import sk.qbsw.sed.client.response.CGroupsAIDataResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IActivityRestrictionClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author moravcik
 *
 */
@Service
public class CActivityRestrictionClientService extends AClientService implements IActivityRestrictionClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CActivityRestrictionClientService() {
		super(CApiUrl.ACTIVITY_RESTRICTION);
	}

	@Override
	public CGroupsAIData getGroupDetail(Long activityGroupId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CGroupsAIDataResponseContent>>() {
		}.getType();
		AServiceCall<Long, CGroupsAIDataResponseContent, CGroupsAIData> call = new AServiceCall<Long, CGroupsAIDataResponseContent, CGroupsAIData>() {

			@Override
			public CGroupsAIData getContentObject(CGroupsAIDataResponseContent content) {
				return content.getGroupsAIData();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_RESTRICTION_GROUP_GET_DETAIL), activityGroupId, type); // CActivityTimeLimitsController.loadGroupDetail()
	}

	@Override
	public CLockRecord saveGroup(CGroupsAIData record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CGroupsAIData, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CGroupsAIData, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_RESTRICTION_GROUP_SAVE), record, type); // CActivityTimeLimitsController.saveGroup()
	}

	@Override
	public List<CCodeListRecord> getValidActivityGroups(Long clientId, Long activityId, Boolean validFlag) throws CBussinessDataException {

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetValidActivityGroupsRequest requestEntity = new CGetValidActivityGroupsRequest();
		requestEntity.setClientId(clientId);
		requestEntity.setActivityId(activityId);
		requestEntity.setValidFlag(validFlag);

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<CGetValidActivityGroupsRequest, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<CGetValidActivityGroupsRequest, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_RESTRICTION_GET_VALID_ACTIVITY_GROUPS), requestEntity, type); // CActivityTimeLimitsController.getValidActivityGroups()
	}

	@Override
	public CActivityIntervalData getIntervalDetail(Long activityId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CActivityIntervalDataResponseContent>>() {
		}.getType();
		AServiceCall<Long, CActivityIntervalDataResponseContent, CActivityIntervalData> call = new AServiceCall<Long, CActivityIntervalDataResponseContent, CActivityIntervalData>() {

			@Override
			public CActivityIntervalData getContentObject(CActivityIntervalDataResponseContent content) {
				return content.getActivityIntervalData();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_RESTRICTION_INTERVAL_GET_DETAIL), activityId, type); // CActivityTimeLimitsController.loadIntervalDetail()
	}

	@Override
	public CLockRecord saveInterval(CActivityIntervalData record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CActivityIntervalData, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CActivityIntervalData, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_RESTRICTION_INTERVAL_SAVE), record, type); // CActivityTimeLimitsController.saveInterval()
	}

	@Override
	public CEmployeeActivityLimitsData getEmployeeLimitsDetail(Long employeeId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CEmployeeActivityLimitsDataResponseContent>>() {
		}.getType();
		AServiceCall<Long, CEmployeeActivityLimitsDataResponseContent, CEmployeeActivityLimitsData> call = new AServiceCall<Long, CEmployeeActivityLimitsDataResponseContent, CEmployeeActivityLimitsData>() {

			@Override
			public CEmployeeActivityLimitsData getContentObject(CEmployeeActivityLimitsDataResponseContent content) {
				return content.getEmployeeActivityLimitsData();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_RESTRICTION_LOAD_EMPLOYEE_LIMITS_DETAIL), employeeId, type); // CActivityTimeLimitsController.loadEmployeeLimitsDetail()
	}

	@Override
	public CLockRecord saveEmployeeLimits(CEmployeeActivityLimitsData data) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CEmployeeActivityLimitsData, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CEmployeeActivityLimitsData, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_RESTRICTION_SAVE_EMPLOYEE_LIMITS), data, type); // CActivityTimeLimitsController.saveEmployeeLimits()
	}
}
