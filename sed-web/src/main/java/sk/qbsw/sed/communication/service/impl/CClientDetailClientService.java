package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.detail.CClientDetailRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.response.CClientDetailRecordResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IClientDetailClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

/**
 * 
 * @author lobb
 *
 */
@Service
public class CClientDetailClientService extends AClientService implements IClientDetailClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CClientDetailClientService() {
		super(CApiUrl.CLIENT_DETAIL);
	}

	@Override
	public CClientDetailRecord getDetail(Long clientId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CClientDetailRecordResponseContent>>() {
		}.getType();
		AServiceCall<Long, CClientDetailRecordResponseContent, CClientDetailRecord> call = new AServiceCall<Long, CClientDetailRecordResponseContent, CClientDetailRecord>() {

			@Override
			public CClientDetailRecord getContentObject(CClientDetailRecordResponseContent content) {
				return content.getRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.CLIENT_DETAIL_GET_DETAIL), clientId, type); // CClientController.getDetail()

	}

	@Override
	public CLockRecord updateDetail(CClientDetailRecord clientDetail) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CClientDetailRecord, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CClientDetailRecord, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.CLIENT_DETAIL_UPDATE_DETAIL), clientDetail, type); // CClientController.updateDetail()
	}
}
