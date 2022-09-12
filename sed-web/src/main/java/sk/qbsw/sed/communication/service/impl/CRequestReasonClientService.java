package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.restriction.CRequestReasonData;
import sk.qbsw.sed.client.model.restriction.CRequestReasonListsData;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CRequestReasonDataResponseContent;
import sk.qbsw.sed.client.response.CRequestReasonListsDataResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpGetRequest;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IRequestReasonClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CRequestReasonClientService extends AClientService implements IRequestReasonClientService {
	
	/**
	 * @deprecated
	 */
	@Deprecated
	public CRequestReasonClientService() {
		super(CApiUrl.REQUEST_REASON);
	}

	@Override
	public CRequestReasonData getDetail(Long recordId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CRequestReasonDataResponseContent>>() {
		}.getType();
		AServiceCall<Long, CRequestReasonDataResponseContent, CRequestReasonData> call = new AServiceCall<Long, CRequestReasonDataResponseContent, CRequestReasonData>() {

			@Override
			public CRequestReasonData getContentObject(CRequestReasonDataResponseContent content) {
				return content.getRequestReasonData();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_REASON_GET_DETAIL), recordId, type); // CRequestReasonController.getDetail()
	}

	@Override
	public CLockRecord save(CRequestReasonData model) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CRequestReasonData requestEntity = model;

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CRequestReasonData, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CRequestReasonData, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_REASON_SAVE), requestEntity, type); // CRequestReasonController.save()
	}

	@Override
	public CRequestReasonListsData getReasonLists(Long clientId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CRequestReasonListsDataResponseContent>>() {
		}.getType();
		AServiceCall<Long, CRequestReasonListsDataResponseContent, CRequestReasonListsData> call = new AServiceCall<Long, CRequestReasonListsDataResponseContent, CRequestReasonListsData>() {

			@Override
			public CRequestReasonListsData getContentObject(CRequestReasonListsDataResponseContent content) {
				return content.getRequestReasonListsData();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_REASON_LIST), clientId, type); // CRequestReasonController.getReasonLists()
	}

	@Override
	public List<CCodeListRecord> getReasonListsForListbox() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_REASON_LIST_FOR_LISTBOX), null, type); // CRequestReasonController.getReasonListsForListbox()
	}
}
