package sk.qbsw.sed.communication.service.impl;

import java.lang.reflect.Type;
import java.util.List;

import org.apache.wicket.Session;
import org.springframework.stereotype.Service;

import com.google.gson.reflect.TypeToken;

import sk.qbsw.sed.client.CApiUrl;
import sk.qbsw.sed.client.model.codelist.CHolidayRecord;
import sk.qbsw.sed.client.model.lock.CLockRecord;
import sk.qbsw.sed.client.request.CGetClientRecordsForTheYearRequest;
import sk.qbsw.sed.client.request.CModifyHolidayRequest;
import sk.qbsw.sed.client.response.CHolidayRecordListResponseContent;
import sk.qbsw.sed.client.response.CHolidayRecordResponseContent;
import sk.qbsw.sed.client.response.CLockRecordResponseContent;
import sk.qbsw.sed.client.response.CResponse;
import sk.qbsw.sed.communication.http.CHttpPostRequest;
import sk.qbsw.sed.communication.model.AServiceCall;
import sk.qbsw.sed.communication.service.IHolidayClientService;
import sk.qbsw.sed.fw.exception.CBussinessDataException;
import sk.qbsw.sed.utils.CInvokerInfoUtils;

@Service
public class CHolidayClientService extends AClientService implements IHolidayClientService {
	
	/**
	 * @deprecated
	 */
	@Deprecated
	public CHolidayClientService() {
		super(CApiUrl.HOLIDAY);
	}

	@Override
	public CHolidayRecord getDetail(Long projectId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CHolidayRecordResponseContent>>() {
		}.getType();
		AServiceCall<Long, CHolidayRecordResponseContent, CHolidayRecord> call = new AServiceCall<Long, CHolidayRecordResponseContent, CHolidayRecord>() {

			@Override
			public CHolidayRecord getContentObject(CHolidayRecordResponseContent content) {
				return content.getHolidayRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.HOLIDAY_GET_DETAIL), projectId, type); // CHolidayController.getDetail()
	}

	@Override
	public CLockRecord modify(CHolidayRecord newRecord) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CModifyHolidayRequest requestEntity = new CModifyHolidayRequest(newRecord.getId(), newRecord);

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CModifyHolidayRequest, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CModifyHolidayRequest, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.HOLIDAY_MODIFY), requestEntity, type); // CHolidayController.modify()
	}

	@Override
	public CLockRecord add(CHolidayRecord record) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CLockRecordResponseContent>>() {
		}.getType();
		AServiceCall<CHolidayRecord, CLockRecordResponseContent, CLockRecord> call = new AServiceCall<CHolidayRecord, CLockRecordResponseContent, CLockRecord>() {

			@Override
			public CLockRecord getContentObject(CLockRecordResponseContent content) {
				return content.getLockRecord();
			}
		};
		return call.call(request, getUrl(CApiUrl.HOLIDAY_ADD), record, type); // CHolidayController.add()
	}

	@Override
	public List<CHolidayRecord> cloneRecordsForNextYear(Long clientId) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));

		Type type = new TypeToken<CResponse<CHolidayRecordListResponseContent>>() {
		}.getType();
		AServiceCall<Long, CHolidayRecordListResponseContent, List<CHolidayRecord>> call = new AServiceCall<Long, CHolidayRecordListResponseContent, List<CHolidayRecord>>() {

			@Override
			public List<CHolidayRecord> getContentObject(CHolidayRecordListResponseContent content) {
				return content.getList();
			}
		};
		return call.call(request, getUrl(CApiUrl.HOLIDAY_CLONE_FOR_NEXT_YEAR), clientId, type); // CHolidayController.cloneCurrentYearClientRecordsForNextYear()
	}

	@Override
	public List<CHolidayRecord> getClientRecordsForTheYear(Long clientId, Integer selectedYearDate) throws CBussinessDataException {
		CHttpPostRequest request = new CHttpPostRequest(CInvokerInfoUtils.createInfo(Session.get()));
		CGetClientRecordsForTheYearRequest requestEntity = new CGetClientRecordsForTheYearRequest(clientId, selectedYearDate);

		Type type = new TypeToken<CResponse<CHolidayRecordListResponseContent>>() {
		}.getType();
		AServiceCall<CGetClientRecordsForTheYearRequest, CHolidayRecordListResponseContent, List<CHolidayRecord>> call = new AServiceCall<CGetClientRecordsForTheYearRequest, CHolidayRecordListResponseContent, List<CHolidayRecord>>() {

			@Override
			public List<CHolidayRecord> getContentObject(CHolidayRecordListResponseContent content) {
				return content.getList();
			}
		};
		return call.call(request, getUrl(CApiUrl.HOLIDAY_GET_CLIENT_RECORDS_FOR_THE_YEAR), requestEntity, type); // CHolidayController.getClientRecordsForTheYear()
	}
}
