package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.Date;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.model.request.CRequestRecord;
import sk.qbsw.sed.client.request.CAddRequestRequest;
import sk.qbsw.sed.client.request.CHomeOfficePermissionIntervalRequest;
import sk.qbsw.sed.client.request.CHomeOfficePermissionRequest;
import sk.qbsw.sed.client.request.CModifyRequestRequest;
import sk.qbsw.sed.client.request.CRejectRequestFromEmailRequest;
import sk.qbsw.sed.client.request.CRequestIdRequest;
import sk.qbsw.sed.client.response.CBooleanResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CRequestRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IRequestClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CRequestClientService extends AClientService implements IRequestClientService {

	/**
	 * @deprecated
	 */
	@Deprecated
	public CRequestClientService() {
		super(CApiUrl.REQUEST);
	}

	@Override
	public CLockRecord add(CRequestRecord requestRecord, boolean ignoreDuplicity) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CAddRequestRequest requestEntity = new CAddRequestRequest(requestRecord.getTypeId(), requestRecord, ignoreDuplicity);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CAddRequestRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CAddRequestRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_ADD), requestEntity, type); // CRequestController.add()
	}

	@Override
	public CRequestRecord getDetail(Long requestId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CRequestIdRequest requestEntity = new CRequestIdRequest(requestId);

		Type type = new TypeToken<CResponse<CRequestRecordResponseContent>>() {
		}.getType();
		AServiceCall<CRequestIdRequest, CRequestRecordResponseContent, CRequestRecord> call = new AServiceCall<CRequestIdRequest, CRequestRecordResponseContent, CRequestRecord>() {

			@Override
			public CRequestRecord getContentObject(CRequestRecordResponseContent content) {
				return content.getRequset();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_GET_DETAIL), requestEntity, type); // CRequestController.getDetail()
	}

	@Override
	public CLockRecord modify(CRequestRecord requestRecord, boolean ignoreDuplicity) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyRequestRequest requestEntity = new CModifyRequestRequest(requestRecord.getId(), requestRecord, ignoreDuplicity);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CModifyRequestRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CModifyRequestRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_MODIFY), requestEntity, type); // CRequestController.modify()
	}

	@Override
	public CLockRecord cancel(Long requestId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CRequestIdRequest requestEntity = new CRequestIdRequest(requestId);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CRequestIdRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CRequestIdRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_CANCEL), requestEntity, type); // CRequestController.cancel()
	}

	@Override
	public CLockRecord approve(Long requestId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CRequestIdRequest requestEntity = new CRequestIdRequest(requestId);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CRequestIdRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CRequestIdRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_APPROVE), requestEntity, type); // CRequestController.approve()

	}

	@Override
	public CLockRecord reject(Long requestId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CRequestIdRequest requestEntity = new CRequestIdRequest(requestId);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CRequestIdRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CRequestIdRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_REJECT), requestEntity, type); // CRequestController.reject()

	}

	@Override
	public Boolean approveRequestFromEmail(String requestId, String requestCode) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(null);
		CRejectRequestFromEmailRequest requestEntity = new CRejectRequestFromEmailRequest(requestId, requestCode);

		Type type = new TypeToken<CResponse<CBooleanResponseContent>>() {
		}.getType();
		AServiceCall<CRejectRequestFromEmailRequest, CBooleanResponseContent, Boolean> call = new AServiceCall<CRejectRequestFromEmailRequest, CBooleanResponseContent, Boolean>() {

			@Override
			public Boolean getContentObject(CBooleanResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_APPROVE_FROM_EMAIL), requestEntity, type); // CRequestController.approveRequestFromEmail()
	}

	@Override
	public Boolean rejectRequestFromEmail(String requestId, String requestCode) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(null);
		CRejectRequestFromEmailRequest requestEntity = new CRejectRequestFromEmailRequest(requestId, requestCode);

		Type type = new TypeToken<CResponse<CBooleanResponseContent>>() {
		}.getType();
		AServiceCall<CRejectRequestFromEmailRequest, CBooleanResponseContent, Boolean> call = new AServiceCall<CRejectRequestFromEmailRequest, CBooleanResponseContent, Boolean>() {

			@Override
			public Boolean getContentObject(CBooleanResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_REJECT_FROM_EMAIL), requestEntity, type); // CRequestController.rejectRequestFromEmail()

	}

	@Override
	public Boolean isAllowedHomeOfficeForToday(Long userId, Date date) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CHomeOfficePermissionRequest requestEntity = new CHomeOfficePermissionRequest(userId, date);

		Type type = new TypeToken<CResponse<CBooleanResponseContent>>() {
		}.getType();
		AServiceCall<CHomeOfficePermissionRequest, CBooleanResponseContent, Boolean> call = new AServiceCall<CHomeOfficePermissionRequest, CBooleanResponseContent, Boolean>() {

			@Override
			public Boolean getContentObject(CBooleanResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_HAS_USER_ALLOWED_HOME_OFFICE_FOR_TODAY), requestEntity, type); // CRequestController.isAllowedHomeOfficeForToday()
	}

	@Override
	public Boolean isAllowedHomeOfficeInInterval(Long userId, Long clientId, Date dateFrom, Date dateTo) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		CHomeOfficePermissionIntervalRequest requestEntity = new CHomeOfficePermissionIntervalRequest(userId, clientId, dateFrom, dateTo);

		Type type = new TypeToken<CResponse<CBooleanResponseContent>>() {
		}.getType();
		AServiceCall<CHomeOfficePermissionIntervalRequest, CBooleanResponseContent, Boolean> call = new AServiceCall<CHomeOfficePermissionIntervalRequest, CBooleanResponseContent, Boolean>() {

			@Override
			public Boolean getContentObject(CBooleanResponseContent content) {
				return content.getValue();
			}
		};
		return call.call(request, getUrl(CApiUrl.REQUEST_HAS_USER_ALLOWED_HOME_OFFICE_IN_INTERVAL), requestEntity, type); // CRequestController.isAllowedHomeOfficeInInterval()
	}
}
