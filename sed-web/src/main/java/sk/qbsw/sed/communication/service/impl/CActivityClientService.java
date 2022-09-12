package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CActivityRecord;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.request.CModifyActivityRequest;
import sk.qbsw.sed.client.response.CActivityRecordResponseContent;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpGetRequest;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IActivityClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.utils.CCacheUtils;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CActivityClientService extends AClientService implements IActivityClientService {

	@Autowired
	CCacheUtils cache;

	/**
	 * @deprecated
	 */
	@Deprecated
	public CActivityClientService() {
		super(CApiUrl.ACTIVITY);
	}

	@Override
	public List<CCodeListRecord> getValidRecordsForUser(Long userId) throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_GET_VALID_RECORDS), null, type); // CActivityController.getValidRecords()
	}

	@Override
	public List<CCodeListRecord> getValidWorkingRecordsForUser(Long userId) throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_GET_VALID_WORKING_RECORDS_FOR_USER), null, type); // CActivityController.getValidWorkingRecordsForUser()

	}

	@Override
	public List<CCodeListRecord> getValidRecordsForLimits() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_GET_VALID_RECORDS_FOR_LIMITS), null, type); // CActivityController.getValidRecordsForLimits()
	}

	@Override
	public List<CCodeListRecord> getAllRecords() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_GET_ALL_RECORDS), null, type); // CActivityController.getAllRecords()
	}

	/**
	 * @see IActivityClientService#getDetail(Long)
	 */
	@Override
	public CActivityRecord getDetail(Long activityId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CActivityRecordResponseContent>>() {
		}.getType();
		AServiceCall<Long, CActivityRecordResponseContent, CActivityRecord> call = new AServiceCall<Long, CActivityRecordResponseContent, CActivityRecord>() {

			@Override
			public CActivityRecord getContentObject(CActivityRecordResponseContent content) {
				return content.getActivityRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_GET_DETAIL), activityId, type); // CActivityController.getDetail()

	}

	/**
	 * @see IActivityClientService#add(CActivityRecord)
	 */
	@Override
	public CLockRecord add(CActivityRecord record) throws CBussinessDataException {
		cache.deleteCacheForOrg(CCacheUtils.CACHE_ACTIVITY);
		cache.deleteCacheForOrg(CCacheUtils.CACHE_ACTIVITY_WORKING);

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CActivityRecord, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CActivityRecord, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_ADD), record, type); // CActivityController.add()
	}

	/**
	 * @see IActivityClientService#modify(CActivityRecord)
	 */
	@Override
	public CLockRecord modify(CActivityRecord newRecord) throws CBussinessDataException {
		cache.deleteCacheForOrg(CCacheUtils.CACHE_ACTIVITY);
		cache.deleteCacheForOrg(CCacheUtils.CACHE_ACTIVITY_WORKING);

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyActivityRequest requestEntity = new CModifyActivityRequest(newRecord.getId(), newRecord);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CModifyActivityRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CModifyActivityRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.ACTIVITY_MODIFY), requestEntity, type); // CActivityController.modify()
	}
}
