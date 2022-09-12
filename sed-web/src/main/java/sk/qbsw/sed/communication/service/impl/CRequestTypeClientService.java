package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CCodeListRecord;
import sk.qbsw.sed.client.response.CCodeListRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpGetRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IRequestTypeClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CRequestTypeClientService extends AClientService implements IRequestTypeClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CRequestTypeClientService() {
		super(CApiUrl.REQUEST_TYPE);
	}

	@Override
	public List<CCodeListRecord> getValidRecords() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_TYPE_GET_VALID_RECORDS), null, type); // CRequestTypeController.getValidRecords()
	}

	@Override
	public List<CCodeListRecord> getValidRecordsForRequestReason() throws CBussinessDataException {
		CHttpGetRequest request = new CHttpGetRequest(CInvokerInfoUtils.createInfo(Session.get()));
		Type type = new TypeToken<CResponse<CCodeListRecordResponseContent>>() {
		}.getType();
		AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>> call = new AServiceCall<Void, CCodeListRecordResponseContent, List<CCodeListRecord>>() {

			@Override
			public List<CCodeListRecord> getContentObject(CCodeListRecordResponseContent content) {
				return content.getCodeListRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_TYPE_RECORDS_FOR_REQUEST_REASON), null, type); // CRequestTypeController.getValidRecordsForRequestReason()
	}
}
