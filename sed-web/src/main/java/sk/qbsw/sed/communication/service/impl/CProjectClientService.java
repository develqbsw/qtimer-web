package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.codelist.CProjectRecord;
import sk.qbsw.sed.client.model.codelist.CResultProjectsGroups;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.request.CModifyProjectRequest;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CProjectRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.client.response.CResultProjectsGroupsResponseContent;
import sk.qbsw.sed.communication.http.CHttpGetRequest;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IProjectClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.fw.utils.CCacheUtils;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CProjectClientService extends AClientService implements IProjectClientService {

	@Autowired
	CCacheUtils cache;

	/**
	 * @deprecated
	 */
	@Deprecated
	public CProjectClientService() {
		super(CApiUrl.PROJECT);
	}

	@Override
	public List<CCodeListRecord> getValidRecordsForUserCached(Long userId) throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.PROJECT_GET_VALID_RECORDS), null, type); // CProjectController.getValidRecords()
	}

	@Override
	public CResultProjectsGroups getAllRecordsWithGroups() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CResultProjectsGroupsResponseContent>>() {
		}.getType();
		AServiceCall<Void, CResultProjectsGroupsResponseContent, CResultProjectsGroups> call = new AServiceCall<Void, CResultProjectsGroupsResponseContent, CResultProjectsGroups>() {

			@Override
			public CResultProjectsGroups getContentObject(CResultProjectsGroupsResponseContent content) {
				return content.getResultProjectsGroups();
			}
		};

		return call.call(request, getUrl(CApiUrl.PROJECT_ALL_RECORDS_WITH_GROUPS), null, type); // CProjectController.getAllRecordsWithGroups()
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
		return call.call(request, getUrl(CApiUrl.PROJECT_GET_ALL_RECORDS), null, type); // CProjectController.getAllRecords()
	}

	@Override
	public CProjectRecord getDetail(Long projectId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CProjectRecordResponseContent>>() {
		}.getType();
		AServiceCall<Long, CProjectRecordResponseContent, CProjectRecord> call = new AServiceCall<Long, CProjectRecordResponseContent, CProjectRecord>() {

			@Override
			public CProjectRecord getContentObject(CProjectRecordResponseContent content) {
				return content.getProjectRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.PROJECT_GET_DETAIL), projectId, type); // CProjectController.getDetail()
	}

	@Override
	public CLockRecord add(CProjectRecord record) throws CBussinessDataException {

		cache.deleteCacheForOrg(CCacheUtils.CACHE_PROJECT);

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CProjectRecord, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CProjectRecord, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.PROJECT_ADD), record, type); // CProjectController.add()
	}

	@Override
	public CLockRecord modify(CProjectRecord newRecord) throws CBussinessDataException {
		cache.deleteCacheForOrg(CCacheUtils.CACHE_PROJECT);

		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyProjectRequest requestEntity = new CModifyProjectRequest();
		requestEntity.setId(newRecord.getId());
		requestEntity.setNewRecord(newRecord);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CModifyProjectRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CModifyProjectRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.PROJECT_MODIFY), requestEntity, type); // CProjectController.modify()

	}

}
